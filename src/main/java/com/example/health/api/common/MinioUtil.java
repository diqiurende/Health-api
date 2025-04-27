package com.example.health.api.common;

import com.example.health.api.config.exception.HealthException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
@Slf4j
public class MinioUtil {
    //从yaml文件里提取endpoint属性值 服务器地址
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient client;

    //创建连接对象方法 在boot启动后自动执行
    @PostConstruct
    public void init() {
        this.client = new MinioClient.Builder().endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 上传图片
     * @param path 保存在minio服务器的路径
     * @param file  文件
     */
    public void uploadImage(String path, MultipartFile file) {
        try {
            //在Minio中保存图片（文件不能超过5M）
            this.client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)//指定桶
                    .object(path)//指定文件地址
                    .stream(file.getInputStream(), -1, 5 * 1024 * 1024)//获取文件输入流
                    .contentType("image/jpeg").build());
            log.debug("在" + path + "保存了文件");
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new HealthException("保存文件失败");
        }
    }

    public void uploadExcel(String path,MultipartFile file) {
        try {
            //Excel-MIME类型
            String mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            //不能超过20M
            this.client.putObject(PutObjectArgs.builder()
                    .bucket(bucket).object(path)
                    .stream(file.getInputStream(), -1, 20 * 1024 * 1024)
                    .contentType(mime).build());
            log.debug("向" + path + "保存了文件");
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new HealthException("保存文件失败");
        }
    }

    public InputStream downloadFile(String path){
        try{
            return  this.client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        }
        catch (Exception e){
            throw new HealthException("下载文件错误");
        }
    }


        //删除
    public void deleteFile(String path) {
        try {
            this.client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());
            log.debug("删除了" + path + "路径下的文件");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new HealthException("文件删除失败");
        }
    }


    public void uploadWord(String path, InputStream in) {
        try {
            String mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            //在Minio中保存Word文档（文件不能超过50M）
            this.client.putObject(PutObjectArgs.builder().bucket(bucket)
                    .object(path).stream(in, -1, 50 * 1024 * 1024)
                    .contentType(mime).build());
            log.debug("向" + path + "保存了文件");
            in.close();
        } catch (Exception e) {
            log.error("保存文件失败", e);
            throw new HealthException("保存文件失败");
        }
    }
}
