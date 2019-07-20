package com.reeking.ssm.service.impl;

import com.reeking.ssm.dao.AdminUserDao;
import com.reeking.ssm.entity.AdminUser;
import com.reeking.ssm.service.AdminUserService;
import com.reeking.ssm.utiles.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuru on 2018/12/16.
 */
@Service("adminUserService")
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserDao adminUserDao;

    @Override
    public AdminUser getAdminUserById(Long id) {
        return adminUserDao.getAdminUserById(id);
    }

    @Override
    public AdminUser getAdminUserByUserName(String userName) {
        return adminUserDao.getAdminUserByUserName(userName);
    }

    @Override
    public int addUser(AdminUser adminUser) {
        adminUser.setPassword(MD5Util.MD5Encode(adminUser.getPassword(), "UTF-8"));
        return adminUserDao.addUser(adminUser);
    }

    @Override
    public int updateUserPassword(AdminUser adminUser) {
        String newPassword = MD5Util.MD5Encode(adminUser.getPassword(), "UTF-8");
        return adminUserDao.updateUserPassword(adminUser.getId(), newPassword);
    }

    @Override
    public PageResult findAdminUsers(PageUtil pageUtil) {
        List<AdminUser> users = adminUserDao.findAdminUsers(pageUtil);
        int totalCount = adminUserDao.getTotalUsers(pageUtil);
        PageResult pageResult = new PageResult(users, pageUtil.getPage(), totalCount, pageUtil.getLimit());
        return pageResult;
    }

    @Override
    public AdminUser updateTokenAndLogin(String userName, String password) {
        AdminUser adminUser = adminUserDao.getAdminUserByUserNameAndPassword(userName, MD5Util.MD5Encode(password, "UTF-8"));
        if (adminUser != null) {
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", adminUser.getId());
            if (adminUserDao.updateUserToken(adminUser.getId(), token) > 0) {
                //返回数据时带上token
                adminUser.setUserToken(token);
                return adminUser;
            }
        }
        return null;
    }

    /**
     * 获取token值
     * @param sessionId
     * @param userId
     * @return
     */
    private String getNewToken(String sessionId, Long userId) {
        String src = sessionId + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public AdminUser getAdminUserByToken(String userToken) {
        return adminUserDao.getAdminUserByToken(userToken);
    }

    @Override
    public List<AdminUser> getUsersForExport() {
        return adminUserDao.getAllAdminUsers();
    }

    @Override
    public int deleteBatch(Long[] ids) {
        return adminUserDao.deleteBatch(ids);
    }

    @Override
    public int importUsersByExcelFile(File file) {
        XSSFSheet xssfSheet = null;
        try {
            //读取file对象并转换为XSSFSheet类型对象进行处理
            xssfSheet = PoiUtil.getXSSFSheet(file);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        List<AdminUser> adminUsers = new ArrayList<>();
        //第一行是表头因此默认从第二行读取
        for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
            //按行读取数据
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                //实体转换
                AdminUser adminUser = convertXSSFRowToAdminUser(xssfRow);
                //用户验证 已存在或者为空则不进行insert操作
                if (!StringUtils.isEmpty(adminUser.getUserName()) && !StringUtils.isEmpty(adminUser.getPassword()) && getAdminUserByUserName(adminUser.getUserName()) == null) {
                    adminUsers.add(adminUser);
                }
            }
        }
        //判空
        if (!CollectionUtils.isEmpty(adminUsers)) {
            //adminUsers用户列表不为空则执行批量添加sql
            return adminUserDao.insertUsersBatch(adminUsers);
        }
        return 0;
    }

    /**
     * 方法抽取
     * 将解析的列转换为AdminUser对象
     *
     * @param xssfRow
     * @return
     */
    private AdminUser convertXSSFRowToAdminUser(XSSFRow xssfRow) {
        AdminUser adminUser = new AdminUser();
        //用户名
        XSSFCell userName = xssfRow.getCell(0);
        //密码
        XSSFCell orinalPassword = xssfRow.getCell(1);
        //设置用户名
        if (!StringUtils.isEmpty(userName)) {
            adminUser.setUserName(PoiUtil.getValue(userName));
        }
        //对读取的密码进行加密并设置到adminUser对象中
        if (!StringUtils.isEmpty(orinalPassword)) {
            adminUser.setPassword(MD5Util.MD5Encode(PoiUtil.getValue(orinalPassword), "UTF-8"));
        }
        return adminUser;
    }
}
