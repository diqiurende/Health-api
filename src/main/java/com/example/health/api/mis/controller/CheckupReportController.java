package com.example.health.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.MinioUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.mis.controller.form.CreateCheckupReportForm;
import com.example.health.api.mis.controller.form.SearchCheckupReportByPageForm;
import com.example.health.api.mis.service.CheckupReportService;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;

@RestController
@RequestMapping("/mis/checkup/report")
public class CheckupReportController {
    @Resource
    private CheckupReportService checkupReportService;

    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchCheckupReportByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = checkupReportService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }


    @PostMapping("/createReport")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public R createReport(@RequestBody @Valid CreateCheckupReportForm form) {
        boolean bool = checkupReportService.createReport(form.getId());
        return R.ok().put("result", bool);
    }

    @GetMapping("/downloadReport")
    @SaCheckPermission(value = {"ROOT", "CHECKUP_REPORT:SELECT"}, mode = SaMode.OR)
    public void downloadReport(@RequestParam String filePath, @RequestParam String name,
                               HttpServletResponse response) {
        if (StrUtil.isBlank(name)) {
            throw new HealthException("name不能为空");
        } else if (!ReUtil.isMatch("^[\\u4e00-\\u9fa5]{2,10}$", name)) {
            throw new HealthException("name内容不正确");
        }

        try (InputStream in = minioUtil.downloadFile(filePath);
             OutputStream out = response.getOutputStream();) {
            response.reset();
            response.setContentType("application/x-msdownloadoctet-stream;charset=utf-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + URLEncoder.encode(name + "的体检报告.docx", "UTF-8"));
            IOUtil.copyCompletely(in, out);
        } catch (Exception e) {
            throw new HealthException("下载体检报告失败");
        }
    }
}
