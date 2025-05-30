package com.example.health.api.mis.controller.form;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Data
public class DeleteRuleByIdForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "userId不能小于1")
    private Integer id;
}