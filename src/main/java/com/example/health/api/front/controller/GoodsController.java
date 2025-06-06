package com.example.health.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.front.controller.form.SearchGoodsByIdForm;
import com.example.health.api.front.controller.form.SearchGoodsListByPageForm;
import com.example.health.api.front.controller.form.SearchGoodsSnapshotByIdForm;
import com.example.health.api.front.controller.form.SearchIndexGoodsByPartForm;
import com.example.health.api.front.service.GoodsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController("FrontGoodsController")
@RequestMapping("/front/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;

    @PostMapping("/searchById")
    public R searchById(@RequestBody @Valid SearchGoodsByIdForm form) {
        HashMap map = goodsService.searchById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/searchIndexGoodsByPart")
    public R searchIndexGoodsByPart(@RequestBody @Valid SearchIndexGoodsByPartForm form) {
        HashMap map = goodsService.searchIndexGoodsByPart(form.getPartIds());
        return R.ok().put("result", map);
    }

    @PostMapping("/searchListByPage")
    public R searchListByPage(@RequestBody @Valid SearchGoodsListByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = goodsService.searchListByPage(param);
        return R.ok().put("page", pageUtils);
    }


    @PostMapping("/searchSnapshotForMis")
    @SaCheckLogin
    public R searchSnapshotForMis(@RequestBody @Valid SearchGoodsSnapshotByIdForm form){
        HashMap map = goodsService.searchSnapshotById(form.getSnapshotId(), null);
        return R.ok().put("result", map);
    }

}