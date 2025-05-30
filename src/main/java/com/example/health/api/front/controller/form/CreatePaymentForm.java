package com.example.health.api.front.controller.form;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreatePaymentForm {
    @NotNull(message = "goodsId不能为空")
    @Min(value = 1, message = "goodsId不能小于1")
    private Integer goodsId;

    @NotNull(message = "number不能为空")
    @Min(value = 1, message = "number不能小于1")
    private Integer number;
}
