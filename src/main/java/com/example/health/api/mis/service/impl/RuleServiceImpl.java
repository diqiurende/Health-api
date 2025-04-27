package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.db.dao.RuleMapper;
import com.example.health.api.db.pojo.RuleEntity;
import com.example.health.api.mis.service.RuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class RuleServiceImpl implements RuleService {
    @Resource
    private RuleMapper ruleMapper;

    @Override
    public ArrayList<HashMap> searchAllRule() {
        ArrayList<HashMap> list = ruleMapper.searchAllRule();
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = ruleMapper.searchCount(param);
        if (count > 0) {
            list = ruleMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(RuleEntity entity) {
        int rows = ruleMapper.insert(entity);
        return rows;
    }


    @Override
    public HashMap searchById(int id) {
        HashMap map = ruleMapper.searchById(id);
        return map;
    }

    @Override
    @Transactional
    public int update(RuleEntity entity) {
        int rows = ruleMapper.update(entity);
        return rows;
    }

    @Override
    @Transactional
    public int deleteById(int id) {
        int rows = ruleMapper.deleteById(id);
        return rows;
    }
}
