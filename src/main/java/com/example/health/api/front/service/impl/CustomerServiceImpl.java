package com.example.health.api.front.service.impl;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.health.api.db.dao.CustomerMapper;
import com.example.health.api.db.dao.GoodsMapper;
import com.example.health.api.db.dao.OrderMapper;
import com.example.health.api.db.pojo.CustomerEntity;
import com.example.health.api.front.service.CustomerService;
import com.example.health.api.front.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("FrontCustomerServiceImpl")
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private OrderMapper orderMapper;

    @Override
    public boolean sendSmsCode(String tel) {
        // 1. 生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(6);
        System.out.println(code);  // 控制台输出验证码

        // 2. 构建防止重复发送的Redis键名
        String key = "sms_code_refresh_" + tel;

        // 3. 检查是否处于短信发送冷却期
        if (redisTemplate.hasKey(key)) {
            // 如果存在冷却期key，说明2分钟内已发过短信，禁止重复发送
            return false;
        }

        // 4. 设置短信发送冷却期（2分钟内不允许重复发送）
        redisTemplate.opsForValue().set(key, code);  // 值存验证码
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);  // 2分钟过期

        // 5. 存储验证码到Redis（有效期5分钟）
        key = "sms_code_" + tel;  // 构建正式验证码存储键
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);

        // 6. 实际发送短信（待实现）
        // TODO 调用短信服务商API发送验证码

        return true;  // 发送成功
    }

    @Override
    @Transactional
    public HashMap login(String tel, String code) {
        HashMap map = new HashMap();

        String key = "sms_code_" + tel;
        //若验证码在redis里过期
        if (!redisTemplate.hasKey(key)) {
            map.put("result", false);
            map.put("msg", "短信验证码已过期");
            return map;
        }

        String cacheCode = redisTemplate.opsForValue().get(key).toString();
        if (!cacheCode.equals(code)) {
            map.put("result", false);
            map.put("msg", "短信验证码不正确");
            return map;
        }

        redisTemplate.delete(key);
        key = "sms_code_refresh_" + tel;
        redisTemplate.delete(key);

        Integer id = customerMapper.searchIdByTel(tel);
        //判断是不是新用户
        if (id == null) {
            CustomerEntity entity = new CustomerEntity();
            entity.setTel(tel);
            //注册新用户
            customerMapper.insert(entity);
            id = entity.getId();
        }

        map.put("id", id);
        map.put("result", true);
        map.put("msg", "登陆成功");
        return map;
    }

    @Override
    public HashMap searchSummary(int id) {
        HashMap map = customerMapper.searchById(id);
        map.putAll(orderMapper.searchFrontStatistic(id));
        return map;
    }

    @Override
    @Transactional
    public boolean update(Map param) {
        int rows = customerMapper.update(param);
        return rows == 1;
    }
}