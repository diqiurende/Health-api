package com.example.health.api.front.controller;
import cn.dev33.satoken.annotation.SaCheckLogin;
//import cn.felord.payment.wechat.v3.WechatApiProvider;
//import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.config.sa_token.StpCustomerUtil;
import com.example.health.api.front.controller.form.*;
import com.example.health.api.front.service.GoodsService;
import com.example.health.api.front.service.OrderService;
import com.example.health.api.socket.WebSocketService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("FrontOrderController")
@RequestMapping("/front/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    @Resource
    private WechatApiProvider wechatApiProvider;

    @PostMapping("/createPayment")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R createPayment(@RequestBody @Valid CreatePaymentForm form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        Map param = BeanUtil.beanToMap(form);
        param.put("customerId", customerId);
        HashMap map = orderService.createPayment(param);
        if (map == null) {
            return R.ok().put("illegal", true);
        } else {
            return R.ok().put("illegal", false).put("result", map);
        }
    }


    @SneakyThrows
    @PostMapping("/paymentCallback")
    public Map paymentCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request) {
        // Step 1：读取请求体内容（微信发来的 JSON 数据）
        String body = request.getReader()
                .lines()
                .collect(Collectors.joining());
        // Step 2：封装微信的签名验证参数
        ResponseSignVerifyParams verifyParams = new ResponseSignVerifyParams();
        verifyParams.setWechatpaySerial(serial);
        verifyParams.setWechatpaySignature(signature);
        verifyParams.setWechatpayTimestamp(timestamp);
        verifyParams.setWechatpayNonce(nonce);
        verifyParams.setBody(body);



        // Step 3：调用微信SDK验签+回调处理
        return wechatApiProvider
                .callback("health-vue")
                .transactionCallback(verifyParams, data -> {
                    // 3.1 获取微信支付回调中的关键数据
                    String transactionId = data.getTransactionId(); // 微信支付订单号
                    String outTradeNo = data.getOutTradeNo();       // 商户系统订单流水号

                    // 3.2 业务逻辑：更新数据库中订单的支付状态
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("transactionId", transactionId);
                    updateMap.put("outTradeNo", outTradeNo);
                    boolean success = orderService.updatePayment(updateMap);

                    // 3.3 通知前端支付成功（ WebSocket 或推送）
                    if (success) {
                             log.debug("订单付款成功，已更新订单状态");
    //查询订单的customerId
    Integer customerId = orderService.searchCustomerId(outTradeNo);
            if (customerId == null) {
        log.error("没有查询到customerId");
    } else {
        //推送消息给前端页面
        JSONObject json = new JSONObject();
        json.set("result", true);
        WebSocketService.sendInfo(json.toString(), "customer_" + customerId.toString());
    }
                }
    else {
        log.error("订单付款成功，但是状态更新失败");
    }

                });
    }

    @PostMapping("/searchPaymentResult")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchPaymentResult(@Valid @RequestBody SearchPaymentResultForm form) {
        boolean bool = orderService.searchPaymentResult(form.getOutTradeNo());
        return R.ok().put("result", bool);
    }


    @PostMapping("/searchByPage")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchByPage(@RequestBody @Valid SearchOrderByPageForm form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        param.put("customerId", customerId);
        PageUtils pageUtils = orderService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }


    @PostMapping("/refund")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R refund(@RequestBody @Valid RefundForm form) {

        //获取登陆的id
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        //设置id
        form.setCustomerId(customerId);
        Map param = BeanUtil.beanToMap(form);
        boolean bool = orderService.refund(param);
        return R.ok().put("result", bool);
    }

    @SneakyThrows
    @PostMapping("/refundCallback")
    public Map refundCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request) {
        String body = request.getReader().lines().collect(Collectors.joining());
        //验证数字签名，确保是微信服务器发送的通知消息
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);
        return wechatApiProvider.callback("health-vue").refundCallback(params, data -> {
            //判断退款是否成功
            String status = data.getRefundStatus().toString();
            if ("SUCCESS".equals(status)) {
                String outRefundNo = data.getOutRefundNo();
                //把订单更新成已退款状态
                boolean bool = orderService.updateRefundStatus(outRefundNo);
                if (!bool) {
                    log.error("订单状态更新失败");
                } else {
                    log.debug("退款流水号为" + outRefundNo + "的订单退款成功");
                }
            } else if ("ABNORMAL".equals(status)) {
                //用户银行卡作废或者冻结，发送短信给用户手机，让用户联系客服执行手动退款到其他银行卡
            }
        });
    }

}
