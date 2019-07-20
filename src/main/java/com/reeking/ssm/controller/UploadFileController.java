package com.reeking.ssm.controller;

import com.reeking.ssm.common.Result;
import com.reeking.ssm.common.ResultGenerator;
import com.reeking.ssm.controller.enums.UploadFileTypeEnum;
import com.reeking.ssm.utiles.FileUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.reeking.ssm.common.Constants.FILE_PRE_URL;

/**
 * Created by zhuru on 2018/12/19.
 */
@RestController
@RequestMapping("/upload")
public class UploadFileController {


    /**
     * 通用 文件上传接口(可以上传图片、视频、excel等文件，具体格式可在UploadFileTypeEnum中进行配置)
     *
     * @return
     */
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        ServletContext sc = request.getSession().getServletContext();
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
        String fileName = null;
        UploadFileTypeEnum uploadFileTypeEnum = UploadFileTypeEnum.getFileEnumByType(type.toLowerCase());
        if (uploadFileTypeEnum == UploadFileTypeEnum.ERROR_TYPE) {
            //格式错误则不允许上传，直接返回错误提示
            return ResultGenerator.genFailResult("请检查文件格式！");
        } else {
            //生成文件名称通用方法
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Random r = new Random();
            StringBuilder tempName = new StringBuilder();
            tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(".").append(type);
            fileName = tempName.toString();
        }
        try {
            String dir = sc.getRealPath("/upload");
            FileUtils.writeByteArrayToFile(new File(dir, fileName), file.getBytes());
        } catch (IOException e) {
            //文件上传异常
            return ResultGenerator.genFailResult("文件上传失败！");
        }
        Result result = ResultGenerator.genSuccessResult();
        //返回文件的全路径
        StringBuilder fileUrl = new StringBuilder();
        fileUrl.append(FILE_PRE_URL).append("/upload/").append(fileName);
        result.setData(fileUrl.toString());
        return result;
    }

    /**
     * 大文件分片上传前逐片检查
     * @param request
     * @param chunks
     * @param chunk
     * @param guid
     * @param fileName
     * @return
     */
    @ResponseBody
    @RequestMapping("/checkChunk")
    public Result checkChunk(HttpServletRequest request, Integer chunks, Integer chunk, String guid, String fileName) {
        try{
            String uploadDir = FileUtil.getRealPath(request, "/upload");
            String ext = fileName.substring(fileName.lastIndexOf("."));
            // 判断是否存在分片
            if (chunks != null && chunk != null) {
                //等价于 uploadDir + "\\temp\\" + guid + "\\" + chunk + ext
                StringBuilder tempFilePath = new StringBuilder();
                tempFilePath.append(uploadDir).append(File.separator).append("temp").append(File.separator).append(guid).append(File.separator).append(chunk).append(ext);
                File file = new File(tempFilePath.toString());
                //是否已存在分片,如果已存在分片则返回SUCCESS结果
                if (file.exists()) {
                    return ResultGenerator.genSuccessResult("分片已存在");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("error");
        }
        return ResultGenerator.genNullResult("不存在分片");
    }

    /**
     * 分片上传文件
     * @param request
     * @param guid
     * @param chunks
     * @param chunk
     * @param name
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping("/files")
    public Result upload(HttpServletRequest request, String guid, Integer chunks, Integer chunk, String name, MultipartFile file) {
        String filePath = null;
        // 上传存储路径
        String uploadDir = FileUtil.getRealPath(request, "/upload");
        // 后缀名
        String ext = name.substring(name.lastIndexOf("."));
        StringBuilder tempFileName = new StringBuilder();
        //等价于 uploadDir + "\\temp\\" + guid + "\\" + chunk + ext
        tempFileName.append(uploadDir).append(File.separator).append("temp").append(File.separator).append(guid).append(File.separator).append(chunk).append(ext);
        File tempFile = new File(tempFileName.toString());
        // 判断文件是否分块
        if (chunks != null && chunk != null) {
            // 根据guid创建临时文件夹
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            try{
                // 保存每一个分片
                file.transferTo(tempFile);
            } catch(Exception e) {
                e.printStackTrace();
                return ResultGenerator.genFailResult("上传文件异常");
            }
            // 如果是当前分片为最后一个分片，则合并临时文件
            if (chunk == chunks -1 ) {
                StringBuilder tempFileFolder = new StringBuilder();
                //等价于 uploadDir + "\\temp\\" + guid + File.separator
                tempFileFolder.append(uploadDir).append(File.separator).append("temp").append(File.separator).append(guid).append(File.separator);
                String newFileName = FileUtil.mergeFile(chunks, ext, tempFileFolder.toString(), request);
                filePath = "/upload/chunked/" + newFileName;
            }
        } else {
            // 不用分片的文件存储到files文件夹下
            StringBuilder destPath = new StringBuilder();
            destPath.append(uploadDir).append(File.separator).append("files").append(File.separator);
            String newName = System.currentTimeMillis() + ext;// 文件新名称
            try {
                FileUtils.writeByteArrayToFile(new File(destPath.toString(), newName), file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            filePath = "/upload/files/" + newName;
        }
        Result result = ResultGenerator.genSuccessResult();
        result.setData(filePath);
        return result;
    }
}
