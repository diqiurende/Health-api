package com.example.health.api.db.dao;


import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
* @author faiz
* @description 针对表【tb_user(用户表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.UserEntity
*/
@Mapper
public interface UserMapper {

    public Set<String> searchUserPermissions(int userId);
    public  Integer login(Map param);



}




