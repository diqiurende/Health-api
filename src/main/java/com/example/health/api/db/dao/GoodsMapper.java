package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.GoodsEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
* @author faiz
* @description 针对表【tb_goods(体检套餐表)】的数据库操作Mapper
* @createDate 2025-03-05 20:35:47
* @Entity generator.domain.GoodsEntity
*/
public interface GoodsMapper {
    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public int insert(GoodsEntity entity);

    public HashMap searchById(Map param);

    public int update(GoodsEntity entity);


    public GoodsEntity searchEntityById(int id);
    public int updateCheckup(Map param);

    public int updateStatus(Map param);

    public ArrayList<String> searchImageByIds(Integer[] ids);
    public int deleteByIds(Integer[] ids);

    public ArrayList<HashMap> searchByPartIdLimit4(int partId);

    public ArrayList<HashMap> searchListByPage(Map param);
    public long searchListCount(Map param);

    public HashMap searchSnapshotNeededById(int id);

    public int updateSalesVolume(int id);

}




