package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.pojo.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public interface RoleService {
    public ArrayList<HashMap> searchAllRole();

    public PageUtils searchByPage(Map param);

    public int insert(RoleEntity role);


    public HashMap searchById(int id);
    public ArrayList<Integer> searchUserIdByRoleId(int roleId);
    public int update(RoleEntity role);

    public int deleteByIds(Integer[] ids);
}