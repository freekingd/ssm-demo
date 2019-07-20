package com.reeking.ssm.service.impl;

import com.reeking.ssm.common.Constants;
import com.reeking.ssm.dao.ArticleDao;
import com.reeking.ssm.entity.Article;
import com.reeking.ssm.redis.RedisUtil;
import com.reeking.ssm.service.ArticleService;
import com.reeking.ssm.utiles.PageResult;
import com.reeking.ssm.utiles.PageUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service("articleService")
public class ArticleServiceImpl implements ArticleService {

    final static Logger logger = Logger.getLogger(ArticleServiceImpl.class);

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private ArticleDao articleDao;

    @Override
    public PageResult getArticlePage(PageUtil pageUtil) {
        List<Article> articleList = articleDao.findArticles(pageUtil);
        int total = articleDao.getTotalArticles(pageUtil);
        PageResult pageResult = new PageResult(articleList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Article queryObject(Integer id) {
        Article article = (Article) redisUtil.get(Constants.ARTICLE_CACHE_KEY + id, Article.class);
        if (article != null) {
            logger.info("在redis缓存中存在id为：" + id + "的Article信息");
            return article;
        }
        Article articleFromDB = articleDao.getArticleById(id);
        if (articleFromDB != null) {
            redisUtil.put(Constants.ARTICLE_CACHE_KEY + id, articleFromDB);
            logger.info("redis缓存中不存在id为：" + id + "的Article，已置入缓存");
        }
        return articleFromDB;
    }

    @Override
    public List<Article> queryList(Map<String, Object> map) {
        List<Article> articles = articleDao.findArticles(map);
        return articles;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return articleDao.getTotalArticles(map);
    }

    @Override
    public int save(Article article) {
        if (articleDao.insertArticle(article) > 0) {
            redisUtil.put(Constants.ARTICLE_CACHE_KEY + article.getId(), article);
            return 1;
        }
        return 0;
    }

    @Override
    public int update(Article article) {
        article.setUpdateTime(new Date());
        if (articleDao.updArticle(article) > 0 ) {
            redisUtil.del(Constants.ARTICLE_CACHE_KEY + article.getId());
            redisUtil.put(Constants.ARTICLE_CACHE_KEY + article.getId(), article);
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(Integer id) {
        if (articleDao.delArticle(id) > 0) {
            redisUtil.del(Constants.ARTICLE_CACHE_KEY + id);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        if (articleDao.deleteBatch(ids) > 0) {
            for (Integer id : ids) {
                redisUtil.del(Constants.ARTICLE_CACHE_KEY + id);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public void test(ExecutorService exec) {
        Date date = new Date();
        // 创建线程池
        for(int i = 0; i < 100; i++) {
          exec.submit(new Runnable() {
              @Override
              public void run() {
                  for(int i = 0; i < 100; i++) {
                    Article article = new Article();
                    article.setAddName("reeking");
                    article.setCreateTime(date);
                    article.setArticleTitle("连接池性能测试：" + i + "线程：" + Thread.currentThread().getId());
                    article.setArticleContent("连接池性能测试：" + i + "线程：" + Thread.currentThread().getName());
                    articleDao.insertArticle(article);
                  }
              }
          });
        }
    }
}
