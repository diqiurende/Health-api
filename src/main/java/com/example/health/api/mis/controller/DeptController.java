package com.example.health.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.mis.controller.form.SearchDeptByPageForm;
import com.example.health.api.mis.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/mis/dept")
public class DeptController {
    @Resource
    private DeptService deptService;

    @GetMapping("/searchAllDept")
    public R searchAllDept(){
        ArrayList<HashMap> list=deptService.searchAllDept();
        return R.ok().put("list",list);
    }


    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@Valid @RequestBody SearchDeptByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = deptService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }
}
