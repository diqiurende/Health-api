package com.example.health.api.mis.service.impl;

import com.example.health.api.db.dao.PermissionMapper;
import com.example.health.api.mis.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public ArrayList<HashMap> searchAllPermission() {
        ArrayList<HashMap> list = permissionMapper.searchAllPermission();
        return list;
    }
}