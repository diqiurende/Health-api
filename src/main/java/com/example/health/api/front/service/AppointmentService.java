package com.example.health.api.front.service;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.pojo.AppointmentEntity;

import java.util.Map;

public interface AppointmentService {

    public String insert(AppointmentEntity entity);

    public PageUtils searchByPage(Map param);


}
