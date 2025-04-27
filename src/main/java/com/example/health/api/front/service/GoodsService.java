package com.example.health.api.front.service;

import com.example.health.api.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public interface GoodsService {
    public HashMap searchById(int id);

    public HashMap searchIndexGoodsByPart(Integer[] partIds);

    public PageUtils searchListByPage(Map param);

    public HashMap searchSnapshotById(String snapshotId, Integer customerId);


}
