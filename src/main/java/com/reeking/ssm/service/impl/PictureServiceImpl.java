package com.reeking.ssm.service.impl;

import com.reeking.ssm.dao.PictureDao;
import com.reeking.ssm.entity.Picture;
import com.reeking.ssm.service.PictureService;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhuru on 2018/12/18.
 */
@Service("pictureService")
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureDao pictureDao;

    @Override
    public PageResult getPicturePage(PageUtil pageUtil) {
        List<Picture> pictures = pictureDao.findPictures(pageUtil);
        int total = pictureDao.getTotalPictures(pageUtil);
        return new PageResult(pictures, pageUtil.getPage(), total, pageUtil.getLimit());
    }

    @Override
    public Picture getPictureById(Integer id) {
        return pictureDao.findPictureById(id);
    }

    @Override
    public int savePicture(Picture picture) {
        return pictureDao.savePicture(picture);
    }

    @Override
    public int deletePicture(Integer id) {
        return pictureDao.deletePictureById(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return pictureDao.deleteBatch(ids);
    }

    @Override
    public int updatePicture(Picture picture) {
        return pictureDao.updatePicture(picture);
    }
}
