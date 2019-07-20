package com.reeking.ssm.service;

import com.reeking.ssm.entity.AdminUser;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;

import java.io.File;
import java.util.List;

/**
 * Created by zhuru on 2018/12/16.
 */
public interface AdminUserService {

    /**
     * 根据用户id获取用户记录
     * @param id
     * @return
     */
    AdminUser getAdminUserById(Long id);


    /**
     * 根据用户名获取用户记录
     * @param userName
     * @return
     */
    AdminUser getAdminUserByUserName(String userName);

    /**
     * 新增用户
     * @param adminUser
     * @return
     */
    int addUser(AdminUser adminUser);

    /**
     * 修改用户密码
     * @param adminUser
     * @return
     */
    int updateUserPassword(AdminUser adminUser);

    /**
     * 获取用户列表数据
     * @param pageUtil
     * @return
     */
    PageResult findAdminUsers(PageUtil pageUtil);

    /**
     * 更新token，登陆
     * @param userName
     * @param password
     * @return
     */
    AdminUser updateTokenAndLogin(String userName, String password);

    /**
     * 根据token获取用户记录
     * @param userToken
     * @return
     */
    AdminUser getAdminUserByToken(String userToken);

    /**
     * 根据excel导入用户记录
     *
     * @param file
     * @return
     */
    int importUsersByExcelFile(File file);

    /**
     * 获取导出数据
     *
     * @return
     */
    List<AdminUser> getUsersForExport();

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    int deleteBatch(Long[] ids);

}
