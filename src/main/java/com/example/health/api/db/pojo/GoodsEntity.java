package com.example.health.api.db.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @TableName tb_goods
 */
@Data
public class GoodsEntity {
    private Integer id;

    private String code;

    private String title;

    private String description;

    private String checkup1;

    private String checkup2;

    private String checkup3;

    private String checkup4;

    private String checkup;

    private String image;

    private BigDecimal initialPrice;

    private BigDecimal currentPrice;

    private Integer salesVolume;

    private String type;

    private String tag;

    private Integer partId;

    private Integer ruleId;

    private Integer status;

    private String md5;

    private String updateTime;

    private String createTime;
}