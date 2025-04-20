package com.example.health.api.mis.service.impl;

import com.example.health.api.db.dao.RuleMapper;
import com.example.health.api.mis.service.RuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class RuleServiceImpl implements RuleService {
    @Resource
    private RuleMapper ruleMapper;

    @Override
    public ArrayList<HashMap> searchAllRule() {
        ArrayList<HashMap> list = ruleMapper.searchAllRule();
        return list;
    }
}
