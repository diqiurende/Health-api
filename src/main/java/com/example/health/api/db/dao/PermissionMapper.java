package com.example.health.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author faiz
* @description 针对表【tb_permission(权限表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.PermissionEntity
*/
public interface PermissionMapper {

    public ArrayList<HashMap> searchAllPermission();

}




