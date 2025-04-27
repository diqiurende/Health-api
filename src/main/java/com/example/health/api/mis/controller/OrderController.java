package com.example.health.api.mis.controller;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.MinioUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.pojo.GoodsEntity;
import com.example.health.api.front.controller.form.SearchIndexGoodsByPartForm;
import com.example.health.api.mis.controller.form.*;
import com.example.health.api.mis.service.GoodsService;
import com.example.health.api.mis.service.OrderService;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@RestController("MisOrderController")
@RequestMapping("/mis/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "ORDER:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchOrderByPageForm form) {
        if ((form.getStartDate() != null && form.getEndDate() == null) || (form.getStartDate() == null && form.getEndDate() != null)) {
            throw new HealthException("startDate和endDate都要填完整");
        }
        //验证日期先后逻辑
        else if (form.getStartDate() != null && form.getEndDate() != null) {
            DateTime startDate = DateUtil.parse(form.getStartDate());
            DateTime endDate = DateUtil.parse(form.getEndDate());
            if (endDate.isBefore(startDate)) {
                throw new HealthException("endDate不能早于startDate");
            }
        }
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = orderService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }


    @PostMapping("/deleteById")
    @SaCheckPermission(value = {"ROOT", "ORDER:DELETE"}, mode = SaMode.OR)
    public R deleteById(@RequestBody @Valid DeleteOrderByIdForm form) {
        int rows = orderService.deleteById(form.getId());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/updateRefundStatusById")
    @SaCheckPermission(value = {"ROOT", "ORDER:UPDATE"}, mode = SaMode.OR)
    public R updateRefundStatusById(@RequestBody @Valid UpdateRefundStatusByIdForm form) {
        int rows = orderService.updateRefundStatusById(form.getId());
        return R.ok().put("rows", rows);
    }
}