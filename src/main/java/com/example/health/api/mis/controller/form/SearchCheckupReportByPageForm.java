package com.example.health.api.mis.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchCheckupReportByPageForm {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "name内容不正确")
    private String name;

    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "tel内容不正确")
    private String tel;

    @Pattern(regexp = "^[0-9A-Za-z]{10,24}$", message = "waybillCode内容不正确")
    private String waybillCode;

    @Range(min = 1, max = 3, message = "status内容不正确")
    private Integer status;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length必须为10~50之间")
    private Integer length;
}
