package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.db.dao.OrderMapper;
import com.example.health.api.mis.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("MisOrderServiceImpl")
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = orderMapper.searchCount(param);
        if (count > 0) {
            list = orderMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);

        return pageUtils;
    }

    @Override
    @Transactional
    public int deleteById(int id) {
        int rows = orderMapper.deleteById(id);
        return rows;
    }


    @Override
    @Transactional
    public int updateRefundStatusById(int id) {
        int rows = orderMapper.updateRefundStatusById(id);
        return rows;
    }
}