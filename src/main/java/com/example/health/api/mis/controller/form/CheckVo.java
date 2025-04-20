package com.example.health.api.mis.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.NotBlank;

/**
 * 该泛型用于验证from里的check变量是否包含以下属性
 */
@Data
public class CheckVo {
    @NotBlank(message = "体检项目不能为空")
    @Length(max = 50, message = "体检项目不能超过50个字符")
    private String title;

    @NotBlank(message = "体检内容不能为空")
    @Length(max = 500, message = "体检内容不能超过500个字符")
    private String content;
}
