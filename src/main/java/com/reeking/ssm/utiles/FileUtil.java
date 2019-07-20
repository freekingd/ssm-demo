package com.reeking.ssm.utiles;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * Created by zhuru on 2018/12/19.
 */
public class FileUtil {

    /**
     * 根据相对路径获取绝对路径
     * @param request
     * @param path
     * @return
     */
    public static String getRealPath(HttpServletRequest request, String path) {
        ServletContext sc = request.getSession().getServletContext();
        return sc.getRealPath(path);
    }

    /**
     * 合并分片临时文件
     * @param chunks
     * @param ext
     * @param mergePath
     * @param request
     * @return
     */
    public static String mergeFile(Integer chunks, String ext, String mergePath,
                                   HttpServletRequest request) {
        String newName = System.currentTimeMillis() + ext;  // 新文件名称
        SequenceInputStream s;
        InputStream s1;
        InputStream s2;
        InputStream s3;
        try{
            s1 = new FileInputStream(mergePath + 0 + ext);
            s2 = new FileInputStream(mergePath + 1 + ext);
            s = new SequenceInputStream(s1, s2);
            String tempFilePath;
            for(int i = 2; i < chunks; i++) {
                tempFilePath = mergePath + i + ext;
                s3 = new FileInputStream(tempFilePath);
                s = new SequenceInputStream(s, s3);
            }
            // 合并后文件存放在/upload/chunked目录下
            StringBuilder filePath = new StringBuilder();
            filePath.append(getRealPath(request, "/upload")).append(File.separator).append("chunked").append(File.separator);
            // 合并文件
            saveStreamToFile(s, filePath.toString(), newName);
            // 删除分块文件
            deleteFolder(mergePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return newName;
    }

    /**
     * 将SequenceInputStream流保存文件
     * @param inputStream
     * @param filePath
     * @param fileName
     */
    private static void saveStreamToFile(SequenceInputStream inputStream, String filePath, String fileName) throws Exception {
        File fileDirectory = new File(filePath);
        synchronized (fileDirectory) {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new Exception("文件夹创建失败，路径为：" + fileDirectory);
                }
            }
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new Exception("文件夹创建失败，路径为：" + fileDirectory);
                }
            }
        }
        OutputStream outputStream = new FileOutputStream(filePath + fileName);
        byte[] buffer = new byte[1024];
        int len;
        try{
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    /**
     * 删除文件夹（里面只有文件）
     * @param mergePath
     * @return
     */
    private static boolean deleteFolder(String mergePath) {
        File dir = new File(mergePath);
        File[] files = dir.listFiles();
        if (null != files) {
            for (File file : files) {
                try{
                    file.delete();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dir.delete();
    }

    /**
     * 转换MultipartFile对象为java.io.File类型
     *
     * @param multipartFile
     * @return
     */
    public static File convertMultipartFileToFile(MultipartFile multipartFile) {
        File result = null;
        try {
            result = File.createTempFile(UUID.randomUUID().toString(), null);
            multipartFile.transferTo(result);
            result.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据url获取文件对象
     *
     * @param fileUrl
     * @return
     */
    public static File downloadFile(String fileUrl) {
        File result = null;
        try {
            result = File.createTempFile(UUID.randomUUID().toString(), null);
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(3000);
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(result));
            byte[] car = new byte[1024];
            int l = 0;
            while ((l = bis.read(car)) != -1) {
                bos.write(car, 0, l);
            }
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
