package com.example.health.api.schedule;

import cn.hutool.core.date.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.example.health.api.db.dao.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Component
@Slf4j
public class AppointmentSchedule {
    @Resource
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 23 * * ?")
    public void createCacheAfter62Day() {
        int maxNum = Integer.parseInt(redisTemplate.opsForValue().get("setting#appointment_number").toString());
        int realNum = 0;
        String date = new DateTime().offset(DateField.DAY_OF_MONTH, 62).toDateStr();
        String key = "appointment#" + date;
        redisTemplate.opsForHash().putAll(key, new HashMap() {{
            put("maxNum", maxNum);
            put("realNum", realNum);
        }});
        DateTime dateTime = new DateTime(date).offsetNew(DateField.DAY_OF_MONTH, 1);
        redisTemplate.expireAt(key, dateTime);
        log.debug("生成了" + date + "的体检日程缓存");
    }
}
