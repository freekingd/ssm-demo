package com.reeking.ssm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by zhuru on 2018/12/23.
 */
@RestController
@RequestMapping("/test")
public class FileTest {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void test(HttpServletResponse response) {
        File file = new File("D:\\Image\\deer.jpg");
        if (file.exists()) {
            try {
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                String CONTENT_TYPE = "application/octest-stream";
                response.setCharacterEncoding("utf-8");
                response.setContentType(CONTENT_TYPE);
                response.addHeader("Content-disposition", "attachment;filename=deer.jpg");
                OutputStream out = new BufferedOutputStream(response.getOutputStream());
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0 , len);
                    out.flush();
                }
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
