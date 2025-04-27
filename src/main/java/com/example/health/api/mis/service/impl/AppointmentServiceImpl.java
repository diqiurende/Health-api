package com.example.health.api.mis.service.impl;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.example.health.api.common.PageUtils;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.dao.*;
import com.example.health.api.mis.service.AppointmentService;
import com.example.health.api.mis.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service("MisAppointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private CheckupResultDao checkupResultDao;

    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private CheckupReportMapper checkupReportMapper;
    @Override
    public ArrayList<HashMap> searchByOrderId(int orderId) {
        ArrayList<HashMap> list = appointmentMapper.searchByOrderId(orderId);
        return list;
    }


    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = appointmentMapper.searchCount(param);
        if (count > 0) {
            list = appointmentMapper.searchByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }


    @Override
    public int deleteByIds(Integer[] ids) {
        int rows = appointmentMapper.deleteByIds(ids);
        return rows;
    }

    @Override
    @Transactional
    public boolean checkin(Map param) {
        String pid = MapUtil.getStr(param, "pid");
        String name = MapUtil.getStr(param, "name");
        String sex = IdcardUtil.getGenderByIdCard(pid) == 1 ? "男" : "女";


        System.out.println(param);
            //更新体检预约状态为已签到
            int rows = appointmentMapper.checkin(param);
            if (rows != 1) {
                throw new HealthException("保存签到记录失败");
            }
            //查询体检流水号和订单快照ID
            HashMap map = appointmentMapper.searchUuidAndSnapshotId(param);
            String uuid = MapUtil.getStr(map, "uuid");
            System.out.println("uuid"+uuid);
            String snapshotId = MapUtil.getStr(map, "snapshotId");
        System.out.println("snapshotId"+snapshotId);
            List<Map> checkup = goodsSnapshotDao.searchCheckup(snapshotId, sex);
        System.out.println("checkup"+checkup);
            //添加体检结果记录
            boolean bool = checkupResultDao.insert(uuid, checkup);
            if (!bool) {
                throw new HealthException("添加体检结果失败");
            }

        return true;
    }


    @Override
    public HashMap searchGuidanceInfo(int id) {
        HashMap map = appointmentMapper.searchSummaryById(id);
        String snapshotId = MapUtil.getStr(map, "snapshotId");
        String sex = MapUtil.getStr(map, "sex");
        //生成二维码图片base64字符串
        String uuid = MapUtil.getStr(map, "uuid");
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(100);
        qrConfig.setHeight(100);
        qrConfig.setMargin(0);
        String qrCodeBase64 = QrCodeUtil.generateAsBase64(uuid, qrConfig, "jpg");
        map.put("qrCodeBase64", qrCodeBase64);

        List<Map> list = goodsSnapshotDao.searchCheckup(snapshotId, sex);
        LinkedHashSet<Map> set = new LinkedHashSet();
        list.forEach(one -> {
            HashMap temp = new HashMap() {{
                put("place", MapUtil.getStr(one, "place"));
                put("name", MapUtil.getStr(one, "name"));
            }};
            set.add(temp);
        });
        map.put("checkup", set);
        return map;
    }


    @Override
    @Transactional
    public boolean updateStatusByUuid(Map param) {
        int rows = appointmentMapper.updateStatusByUuid(param);
        if (rows != 1) {
            return false;
        }
        int status = MapUtil.getInt(param, "status");
        if (status == 3) {
            //检查对应的订单是否所有体检预约都已经结束
            String uuid = MapUtil.getStr(param, "uuid");
            HashMap map = orderMapper.searchOrderIsFinished(uuid);
            int orderId = MapUtil.getInt(map, "id");
            int n1 = MapUtil.getInt(map, "n1");
            int n2 = MapUtil.getInt(map, "n2");
            if (n1 == n2) {
                //更新订单为已结束状态
                rows = orderMapper.updateStatus(new HashMap<>() {{
                    put("status", 6);//结束状态
                    put("id", orderId);
                }});
                if (rows != 1) {
                    return false;
                }
            }
            //查询体检结果ID
            String resultId = checkupResultDao.searchIdByUuid(uuid);
            param.put("resultId", resultId);
            rows = checkupReportMapper.insert(param);
            if (rows != 1) {
                return false;
            }
        }
        return true;
    }


    @Override
    public HashMap searchByUuid(String uuid) {
        HashMap map = appointmentMapper.searchByUuid(uuid);
        if (map == null) {
            throw new HealthException("不存在体检预约记录");
        }
        Integer status = MapUtil.getInt(map, "status");
        if (status == 1) {
            throw new HealthException("该体检没有签到");
        } else if (status == 3) {
            throw new HealthException("该体检预约已经结束");
        }
        return map;
    }
}