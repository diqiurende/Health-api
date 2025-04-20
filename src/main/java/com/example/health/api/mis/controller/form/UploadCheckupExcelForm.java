package com.example.health.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UploadCheckupExcelForm {
    @NotNull(message = "id不能小于1")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

}