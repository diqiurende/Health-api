package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.SystemEntity;

import java.util.ArrayList;

/**
* @author faiz
* @description 针对表【tb_system(系统表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.SystemEntity
*/
public interface SystemMapper {

    public ArrayList<SystemEntity> searchAll();

}




