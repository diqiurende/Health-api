package com.example.health.api.mis.service;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.pojo.GoodsEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
@Service
public interface GoodsService {

    public PageUtils searchByPage(Map param);

    public long searchCount(Map param);

    public String uploadIma(MultipartFile file);

    public int insert(GoodsEntity entity);

    public HashMap searchById(int id);

    public int update(GoodsEntity entity);

    public void updateCheckup(int id, MultipartFile file);

    public boolean updateStatus(Map param);

    public int deleteByIds(Integer[] ids);


}
