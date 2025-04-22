package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
//import com.alipay.api.domain.HahaIspTestDO;
import com.example.health.api.common.MinioUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.config.exception.HealthException;
import com.example.health.api.db.dao.GoodsMapper;
import com.example.health.api.db.pojo.GoodsEntity;
import com.example.health.api.mis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import cn.hutool.crypto.digest.MD5;
@Service("MisGoodsServiceImpl")
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private MinioUtil minioUtil;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = goodsMapper.searchCount(param);
        if (count > 0) {//若存在满足查询条件的记录
            list = goodsMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public long searchCount(Map param) {
        return 0;
    }

    @Override
    public String uploadIma(MultipartFile file) {
        //生成只包含英文和数字的新文件名
        String newName= IdUtil.simpleUUID()+".jpg";
        //构建目录
        String path="front/goods/"+newName;
        minioUtil.uploadImage(path,file);
        return path;
    }

    @Override
    @Transactional
    public int insert(GoodsEntity entity) {
        // 计算商品当前的MD5值作快照
        String md5 = this.genEntityMd5(entity);
        entity.setMd5(md5);

        // 保存新快照的商品记录
        int rows = goodsMapper.insert(entity);

        return rows;

    }

    private String genEntityMd5(GoodsEntity entity) {
        //将对象转换为json对象
        JSONObject json = JSONUtil.parseObj(entity);
        //移除其他字段
        json.remove("id");
        json.remove("partId");
        json.remove("salesVolume");
        json.remove("status");
        json.remove("md5");
        json.remove("updateTime");
        json.remove("createTime");
        String md5 = MD5.create().digestHex(json.toString()).toUpperCase();//大写16进制字符串形式返回
        return md5;
    }
    @Override
    public HashMap searchById(int id) {
        Map param=new HashMap<>();
        param.put("id",id);
        HashMap map=goodsMapper.searchById(param);

        //将以下String类型的字段转为Json字符串存储
        Arrays.stream(new  String[]{"tag", "checkup_1", "checkup_2", "checkup_3", "checkup_4"})
                .forEach(key->{
                    String value= MapUtil.getStr(map,key);
                    if(value!=null){
                        JSONArray jsonArray=JSONUtil.parseArray(value);
                        map.replace(key,jsonArray);
                    }
                });
        return map;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goodsCache", key = "#entity.id")
    public int update(GoodsEntity entity){
        //和insert一样 更新MD5值
        String md5 = this.genEntityMd5(entity);
        entity.setMd5(md5);

        // 保存新快照的商品记录
        int rows = goodsMapper.update(entity);

        return rows;

    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goodsCache", key = "#id")
    public void updateCheckup(int id, MultipartFile file) {

        //list用于保存数据
        List<LinkedHashMap<String, String>> list = new ArrayList<>();

        // 每列对应的字段名，保持顺序一致
        String[] keys = {"place", "name", "item", "type", "code", "sex", "value", "template"};
        try(
            InputStream in = file.getInputStream(); // 读取上传文件的输入流
            BufferedInputStream bin = new BufferedInputStream(in); // 用缓冲流提高读取效率
            XSSFWorkbook workbook = new XSSFWorkbook(bin) // 用 POI 解析 Excel（XSSFWorkbook 用于 .xlsx 文件）
        ){
            XSSFSheet sheet = workbook.getSheetAt(0); // 获取第一个 Sheet
            for(int i=1;i<=sheet.getLastRowNum();i++){
                XSSFRow row=sheet.getRow(i);
                // 创建有序 Map 保存每列的值
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                // 使用循环处理每一列
                for (int j = 0; j < keys.length; j++) {
                    XSSFCell cell = row.getCell(j);
                    String value = cell.getStringCellValue();
                    map.put(keys[j], value);
                }
                list.add(map);
            }

            System.out.println(list);

        }catch (Exception e) {
            throw new HealthException("处理Excel文件失败", e);
        }
        if (list.size() == 0) {
            throw new HealthException("文档内容无效");
        }

        //把文件保存到minio
        //id作为文件名
        String path = "/mis/goods/checkup/" + id + ".xlsx";
        minioUtil.uploadExcel(path,file);


        // 根据商品ID查询商品记录，然后重新计算MD5值，更新商品记录的checkup和md5字段
        GoodsEntity entity=goodsMapper.searchEntityById(id);
        //转换之前的list为json数组
        String json_list=JSONUtil.parseArray(list).toString();
        //更新checkup
        entity.setCheckup(json_list);

        String md5 = this.genEntityMd5(entity);
        //构建update的param
        HashMap map=new HashMap(){
            {
                put("id", id);
                put("checkup", json_list);
                put("md5", md5); //传入新的MD5
            }
        };
        int rows=goodsMapper.updateCheckup(map);
        if(rows!=1){
            throw new HealthException("更新体检内容失败");
        }


    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goodsCache", key = "#param.get('id')",
            condition = "#param.get('status')==false")
    public boolean updateStatus(Map param) {
        int rows=goodsMapper.updateStatus(param);
        if(rows!=1){
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "goodsCache", key = "#ids")
    public int deleteByIds(Integer[] ids) {
        //根据id查询到记录的封面
        ArrayList<String> list = goodsMapper.searchImageByIds(ids);
        int rows=goodsMapper.deleteByIds(ids);
        if(rows>0){
            //遍历这些记录 删除对应的封面
            list.forEach(path->{
                minioUtil.deleteFile(path);
            });
        }
        return rows;
    }





}
