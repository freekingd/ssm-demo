package com.reeking.ssm.service;

import com.reeking.ssm.entity.Picture;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;

/**
 * Created by zhuru on 2018/12/18.
 */
public interface PictureService {

    /**
     * 获取图片列表信息
     * @param pageUtil
     * @return
     */
    PageResult getPicturePage(PageUtil pageUtil);

    /**
     * 根据id获取图片
     * @param id
     * @return
     */
    Picture getPictureById(Integer id);

    /**
     * 保存图片
     * @param picture
     * @return
     */
    int savePicture(Picture picture);

    /**
     * 根据id删除图片
    * @param id
     * @return
     */
    int deletePicture(Integer id);

    /**
     * 批量删除图片
     * @param ids
     * @return
     */
    int deleteBatch(Integer[] ids);

    /**
     * 更新图片信息
     * @param picture
     * @return
     */
    int updatePicture(Picture picture);
}
