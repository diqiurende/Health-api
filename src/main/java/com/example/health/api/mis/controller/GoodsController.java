package com.example.health.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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

@RestController("MisGoodsController")
@RequestMapping("/mis/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;


    @Resource
    private MinioUtil minioUtil;

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGoodsByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = goodsService.searchByPage(param);
        return R.ok().put("page", pageUtils);

    }

    @PostMapping("/uploadImage")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R uploadImage(@Param("file")MultipartFile file){
        String path=goodsService.uploadIma(file);
        return R.ok().put("result",path);
    }
    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "GOODS:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertGoodsForm form) {
        //from里的chechup是ArrayList,转pojo对象的String需单独操作
        GoodsEntity entity= BeanUtil.toBean(form,GoodsEntity.class, CopyOptions.create().setIgnoreProperties("checkup_1", "checkup_2", "checkup_3", "checkup_4", "tag"));
        String temp=null;
        System.out.println("===准备输出checkup_1===");
        System.out.println(form.getCheckup_1());
        System.out.println("===输出结束===");
        if (form.getCheckup_1() != null) {
            //手动将ArrayList转换成JSON数组字符串，给POJO对象的checkup_1变量赋值
            JSONArray list=JSONUtil.parseArray(form.getCheckup_1());
            temp = list.toString();
            entity.setCheckup_1(temp);
        }

        if (form.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }

        if (form.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (form.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (form.getTag() != null) {
            temp = JSONUtil.parseArray(form.getTag()).toString();
            entity.setTag(temp);
        }
        int rows= goodsService.insert(entity);
        return R.ok().put("rows",rows);

    }


    @PostMapping("searchById")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchGoodsByIdForm form){
       int id=form.getId();
       HashMap param=goodsService.searchById(id);
       return R.ok().put("result",param);
    }


    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGoodsForm form) {
        //from里的chechup是ArrayList,转pojo对象的String需单独操作
        GoodsEntity entity= BeanUtil.toBean(form,GoodsEntity.class, CopyOptions.create().setIgnoreProperties("checkup_1", "checkup_2", "checkup_3", "checkup_4", "tag"));
        String temp=null;
        if (form.getCheckup_1() != null) {
            //手动将ArrayList转换成JSON数组字符串，给POJO对象的checkup_1变量赋值
            temp = JSONUtil.parseArray(form.getCheckup_1()).toString();
            entity.setCheckup_1(temp);
        }

        if (form.getCheckup_2() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_2()).toString();
            entity.setCheckup_2(temp);
        }

        if (form.getCheckup_3() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_3()).toString();
            entity.setCheckup_3(temp);
        }
        if (form.getCheckup_4() != null) {
            temp = JSONUtil.parseArray(form.getCheckup_4()).toString();
            entity.setCheckup_4(temp);
        }
        if (form.getTag() != null) {
            temp = JSONUtil.parseArray(form.getTag()).toString();
            entity.setTag(temp);
        }
        int rows= goodsService.update(entity);
        return R.ok().put("rows",rows);

    }


    @PostMapping("/updateExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE","GOODS:INSERT"}, mode = SaMode.OR)
    public R updateExcel( @Valid UploadCheckupExcelForm form,
                         @Param("file") MultipartFile file){
        int id= form.getId();
        goodsService.updateCheckup(id,file);
        return R.ok();

    }

    @GetMapping("/downloadExcel")
    @SaCheckPermission(value = {"ROOT", "GOODS:SELECT", "GOODS:INSERT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public void getfile(@Valid DownloadExcelForm form, HttpServletResponse response){
            try {
                // 设置响应头，告诉浏览器这是一个附件，文件名为：{id}.xlsx
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + form.getId() + ".xlsx");

                // 设置MIME类型为二进制流，浏览器识别为下载操作
                response.setContentType("application/x-download");

                // 设置字符编码，避免中文乱码
                response.setCharacterEncoding("UTF-8");

                // 构造MinIO中该Excel文件的路径
                String path = "/mis/goods/checkup/" + form.getId() + ".xlsx";

                // 使用 try-with-resources 自动关闭流，避免内存泄露
                try (
                        // 从MinIO中读取文件输入流
                        InputStream in = minioUtil.downloadFile(path);
                        BufferedInputStream bin = new BufferedInputStream(in);

                        // 获取HTTP响应输出流
                        ServletOutputStream out = response.getOutputStream();
                        BufferedOutputStream bout = new BufferedOutputStream(out);
                ) {
                    // 将MinIO读取的内容直接写入浏览器输出流，实现文件下载
                    IoUtil.copy(bin, bout);
                }
            } catch (Exception e) {
                // 捕获异常，抛出自定义异常信息
                throw new HealthException("文档下载失败");
            }

    }

    @PostMapping("updateStaus")
    @SaCheckPermission(value = {"ROOT", "GOODS:UPDATE"}, mode = SaMode.OR)
    public R updateStatus (@RequestBody @Valid UpdateGoodsStatusForm form){
        //转form为Map
        Map param=BeanUtil.beanToMap(form);
        boolean bool = goodsService.updateStatus(param);
        return R.ok().put("result", bool);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "GOODS:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteGoodsByIdsForm form) {
        int rows = goodsService.deleteByIds(form.getIds());
        return R.ok().put("rows", rows);
    }





}
