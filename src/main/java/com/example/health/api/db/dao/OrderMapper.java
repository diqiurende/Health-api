package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.OrderEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_order(订单表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.OrderEntity
*/
public interface OrderMapper {
    public HashMap searchFrontStatistic(int customerId);


    public boolean searchIllegalCountInDay(int customerId);
    public int closeOrder();
    public int insert(OrderEntity entity);

    //根据流水号更新订单状态为已付款
    public int updatePayment(Map param);


    public Integer searchCustomerId(String outTradeNo);

    public ArrayList<HashMap> searchFrontOrderByPage(Map param);
    public long searchFrontOrderCount(Map param);

    public String searchAlreadyRefund(int id);
    public HashMap searchRefundNeeded(Map param);
    public int updateOutRefundNo(Map param);

    public int updateRefundStatusByOutRefundNo(String outRefundNo);

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public int deleteById(int id);

    public int updateRefundStatusById(int id);

    public int updateStatus(Map param);

    public Integer hasOwnOrder(Map param);

    public HashMap searchOrderIsFinished(String uuid);


}




