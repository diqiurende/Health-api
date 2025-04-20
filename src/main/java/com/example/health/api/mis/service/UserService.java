package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.pojo.UserEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Service
public interface UserService {
    public Integer login(Map param);


    public int updatePassword(Map param);



    //实现分页
     public PageUtils searchByPage(Map param);

    public long searchCount(Map param);

    public int insert(UserEntity user);

    public HashMap searchById(int userId);
    public int update(Map param);

    public int deleteUserByIds(Integer[] ids);

    public int dismisss(int id);
}
