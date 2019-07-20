package com.reeking.ssm.controller;

import com.reeking.ssm.common.Result;
import com.reeking.ssm.common.ResultGenerator;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by zhuru on 2018/12/18.
 */
@RestController
@RequestMapping("/image")
public class UploadImageController {

    @RequestMapping("")
    public Result upload(HttpServletRequest request, @RequestParam("file")MultipartFile file) throws IOException {
        ServletContext context = request.getSession().getServletContext();
        String dir = context.getRealPath("/upload");
        String imgName = file.getOriginalFilename();
        String type = imgName.substring(imgName.lastIndexOf(".") + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        if ("jpg".equals(type)) {
            imgName = sdf.format(new Date()) + r.nextInt(100) + ".jpg";
        } else if ("png".equals(type)) {
            imgName = sdf.format(new Date()) + r.nextInt(100) + ".png";
        } else if ("jpeg".equals(type)) {
            imgName = sdf.format(new Date()) + r.nextInt(100) + ".jpeg";
        } else if ("gif".equals(type)) {
            imgName = sdf.format(new Date()) + r.nextInt(100) + ".gif";
        } else {
            return null;
        }
        FileUtils.writeByteArrayToFile(new File(dir, imgName), file.getBytes());
        Result result = ResultGenerator.genSuccessResult();
        result.setData("/upload/" + imgName);
        return result;
    }
}
