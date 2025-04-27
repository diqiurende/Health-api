package com.example.health.api.mis.controller;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.mis.controller.form.SearchRoleByPageForm;
import com.example.health.api.mis.service.PermissionService;
import com.example.health.api.mis.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/mis/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @GetMapping("/searchAllPermission")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT", "ROLE:UPDATE"}, mode = SaMode.OR)
    public R searchAllPermission() {
        ArrayList<HashMap> list = permissionService.searchAllPermission();
        return R.ok().put("list", list);
    }
}