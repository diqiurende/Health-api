package com.example.health.api.mis.controller;

import com.example.health.api.common.R;
import com.example.health.api.mis.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/mis/role")
public class RoleController {
    @Resource
     private RoleService roleService;

    @GetMapping("searchAllRole")
    public R searchAllRole(){
        ArrayList<HashMap> roles=roleService.searchAllRole();
        return R.ok().put("list",roles);
    }
}
