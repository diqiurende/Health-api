package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.dao.RoleMapper;
import com.example.health.api.db.pojo.RoleEntity;
import com.example.health.api.mis.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Override
    public ArrayList<HashMap> searchAllRole() {
        ArrayList<HashMap> list=roleMapper.searchAllRole();
        return list;
    }


    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = roleMapper.searchCount(param);
        if (count > 0) {
            list = roleMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }


    @Override
    @Transactional
    public int insert(RoleEntity role) {
        int rows = roleMapper.insert(role);
        return rows;
    }


    @Override
    public HashMap searchById(int id) {
        HashMap map = roleMapper.searchById(id);
        return map;
    }

    @Override
    public ArrayList<Integer> searchUserIdByRoleId(int roleId) {
        ArrayList<Integer> list = roleMapper.searchUserIdByRoleId(roleId);
        return list;
    }

    @Override
    @Transactional
    public int update(RoleEntity role) {
        int rows = roleMapper.update(role);
        return rows;
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        if (!roleMapper.searchCanDelete(ids)) {
            throw new HealthException("无法删除关联用户的角色");
        }
        int rows = roleMapper.deleteByIds(ids);
        return rows;
    }
}
