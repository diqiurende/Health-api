package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.RuleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_rule(规则表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.RuleEntity
*/
public interface RuleMapper {
     public ArrayList<HashMap> searchAllRule();

     public ArrayList<HashMap> searchByPage(Map param);
     public long searchCount(Map param);

     public int insert(RuleEntity entity);

     public HashMap searchById(int id);
     public int update(RuleEntity entity);
     public int deleteById(int id);
}




