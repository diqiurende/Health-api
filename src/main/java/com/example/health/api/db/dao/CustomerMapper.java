package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.CustomerEntity;

import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_customer(客户表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.CustomerEntity
*/
public interface CustomerMapper {
    public Integer searchIdByTel(String tel);
    public void insert(CustomerEntity entity);

    public HashMap searchById(int id);

    public int update(Map param);

}




