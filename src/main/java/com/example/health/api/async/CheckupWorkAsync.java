package com.example.health.api.async;

import com.example.health.api.mis.service.CheckupReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class CheckupWorkAsync {
    @Resource
    private CheckupReportService checkupReportService;

    @Async("AsyncTaskExecutor")
    public void createReport(Integer id) {
        checkupReportService.createReport(id);
    }
}