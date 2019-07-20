package com.reeking.ssm.controller;

import com.reeking.ssm.common.Constants;
import com.reeking.ssm.common.Result;
import com.reeking.ssm.common.ResultGenerator;
import com.reeking.ssm.controller.annotation.TokenToUser;
import com.reeking.ssm.entity.AdminUser;
import com.reeking.ssm.entity.Picture;
import com.reeking.ssm.service.PictureService;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by zhuru on 2018/12/18.
 */
@RestController
@RequestMapping("/pictures")
public class PictureController {

    @Autowired
    private PictureService pictureService;

    /**
     * 查询图片列表
     * @param param
     * @return
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> param) {
        if (StringUtils.isEmpty(param.get("page")) || StringUtils.isEmpty(param.get("limit"))) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误");
        }
        PageUtil pageUtil = new PageUtil(param);
        PageResult pageResult = pictureService.getPicturePage(pageUtil);
        if (pageResult == null) {
            return ResultGenerator.genFailResult("查询失败！");
        } else {
            return ResultGenerator.genSuccessResult(pageResult);
        }
    }

    /**
     * 保存图片
     * @param picture
     * @param loginUser
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result save(@RequestBody Picture picture, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "请先登陆！");
        }
        if (StringUtils.isEmpty(picture.getPath()) || StringUtils.isEmpty(picture.getRemark())) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误！");
        }
        if (pictureService.savePicture(picture) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("保存失败");
        }
    }

    /**
     * 根据id删除图片
     * @param id
     * @param loginUser
     * @return
     */
    @RequestMapping("/deleteById")
    public Result delete(@RequestBody Integer id, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "请先登录");
        }
        if (id < 1) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误");
        }
        if (pictureService.deletePicture(id) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * 批量删除图片
     * @param ids
     * @param loginUser
     * @return
     */
    @RequestMapping("/delete")
    public Result deleteBatch(@RequestBody Integer[] ids, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "请先登录");
        }
        if (ids == null || ids.length < 1) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误");
        }
        if (pictureService.deleteBatch(ids) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    /**
     * 根据id获取图片信息
     * @param id
     * @param loginUser
     * @return
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Integer id, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "未登录！");
        }
        if (id < 1) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        Picture picture = pictureService.getPictureById(id);
        if (picture == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        return ResultGenerator.genSuccessResult(picture);
    }

    /**
     * 更新图片
     * @param picture
     * @param loginUser
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Picture picture, @TokenToUser AdminUser loginUser) {
        if (loginUser == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_NOT_LOGIN, "请先登陆");
        }
        if (picture.getId() == null || StringUtils.isEmpty(picture.getPath()) || StringUtils.isEmpty(picture.getRemark())) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数错误");
        }
        Picture tempPicture = pictureService.getPictureById(picture.getId());
        if (tempPicture == null) {
            return ResultGenerator.genErrorResult(Constants.RESULT_CODE_PARAM_ERROR, "参数异常！");
        }
        if (pictureService.updatePicture(picture) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("更新失败");
        }
    }
}
