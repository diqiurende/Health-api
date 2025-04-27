package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
@Service
public interface DeptService {
    public ArrayList<HashMap> searchAllDept();

    public PageUtils searchByPage(HashMap param);
}
