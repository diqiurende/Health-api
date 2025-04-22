package com.example.health.api.schedule;

import com.example.health.api.db.dao.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Slf4j
public class OrderSchedule {

    @Resource
    private OrderMapper orderMapper;

    /**
     * 定时任务：每小时执行一次，关闭超时未付款订单
     * @Scheduled：用于定义定时任务的执行时间
     * cron = "0 0 * * * ?" 表示：每小时的第0分钟0秒执行一次（即整点执行）
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void closeUnpaymentOrder() {
        int rows = orderMapper.closeOrder();
        if (rows > 0) {
            log.info("关闭了" + rows + "个未付款的订单");
        }
    }
}