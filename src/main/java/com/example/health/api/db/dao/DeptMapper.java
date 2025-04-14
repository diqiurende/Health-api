package com.example.health.api.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author faiz
* @description 针对表【tb_dept(部门表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.DeptEntity
*/
@Mapper
public interface DeptMapper {
    public ArrayList<HashMap> searchAllDept();

}




