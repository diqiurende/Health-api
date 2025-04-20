package com.example.health.api.front.controller.form;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SearchGoodsByIdForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;
}