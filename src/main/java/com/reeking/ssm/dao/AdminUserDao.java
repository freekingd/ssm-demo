package com.reeking.ssm.dao;

import com.reeking.ssm.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuru on 2018/12/16.
 */
public interface AdminUserDao {

    /**
     * 根据用户名获取用户记录
     * @param userName
     * @return
     */
    AdminUser getAdminUserByUserName(String userName);

    /**
     * 根据id获取用户记录
     * @param id
     * @return
     */
    AdminUser getAdminUserById(Long id);

    /**
     * 新增用户
     * @param adminUser
     * @return
     */
    int addUser(AdminUser adminUser);

    /**
     * 修改用户密码
     * @param userId
     * @param newPassword
     * @return
     */
    int updateUserPassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    /**
     * 查询用户列表
     * @param param
     * @return
     */
    List<AdminUser> findAdminUsers(Map param);

    /**
     * 查询用户总数
     * @param param
     * @return
     */
    int getTotalUsers(Map param);

    /**
     * 根据用户名和密码获取用户记录
     * @param userName
     * @param passwordMD5
     * @return
     */
    AdminUser getAdminUserByUserNameAndPassword(@Param("userName") String userName, @Param("passwordMD5") String passwordMD5);

    /**
     * 根据token获取用户记录
     * @param userToken
     * @return
     */
    AdminUser getAdminUserByToken(String userToken);

    /**
     *  更新用户token
     * @param userId
     * @param newToken
     * @return
     */
    int updateUserToken(@Param("userId") Long userId, @Param("newToken") String newToken);

    /**
     * 批量新增用户记录
     *
     * @return
     */
    int insertUsersBatch(@Param("adminUsers") List<AdminUser> adminUsers);

    /**
     * 查询所有用户列表
     *
     * @return
     */
    List<AdminUser> getAllAdminUsers();

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    int deleteBatch(Long[] ids);
}
