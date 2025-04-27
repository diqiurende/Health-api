package com.example.health.api.front.service.impl;

import cn.felord.payment.PayException;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.WechatResponseEntity;
import cn.felord.payment.wechat.v3.model.*;
import cn.hutool.core.util.IdUtil;
import com.example.health.api.front.service.PaymentService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private WechatApiProvider wechatApiProvider;

    @Override
    public ObjectNode unifiedOrder(String outTradeNo, int total,
                                   String desc, String notifyUrl,
                                   String timeExpire) {
        PayParams params = new PayParams();

        Amount amount = new Amount();
        //amount.setTotal(total);
        amount.setTotal(1);  //设置付款金额为1分钱
        params.setAmount(amount);

        params.setOutTradeNo(outTradeNo);
        params.setDescription(desc);
        params.setNotifyUrl(notifyUrl);

        //设置场景信息
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp("127.0.0.1"); //终端设备（浏览器）的IP地址
        params.setSceneInfo(sceneInfo);

        if (timeExpire != null) {
            params.setTimeExpire(OffsetDateTime.parse(timeExpire));
        }

        WechatResponseEntity<ObjectNode> response = wechatApiProvider.directPayApi("health-vue").nativePay(params);
        if (response.is2xxSuccessful()) {
            ObjectNode json = response.getBody();
            return json;
        } else {
            log.error("创建微信支付订单失败", response.getBody());
            throw new PayException("创建微信支付订单失败");
        }
    }

    @Override
    public String searchPaymentResult(String outTradeNo) {
        // 构建查询参数对象，用于查询交易信息（支持用订单号或微信交易ID）
        TransactionQueryParams params = new TransactionQueryParams();
        // 设置要查询的支付单号
        params.setTransactionIdOrOutTradeNo(outTradeNo);

        // 调用微信支付封装的接口，查询交易状态（通过商户订单号查询）
        WechatResponseEntity<ObjectNode> entity =
                wechatApiProvider.directPayApi("health-vue").queryTransactionByOutTradeNo(params);

        // 判断HTTP响应是否成功（状态码是否为2xx）
        if (!entity.is2xxSuccessful()) {
            // 如果接口调用失败，记录错误日志
            log.error("查询付款失败", entity.getBody());
            return null;
        }

        // 获取接口返回的响应体（JSON格式）
        ObjectNode body = entity.getBody();

        // 从响应体中提取交易状态（trade_state）
        String status = body.get("trade_state").textValue();

        // 如果支付状态为“SUCCESS”，说明付款已完成
        if ("SUCCESS".equals(status)) {
            // 获取微信返回的交易ID（流水号）
            String transactionId = body.get("transaction_id").textValue();
            return transactionId;
        }

        // 如果未支付或支付失败，则返回null
        return null;

    }


    @Override
    public String refund(String transactionId, Integer refund, Integer total, String notifyUrl) {
        RefundParams params = new RefundParams();
        params.setTransactionId(transactionId);
        String outRefundNo = IdUtil.simpleUUID().toUpperCase(); //生成退款流水号
        params.setOutRefundNo(outRefundNo);
        params.setNotifyUrl(notifyUrl);

        RefundParams.RefundAmount amount = new RefundParams.RefundAmount();
        amount.setRefund(refund); //退款金额
        amount.setTotal(total); //订单金额
        amount.setCurrency("CNY");
        params.setAmount(amount);

        WechatResponseEntity<ObjectNode> entity = wechatApiProvider.directPayApi("health-vue").refund(params);
        if (!entity.is2xxSuccessful()) {
            log.error("退款失败", entity.getBody());
            return null;
        }
        ObjectNode body = entity.getBody();
        //判断退款中状态
        if ("PROCESSING".equals(body.get("status").textValue())) {
            return outRefundNo;
        }
        return null;
    }
}
