package com.reeking.ssm;

import com.reeking.ssm.entity.AdminUser;
import com.reeking.ssm.service.AdminUserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by zhuru on 2018/12/22.
 */
@RunWith(SpringJUnit4ClassRunner.class) // 指定测试用例的运行器，这里指定的JUnit4
@ContextConfiguration("classpath:applicationContext.xml")
public class AdminUserServiceTest {

    @Autowired
    private AdminUserService adminUserService;

    @Test
    public void getAdminUserById() {
        AdminUser adminUser = adminUserService.getAdminUserById((long) 112);
        Assert.assertTrue(adminUser == null);
    }
}
