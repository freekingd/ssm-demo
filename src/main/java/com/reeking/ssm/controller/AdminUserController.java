package com.reeking.ssm.controller;

import com.reeking.ssm.common.Constants;
import com.reeking.ssm.common.Result;
import com.reeking.ssm.common.ResultGenerator;
import com.reeking.ssm.controller.annotation.TokenToUser;
import com.reeking.ssm.entity.AdminUser;
import com.reeking.ssm.service.AdminUserService;
import com.reeking.ssm.utiles.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * Created by zhuru on 2018/12/16.
 */
@RestController
@RequestMapping("/users")
public class AdminUserController {

    final static Logger logger = Logger.getLogger(AdminUserController.class);

    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody AdminUser user) {
        logger.info("请求登陆，参数为：{" + user.toString() + "}");
        Result result = ResultGenerator.genFailResult("登陆失败！");
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
            logger.error("未填写登陆信息，登陆失败");
            result.setMessage("登录名或密码不能为空！");
        }
        AdminUser adminUser = adminUserService.updateTokenAndLogin(user.getUserName(), user.getPassword());
        if (adminUser != null) {
            logger.info("登陆成功，用户名：" + adminUser.getUserName());
            result = ResultGenerator.genSuccessResult(adminUser);
        }
        return result;
    }

    /**
     * 查询用户列表
     * @param param
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(@RequestParam Map<String, Object> param) {
        if (StringUtils.isEmpty(param.get("page")) || StringUtils.isEmpty(param.get("limit"))) {
            logger.error("查询用户列表失败：参数错误");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误❌！");
        }
        PageUtil pageUtil = new PageUtil(param);
        PageResult pageResult = adminUserService.findAdminUsers(pageUtil);
        logger.info("查询用户列表成功");
        return ResultGenerator.genSuccessResult(pageResult);
    }

    /**
     * 保存新增用户
     * @param adminUser
     * @param loginUser
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result addUser(@RequestBody AdminUser adminUser, @TokenToUser AdminUser loginUser) {
        if (null == loginUser) {
            logger.error("新增用户失败：未登录");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录");
        }
        if (StringUtils.isEmpty(adminUser.getUserName()) || StringUtils.isEmpty(adminUser.getPassword())) {
            logger.error("新增用户失败：参数错误");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误！");
        }
        AdminUser tempUser = adminUserService.getAdminUserByUserName(adminUser.getUserName());
        if (tempUser != null) {
            logger.error("新增用户失败：该用户名已存在【" + tempUser.getUserName() +  "】");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "该用户名已存在！");
        }
        if ("admin".equals(adminUser.getUserName().trim())) {
            logger.error("新增用户失败：不能添加admin用户");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "不能添加admin用户！");
        }
        if (adminUserService.addUser(adminUser) > 0) {
            logger.info("新增用户成功，用户名：" + adminUser.getUserName());
            return ResultGenerator.genSuccessResult();
        }
        logger.error("新增用户失败," + adminUser.toString());
        return ResultGenerator.genFailResult("新增失败！");
    }

    /**
     * 修改用户密码
     * @param adminUser 
     * @param loginUser
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result editUser(@RequestBody AdminUser adminUser, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            logger.error("修改用户密码失败，未登录");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录");
        }
        if (StringUtils.isEmpty(adminUser.getPassword())) {
            logger.error("修改用户密码失败，新密码为空");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "请输入新密码！");
        }
        AdminUser tempUser = adminUserService.getAdminUserById(adminUser.getId());
        if (tempUser == null) {
            logger.error("修改用户密码失败，指定用户不存在");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "该用户不存在！");
        }
        if ("admin".equals(tempUser.getUserName().trim())) {
            logger.error("修改用户密码失败，不能修改admin用户的密码");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "不能修改admin用户的密码！");
        }
        tempUser.setPassword(adminUser.getPassword());
        if (adminUserService.updateUserPassword(tempUser) > 0) {
            logger.info("修改用户密码成功，" + tempUser.toString());
            return ResultGenerator.genSuccessResult();
        }
        logger.error("修改用户密码失败，" + tempUser.toString());
        return ResultGenerator.genFailResult("修改失败");
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            logger.error("请求删除用户失败，未登录");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
        }
        if (ids.length < 1) {
            logger.error("请求删除用户失败，参数异常");
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        if (adminUserService.deleteBatch(ids) > 0) {
            logger.info("请求删除用户成功 " + Arrays.toString(ids));
            return ResultGenerator.genSuccessResult();
        } else {
            logger.error("请求删除用户失败 " + Arrays.toString(ids));
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * 批量导入用户V1
     * <p>
     * 批量导入用户(直接导入)
     */
    @RequestMapping(value = "/importV1", method = RequestMethod.POST)
    public Result saveByExcelFileV1(@RequestParam("file") MultipartFile multipartFile) {
        File file = FileUtil.convertMultipartFileToFile(multipartFile);
        if (file == null) {
            logger.error("批量导入用户失败，未获取到数据");
            return ResultGenerator.genFailResult("导入失败");
        }
        int importResult = adminUserService.importUsersByExcelFile(file);
        if (importResult > 0) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(importResult);
            logger.info("批量导入用户成功");
            return result;
        } else {
            logger.error("批量导入用户失败");
            return ResultGenerator.genFailResult("导入失败");
        }
    }

    /**
     * 批量导入用户V2
     * <p>
     * 批量导入用户(根据文件url导入)
     */
    @RequestMapping(value = "/importV2", method = RequestMethod.POST)
    public Result saveByExcelFileV2(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            logger.error("批量导入用户失败，未获取到文件");
            return ResultGenerator.genFailResult("fileUrl不能为空");
        }
        File file = FileUtil.downloadFile(fileUrl);
        if (file == null) {
            logger.error("批量导入用户失败，未获取到文件");
            return ResultGenerator.genFailResult("文件不存在");
        }
        int importResult = adminUserService.importUsersByExcelFile(file);
        if (importResult > 0) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(importResult);
            logger.info("批量导入用户成功");
            return result;
        } else {
            logger.error("批量导入用户失败");
            return ResultGenerator.genFailResult("导入失败");
        }
    }


    /**
     * 文件导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportUsers(HttpServletRequest request, HttpServletResponse response) {
        List<AdminUser> userList = adminUserService.getUsersForExport();
        //单元格表头
        String[] excelHeader = {"用户id", "用户名", "账号状态", "添加时间"};
        //字段名称
        String[] fileds = {"userId", "userName", "status", "createTime"};
        //单元格宽度内容格式
        int[] formats = {4, 2, 1, 1};
        //单元格宽度
        int[] widths = {256 * 14, 512 * 14, 256 * 14, 512 * 14};
        try {
            List<Map<String, Object>> excelData = new ArrayList<Map<String, Object>>();
            if (CollectionUtils.isNotEmpty(userList)) {
                for (AdminUser user : userList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", user.getId());
                    map.put("userName", user.getUserName());
                    map.put("status", user.getIsDeleted() == 0 ? "正常账号" : "废弃账号");
                    map.put("createTime", DateUtil.getDateString(user.getCreateTime()));
                    excelData.add(map);
                }
            }
            String excelName = "用户数据_" + System.currentTimeMillis();
            PoiUtil.exportFile(excelName, excelHeader, fileds, formats, widths, excelData, request, response);
            logger.info("文件导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件导出失败");
        }
    }
}
