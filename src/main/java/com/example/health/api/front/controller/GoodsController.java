package com.example.health.api.front.controller;

import com.example.health.api.common.R;
import com.example.health.api.front.controller.form.SearchGoodsByIdForm;
import com.example.health.api.front.service.GoodsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

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
}