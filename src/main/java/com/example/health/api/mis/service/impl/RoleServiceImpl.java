package com.example.health.api.mis.service.impl;

import com.example.health.api.db.dao.RoleMapper;
import com.example.health.api.mis.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Override
    public ArrayList<HashMap> searchAllRole() {
        ArrayList<HashMap> list=roleMapper.searchAllRole();
        return list;
    }
}
