package com.example.health.api.async;

import cn.hutool.core.date.*;
import cn.hutool.core.map.MapUtil;
import com.example.health.api.db.dao.AppointmentRestrictionMapper;
import com.example.health.api.db.dao.SystemMapper;
import com.example.health.api.db.pojo.SystemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Slf4j
public class InitializeWorkAsync {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SystemMapper systemMapper;

    @Resource
    private AppointmentRestrictionMapper appointmentRestrictionMapper;

    /**
     * 初始化工作（异步任务）
     * 包含系统设置加载 + 未来60天体检日程缓存预加载
     */
    @Async("AsyncTaskExecutor")
    public void init() {
        this.loadSystemSetting();         // 1. 加载系统配置项
        this.createAppointmentCache();    // 2. 初始化未来60天体检人数缓存
    }

    /**
     * 加载系统配置到 Redis 中
     * 每个配置项缓存为： key = setting#{item}, value = {value}
     */
    private void loadSystemSetting() {
        ArrayList<SystemEntity> list = systemMapper.searchAll();
        list.forEach(one -> {
            redisTemplate.opsForValue().set("setting#" + one.getItem(), one.getValue());
        });
        log.debug("系统设置缓存成功");
    }

    /**
     * 生成未来60天的体检预约缓存
     * 每天 key = appointment#{yyyy-MM-dd}, Hash结构保存 maxNum（最多可预约）和 realNum（实际已预约）
     */
    private void createAppointmentCache() {
        // 起始日期为明天
        DateTime startDate = DateUtil.tomorrow();
        // 结束日期是60天后的那天
        DateTime endDate = startDate.offsetNew(DateField.DAY_OF_MONTH, 60);
        // 生成日期范围对象（每天为步进）
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);

        // 查询数据库中该时间段内已有预约数据
        HashMap param = new HashMap() {{
            put("startDate", startDate.toDateStr());
            put("endDate", endDate.toDateStr());
        }};
        ArrayList<HashMap> list = appointmentRestrictionMapper.searchScheduleInRange(param);

        // 遍历未来60天，构建每天的预约缓存
        range.forEach(one -> {
            String date = one.toDateStr();  // 当前日期字符串
            // 获取系统默认最大预约人数（若当天未特殊配置）
            int maxNum = Integer.parseInt(redisTemplate.opsForValue().get("setting#appointment_number").toString());
            int realNum = 0;

            // 如果数据库中存在该日期配置，使用该配置覆盖默认值
            for (HashMap map : list) {
                String temp = MapUtil.getStr(map, "date");
                if (date.equals(temp)) {
                    maxNum = MapUtil.getInt(map, "num_1");  // 特殊日期最大预约数
                    realNum = MapUtil.getInt(map, "num_3"); // 特殊日期当前预约数
                    break;
                }
            }

            // 构建 Redis 缓存数据
            HashMap cache = new HashMap();
            cache.put("maxNum", maxNum);
            cache.put("realNum", realNum);

            // 存入 Redis，key=appointment#日期，value=Hash结构
            String key = "appointment#" + date;
            redisTemplate.opsForHash().putAll(key, cache);
            // 设置缓存过期时间为第二天凌晨（确保不会长期占用）
            DateTime dateTime = new DateTime(date).offsetNew(DateField.DAY_OF_MONTH, 1);
            redisTemplate.expireAt(key, dateTime);
        });

        log.debug("未来60天体检人数缓存成功");
    }
}
