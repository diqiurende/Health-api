package com.example.health.api.mis.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.example.health.api.common.MinioUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.dao.AppointmentMapper;
import com.example.health.api.db.dao.CheckupReportMapper;
import com.example.health.api.db.dao.CheckupResultDao;
import com.example.health.api.db.pojo.CheckupResultEntity;
import com.example.health.api.mis.service.CheckupReportService;
import com.example.health.api.report.CheckupReportUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class CheckupReportServiceImpl implements CheckupReportService {
    @Resource
    private CheckupReportMapper checkupReportMapper;


    @Resource
    private AppointmentMapper appointmentDao;

    @Resource
    private CheckupResultDao checkupResultDao;

    @Resource
    private CheckupReportUtil checkupReportUtil;

    @Resource
    private MinioUtil minioUtil;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = checkupReportMapper.searchCount(param);
        if (count > 0) {
            list = checkupReportMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);

        return pageUtils;
    }


    @SneakyThrows
    @Override
    @Transactional
    public boolean createReport(Integer id) {
        HashMap result = checkupReportMapper.searchById(id);
        if (result == null || result.size() == 0) {
            throw new HealthException("不存在体检报告记录");
        }
        int appointmentId = MapUtil.getInt(result, "appointmentId");
        String resultId = MapUtil.getStr(result, "resultId");
        int status = MapUtil.getInt(result, "status");
        DateTime date = DateUtil.parseDate(MapUtil.getStr(result, "date"));
        DateTime today = new DateTime(DateUtil.today());
        //判断当前日期距离体检日期是否在10天以内
//        if (today.offsetNew(DateField.DAY_OF_MONTH, -10).isBefore(date)) {
//            throw new HealthException("无法生成10天以内的体检报告");
//        }
        //根据状态判断是否已经生成过体检报告
        if (status != 1) {
            log.debug("主键为" + id + "的体检报告已经生成，当前任务自动结束");
            return true;
        }

        //查询体检人信息、体检套餐名称
        HashMap map = appointmentDao.searchDataForReport(appointmentId);
        //查询体检项目
        CheckupResultEntity entity = checkupResultDao.searchById(resultId);
        List<Map> checkup = entity.getCheckup();
        map.put("checkup", checkup);
        map.put("result", entity.getResult());

        HashSet set = new HashSet();

        //把所有体检科室添加到Set中
        checkup.forEach(one -> {
            String place = MapUtil.getStr(one, "place");
            set.add(place);
        });
        //获取已经录入体检结果的科室列表
        List<String> placeList = entity.getPlace();



        //创建体检报告
        XWPFDocument report = checkupReportUtil.createReport(map);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        report.write(out);
        out.flush();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        String filePath = "/report/checkup/" + resultId + ".docx";
        //把体检报告上传到Minio
        minioUtil.uploadWord(filePath, in);

        //更新体检结果状态
        checkupReportMapper.update(new HashMap() {{
            put("status", 2);
            put("filePath", filePath);
            put("id", id);
        }});

        return true;
    }
}