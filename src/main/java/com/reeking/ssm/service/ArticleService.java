package com.reeking.ssm.service;


import com.reeking.ssm.entity.Article;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author 13
 * @date 2018-08-15
 */
public interface ArticleService {

	PageResult getArticlePage(PageUtil pageUtil);

	Article queryObject(Integer id);

	List<Article> queryList(Map<String, Object> map);

	int queryTotal(Map<String, Object> map);

	int save(Article article);

	int update(Article article);

	int delete(Integer id);

	int deleteBatch(Integer[] ids);

    void test(ExecutorService exec);
}
