package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_role(角色表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.RoleEntity
*/
@Mapper
public interface RoleMapper {
    public ArrayList<HashMap> searchAllRole();

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public int insert(RoleEntity role);

    public HashMap searchById(int id);
    public ArrayList<Integer> searchUserIdByRoleId(int roleId);
    public int update(RoleEntity role);

    public boolean searchCanDelete(Integer[] ids);
    public int deleteByIds(Integer[] ids);


}




