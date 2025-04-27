package com.example.health.api.db.dao;


import com.example.health.api.db.pojo.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
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

    public String SearchUserNameById(int userId);
     public int UpadatePassword(Map param);

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public int insert(UserEntity user);

    public HashMap searchById(int userId);
    public int update(Map param);

    public int deleteUserByIds(Integer[] ids);

    public int dismisss(int id);

    public HashMap searchDoctorById(int userId);



}




