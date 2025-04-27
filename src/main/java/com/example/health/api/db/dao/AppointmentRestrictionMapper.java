package com.example.health.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_appointment_restriction(体检预约限流表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.AppointmentRestrictionEntity
*/
public interface AppointmentRestrictionMapper {

    public ArrayList<HashMap> searchScheduleInRange(Map param);

    public int saveOrUpdateRealNum(Map param);

}




