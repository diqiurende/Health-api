package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.pojo.RuleEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public interface RuleService {

    public ArrayList<HashMap> searchAllRule();
    public PageUtils searchByPage(Map param);

    public int insert(RuleEntity entity);

    public HashMap searchById(int id);
    public int update(RuleEntity entity);

    public int deleteById(int id);
}
