package com.reeking.ssm;

import com.reeking.ssm.service.ArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhuru on 2018/12/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    public void DBCPTest() {
        Long begin = System.currentTimeMillis();
        ExecutorService exec = Executors.newFixedThreadPool(30);
        articleService.test(exec);
        exec.shutdown();
        while (true) {
            if (exec.isTerminated()) {
                Long end = System.currentTimeMillis();
                System.out.println("-----------(end-begin)值=" + (end-begin) + "," + "当前位置=ArticleServiceTest.DBCPTest()-----------");
                break;

            }
        }
    }
}
