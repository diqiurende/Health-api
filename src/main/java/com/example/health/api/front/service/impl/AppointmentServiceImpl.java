package com.example.health.api.front.service.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.db.dao.*;
import com.example.health.api.db.pojo.AppointmentEntity;
import com.example.health.api.db.pojo.GoodsSnapshotEntity;
import com.example.health.api.front.service.AppointmentService;
import com.example.health.api.front.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service("FrontAppointmentService")
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private AppointmentMapper appointmentDao;

    @Resource
    private AppointmentRestrictionMapper appointmentRestrictionDao;

    @Resource
    private OrderMapper orderDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public String insert(AppointmentEntity entity) {
        HashMap<String, String> resultCode = new HashMap() {{
            put("full", "当天预约已满，请选择其他日期");
            put("fail", "预约失败");
            put("success", "预约成功");
        }};
        String key = "appointment#" + entity.getDate();
        String execute = (String) redisTemplate.execute(new SessionCallback() {
            @Override
            public String execute(RedisOperations operations) throws DataAccessException {
                //关注缓存数据（拿到乐观锁的Version）
                operations.watch(key);
                //拿到缓存的数据
                Map entry = operations.opsForHash().entries(key);
                int maxNum = Integer.parseInt(entry.get("maxNum").toString());
                int realNum = Integer.parseInt(entry.get("realNum").toString());
                if (realNum < maxNum) {
                    //开启Redis事务
                    operations.multi();
                    //已人数+1
                    operations.opsForHash().increment(key, "realNum", 1);
                    //提交事务
                    List<Long> list = operations.exec();
                    if (list.size() == 0) {
                        return resultCode.get("fail");
                    }
                    long num = list.get(0);
                    return resultCode.get(num > 0 ? "success" : "fail");
                } else {
                    operations.unwatch();
                    return resultCode.get("full");
                }
            }
        });

        //如果Redis事务提交失败就结束Service方法
        if (!execute.equals(resultCode.get("success"))) {
            return execute;
        }

        int rows = appointmentDao.insert(entity);
        if (rows != 1) {
            return resultCode.get("fail");
        }

        Map entry = redisTemplate.opsForHash().entries(key);
        int maxNum = Integer.parseInt(entry.get("maxNum").toString());

        HashMap param = new HashMap() {{
            put("date", entity.getDate());
            put("num_1", maxNum);
            put("num_2", maxNum);
            put("num_3", 1);
        }};

        //更新预约现流表中的预约人数
        rows = appointmentRestrictionDao.saveOrUpdateRealNum(param);
        if (rows == 0) {
            return resultCode.get("fail");
        }

        //更新订单状态
        int orderId = entity.getOrderId();
        param = new HashMap() {{
            put("status", 5);
            put("id", orderId);
        }};
        rows = orderDao.updateStatus(param);
        if (rows == 0) {
            return resultCode.get("fail");
        }

        return resultCode.get("success");
    }


    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = appointmentDao.searchFrontAppointmentCount(param);
        if (count > 0) {
            list = appointmentDao.searchFrontAppointmentByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}
