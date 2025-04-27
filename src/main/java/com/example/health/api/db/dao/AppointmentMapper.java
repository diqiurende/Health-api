package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.AppointmentEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_appointment(体检预约表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.AppointmentEntity
*/
public interface AppointmentMapper {

    public ArrayList<HashMap> searchByOrderId(int orderId);

    public int insert(AppointmentEntity entity);

    public ArrayList<HashMap> searchFrontAppointmentByPage(Map param);
    public long searchFrontAppointmentCount(Map param);

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public int deleteByIds(Integer[] ids);


    public int checkin(Map param);
    public HashMap searchUuidAndSnapshotId(Map param);

    public HashMap searchSummaryById(int id);


    public int updateStatusByUuid(Map param);

    public HashMap searchByUuid(String uuid);

    public HashMap searchDataForReport(int id);
}




