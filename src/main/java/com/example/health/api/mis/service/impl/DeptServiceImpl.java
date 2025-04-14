package com.example.health.api.mis.service.impl;

import com.example.health.api.db.dao.DeptMapper;
import com.example.health.api.mis.service.DeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class DeptServiceImpl implements DeptService {
    @Resource
    private DeptMapper deptMapper;

    @Override
    public ArrayList<HashMap> searchAllDept() {
      ArrayList<HashMap> list=deptMapper.searchAllDept();
      return list;
    }
}
