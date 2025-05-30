package com.example.health.api.mis.controller.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteAppointmentByIdsForm {
    @NotEmpty(message = "ids不能为空")
    private Integer[] ids;
}
