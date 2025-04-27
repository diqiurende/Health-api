package com.example.health.api.mis.service;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.health.api.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface AppointmentService {

    public ArrayList<HashMap> searchByOrderId(int orderId);

    public PageUtils searchByPage(Map param);

    public int deleteByIds(Integer[] ids);

    public boolean checkin(Map param);

    public HashMap searchGuidanceInfo(int id);

    public boolean updateStatusByUuid(Map param);

    public HashMap searchByUuid(String uuid);
}
