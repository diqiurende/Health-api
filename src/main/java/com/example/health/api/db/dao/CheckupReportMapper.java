package com.example.health.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_checkup_report(体检报告表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.CheckupReportEntity
*/
public interface CheckupReportMapper {

    public int insert(Map param);

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public HashMap searchById(int id);

    public int update(Map param);

    public ArrayList<Integer> searchWillGenerateReport();

}




