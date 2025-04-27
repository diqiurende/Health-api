package com.example.health.api.front.service;
import com.example.health.api.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public interface OrderService {
    public HashMap createPayment(Map param);

    public boolean updatePayment(Map param);

    public Integer searchCustomerId(String outTradeNo);
    public boolean searchPaymentResult(String outTradeNo);

    public PageUtils searchByPage(Map param);

    public boolean refund(Map param);

    public boolean updateRefundStatus(String outRefundNo);

    public boolean hasOwnOrder(Map param);

}