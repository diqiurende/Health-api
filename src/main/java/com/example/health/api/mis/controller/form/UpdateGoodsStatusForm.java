package com.example.health.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UpdateGoodsStatusForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

    @NotNull(message = "status不能为空")
    private Boolean status;
}