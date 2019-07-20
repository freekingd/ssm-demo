package com.reeking.ssm.dao;

import com.reeking.ssm.entity.Picture;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuru on 2018/12/18.
 */
public interface PictureDao {

    /**
     * 查询图片列表
     * @param map
     * @return
     */
    List<Picture> findPictures(Map<String, Object> map);

    /**
     * 根据id查询图片
     * @param id
     * @return
     */
    Picture findPictureById(Integer id);

    /**
     * 获取图片总数
     * @param map
     * @return
     */
    int getTotalPictures(Map<String, Object> map);

    /**
     * 新增图片保存
     * @param picture
     * @return
     */
    int savePicture(Picture picture);

    /**
     * 根据图片id删除图片
     * @param id
     * @return
     */
    int deletePictureById(Integer id);

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
