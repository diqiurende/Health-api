package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface OrderService {
    public PageUtils searchByPage(Map param);

    public int deleteById(int id);

    public int updateRefundStatusById(int id);
}
