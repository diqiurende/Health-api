package com.example.health.api.db.dao;

import com.example.health.api.db.pojo.GoodsSnapshotEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository  // 标记为持久层组件，供 Spring 管理
public class GoodsSnapshotDao {

    @Resource
    private MongoTemplate mongoTemplate; // Spring 提供的 MongoDB 操作模板，封装了常用操作方法

    /**
     * 根据 md5 判断是否已存在对应的快照文档
     * @param md5 商品快照的唯一标识（摘要）
     * @return 若存在返回对应快照文档的 _id；否则返回 null
     */
    public String hasGoodsSnapshot(String md5) {
        // 构造条件：md5 字段等于参数 md5
        Criteria criteria = Criteria.where("md5").is(md5);
        // 创建查询对象
        Query query = new Query(criteria);
        // 分页参数：只查询一条
        query.skip(0);
        query.limit(1);
        // 查询匹配的第一条记录
        GoodsSnapshotEntity entity = mongoTemplate.findOne(query, GoodsSnapshotEntity.class);
        // 如果存在返回 _id；否则返回 null
        return entity != null ? entity.get_id() : null;
    }

    /**
     * 插入商品快照文档到 MongoDB
     * @param entity 快照对象（包含商品各字段 + md5）
     * @return 插入后的文档 _id
     */
    public String insert(GoodsSnapshotEntity entity) {
        // save 方法会自动根据是否存在 _id 选择 insert 或 update
        String _id = mongoTemplate.save(entity).get_id();
        return _id;
    }
}
