package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;

import java.util.Map;

public interface CheckupReportService {
    public PageUtils searchByPage(Map param);

    public boolean createReport(Integer id);
}