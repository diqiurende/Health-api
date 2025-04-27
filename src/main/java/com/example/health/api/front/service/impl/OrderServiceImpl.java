package com.example.health.api.front.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.dao.GoodsMapper;
import com.example.health.api.db.dao.GoodsSnapshotDao;
import com.example.health.api.db.dao.OrderMapper;
import com.example.health.api.db.pojo.GoodsSnapshotEntity;
import com.example.health.api.db.pojo.OrderEntity;
import com.example.health.api.front.service.GoodsService;
import com.example.health.api.front.service.OrderService;
import com.example.health.api.front.service.PaymentService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service("FrontOrderServiceImpl")
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private PaymentService paymentService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;

    private String paymentNotifyUrl = "/front/order/paymentCallback";

    private String refundNotifyUrl = "/front/order/refundCallback";


    @Override
    @Transactional
    public HashMap createPayment(Map param) {
        int goodsId = MapUtil.getInt(param, "goodsId");
        Integer number = MapUtil.getInt(param, "number");
        int customerId = MapUtil.getInt(param, "customerId");

        //如果当天该客户有10个以上未付款订单或者5个以上退款订单，当天就无法下单
        boolean illegal = orderMapper.searchIllegalCountInDay(customerId);
        if (illegal) {
            return null;
        }
        // 查找商品详情信息
        HashMap map = goodsMapper.searchSnapshotNeededById(goodsId);
        String goodsCode = MapUtil.getStr(map, "code");
        String goodsTitle = MapUtil.getStr(map, "title");
        String goodsDescription = MapUtil.getStr(map, "description");
        String goodsImage = MapUtil.getStr(map, "image");
        BigDecimal goodsInitialPrice = new BigDecimal(MapUtil.getStr(map, "initialPrice"));
        BigDecimal goodsCurrentPrice = new BigDecimal(MapUtil.getStr(map, "currentPrice"));
        String goodsRuleName = MapUtil.getStr(map, "ruleName");
        String goodsRule = MapUtil.getStr(map, "rule");
        String goodsType = MapUtil.getStr(map, "type");
        String goodsMd5 = MapUtil.getStr(map, "md5");

        String temp = MapUtil.getStr(map, "checkup_1");
        List<Map> goodsCheckup_1 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_2");
        List<Map> goodsCheckup_2 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_3");
        List<Map> goodsCheckup_3 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup_4");
        List<Map> goodsCheckup_4 = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "checkup");
        List<Map> goodsCheckup = temp != null ? JSONUtil.parseArray(temp).toList(Map.class) : null;

        temp = MapUtil.getStr(map, "tag");
        List<String> goodsTag = temp != null ? JSONUtil.parseArray(temp).toList(String.class) : null;

        // 计算订单金额

        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("number", number.intValue());
        context.put("price", goodsCurrentPrice.toString());

        String amount = null;
        if (goodsRule != null) {
            try {
                //执行规则引擎计算支付结果
                amount = runner.execute(goodsRule, context, null, true, false).toString();
            } catch (Exception e) {
                throw new HealthException("规则引擎计算价格失败", e);
            }
        } else {
            amount = goodsCurrentPrice.multiply(new BigDecimal(number)).toString();
        }



        // 创建微信支付单
        //把付款金额从元转换成分
        int total = NumberUtil.mul(amount, "100").intValue();
        //生成商品订单流水号
        String outTradeNo = IdUtil.simpleUUID().toUpperCase();

        //付款过期时间为20分钟
        DateTime dateTime = new DateTime();
        dateTime.offset(DateField.MINUTE, 20);
        String timeExpire = dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        //创建支付单
        ObjectNode objectNode = paymentService.unifiedOrder(outTradeNo, total, "购买体检套餐", paymentNotifyUrl, timeExpire);

        String codeUrl = objectNode.get("code_url").textValue();


        //把支付单的codeUrl缓存到Redis中，检测未付款订单是否可以付款
        String key = "codeUrl_" + customerId + "_" + outTradeNo;
        redisTemplate.opsForValue().set(key, codeUrl);
        redisTemplate.expireAt(key, dateTime); //设置缓存过期时间


        // 如果不存在该商品快照，就创建快照记录
        if (codeUrl != null) {
            //根据商品md5查询快照
            String _id = goodsSnapshotDao.hasGoodsSnapshot(goodsMd5);
            //如果不存在就创建快照
            if (_id == null) {
                GoodsSnapshotEntity entity = new GoodsSnapshotEntity();
                entity.setId(goodsId);
                entity.setCode(goodsCode);
                entity.setTitle(goodsTitle);
                entity.setDescription(goodsDescription);
                entity.setCheckup_1(goodsCheckup_1);
                entity.setCheckup_2(goodsCheckup_2);
                entity.setCheckup_3(goodsCheckup_3);
                entity.setCheckup_4(goodsCheckup_4);
                entity.setCheckup(goodsCheckup);
                entity.setImage(goodsImage);
                entity.setInitialPrice(goodsInitialPrice);
                entity.setCurrentPrice(goodsCurrentPrice);
                entity.setType(goodsType);
                entity.setTag(goodsTag);
                entity.setRuleName(goodsRuleName);
                entity.setRule(goodsRule);
                entity.setMd5(goodsMd5);

                //保存商品快照，拿到快照的主键值
                _id = goodsSnapshotDao.insert(entity);
            }

            OrderEntity entity = new OrderEntity();
            entity.setCustomerId(customerId);
            entity.setGoodsId(goodsId);
            //关联商品快照
            entity.setSnapshotId(_id);

            entity.setGoodsTitle(goodsTitle);
            entity.setGoodsPrice(goodsCurrentPrice);
            entity.setNumber(number);
            entity.setAmount(new BigDecimal(amount));
            entity.setGoodsImage(goodsImage);
            entity.setGoodsDescription(goodsDescription);
            //保存订单流水号
            entity.setOutTradeNo(outTradeNo);
            //保存订单记录
            orderMapper.insert(entity);

            //url转为二维码base64编码
            QrConfig qrConfig = new QrConfig();
            qrConfig.setWidth(230);
            qrConfig.setHeight(230);
            qrConfig.setMargin(2);
            String qrCodeBase64 = QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");

            //更新商品销量+1
            int rows = goodsMapper.updateSalesVolume(goodsId);
            if (rows != 1) {
                throw new HealthException("更新商品销量失败");
            }
            return new HashMap() {{
                put("qrCodeBase64", qrCodeBase64);
                put("outTradeNo", outTradeNo);
            }};
        } else {
            log.error("创建支付订单失败", objectNode);
            throw new HealthException("创建支付订单失败");
        }
    }

    @Override
    @Transactional
    public boolean updatePayment(Map param) {
        int rows = orderMapper.updatePayment(param);
        if(rows==1){
            return true;
        }
        return false;
    }


    @Override
    public Integer searchCustomerId(String outTradeNo) {
        Integer customerId = orderMapper.searchCustomerId(outTradeNo);
        return customerId;
    }


    @Override
    @Transactional
    public boolean searchPaymentResult(String outTradeNo) {
        String transactionId = paymentService.searchPaymentResult(outTradeNo);
        // 如果交易ID不为空，说明该订单已支付成功
        if (transactionId != null) {
            // 更新订单支付状态，把交易ID也更新进去
            this.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);       // 商户系统订单流水号
                put("transactionId", transactionId); // 微信支付返回的交易支付号
            }});

            return true;  // 返回 true 表示处理成功
        } else {
            return false; // 如果没有查到交易ID，说明尚未支付或失败
        }
    }


    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = orderMapper.searchFrontOrderCount(param);
        if (count > 0) {
            list = orderMapper.searchFrontOrderByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }


    @Override
    @Transactional
    public boolean refund(Map param) {
        //先查询订单是否存在退款流水号，避免用户重复申请退款
        int id = MapUtil.getInt(param, "id");
        String outRefundNo = orderMapper.searchAlreadyRefund(id);
        //判断该订单是否申请退款了
        if (outRefundNo != null) {
            return false;
        }

        HashMap map = orderMapper.searchRefundNeeded(param);
        String transactionId = MapUtil.getStr(map, "transactionId");
        String amount = MapUtil.getStr(map, "amount");

//        int total = NumberUtil.mul(amount, "100").intValue();  //总金额
        int total=1;
        //int refund = total;   //退款金额
        int refund = 1;

        if (transactionId == null) {
            log.error("transactionId不能为空");
            return false;
        }
        //执行退款
        outRefundNo = paymentService.refund(transactionId, refund, total, refundNotifyUrl);
        param.put("outRefundNo", outRefundNo);
        if (outRefundNo != null) {
            //更新退款流水号和退款日期时间
            int rows = orderMapper.updateOutRefundNo(param);
            if (rows == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateRefundStatus(String outRefundNo) {
        int rows = orderMapper.updateRefundStatusByOutRefundNo(outRefundNo);
        return rows == 1;
    }
    @Override
    public boolean hasOwnOrder(Map param) {
        Integer id = orderMapper.hasOwnOrder(param);
        return id != null;
    }
}
