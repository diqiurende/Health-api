package com.example.health.api.front.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.db.dao.GoodsMapper;
import com.example.health.api.db.dao.GoodsSnapshotDao;
import com.example.health.api.db.dao.OrderMapper;
import com.example.health.api.db.pojo.GoodsSnapshotEntity;
import com.example.health.api.front.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("FrontGoodsServiceImpl")
@Slf4j
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;


    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;



    @Override
    @Cacheable(cacheNames = "goodsCache", key = "#id")
    public HashMap searchById(int id) {
        Map param = new HashMap() {{
            put("id", id);
            put("status", true);
        }};
        HashMap map = goodsMapper.searchById(param);
        if (map != null) {
            for (String key : new String[]{"tag", "checkup_1", "checkup_2", "checkup_3", "checkup_4"}) {
                String temp = MapUtil.getStr(map, key);
                JSONArray array = JSONUtil.parseArray(temp);
                map.replace(key, array);
            }
            return map;
        }
        return null;
    }

    @Override
    public HashMap searchIndexGoodsByPart(Integer[] partIds) {
        HashMap map = new HashMap();
        for (int partId : partIds) {
            ArrayList<HashMap> list = goodsMapper.searchByPartIdLimit4(partId);
            map.put(partId, list);
        }
        return map;
    }

    @Override
    public PageUtils searchListByPage(Map param) {
        ArrayList<HashMap> list=new ArrayList<>();
        long count=goodsMapper.searchListCount(param);
        if(count>0){
            list=goodsMapper.searchListByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return  pageUtils;

    }


    @Override
    public HashMap searchSnapshotById(String snapshotId, Integer customerId) {


        GoodsSnapshotEntity entity = goodsSnapshotDao.searchById(snapshotId);
        HashMap map = BeanUtil.toBean(entity, HashMap.class);
        return map;
    }


}
