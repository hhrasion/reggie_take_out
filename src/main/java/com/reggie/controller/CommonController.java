package com.reggie.controller;

import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value(("${reggie.path}"))
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){ // MultipartFile 参数名与前端传来的名称保持一致
        log.info(file.toString());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID生成文件名
        String fileName = UUID.randomUUID() + suffix;

        // 创建一个目录对象
        File dir = new File(basePath);
        // 判断当前目录是否存在
        if (!dir.exists()){
            // 目录不存在，需要创建
            dir.mkdirs();
        }

        // 将文件转存至指定位置
        try {
            file.transferTo(new File(basePath+fileName));
        }catch (IOException e){
            e.printStackTrace();
            return R.error("上传失败");
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            // 输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            // 输出流，通过输出流将文件协会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes =new byte[1024];
            while ( (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();;
            fileInputStream.close();;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
