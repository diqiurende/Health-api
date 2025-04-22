package com.example.health.api.front.controller.form;
import lombok.Data;

import javax.validation.constraints.*;
@Data
public class SendSmsCodeForm {
    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "tel内容错误")
    private String tel;
}
