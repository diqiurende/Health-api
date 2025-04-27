package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.health.api.common.PageUtils;
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


    @Override
    public PageUtils searchByPage(HashMap param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = deptMapper.searchCount(param);
        if (count > 0) {
            list = deptMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);

        return pageUtils;
    }
}
