package com.hpu.dao.impl;

import com.alibaba.fastjson.JSON;
import com.hpu.bean.Sku;
import com.hpu.dao.SkuDao;
import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuDaoImpl implements SkuDao{
    private SqlSession sqlSession;
    public Sku findById(String id) {
        Sku sku = null;
        try {
            //读取主配置文件
            InputStream input = Resources.getResourceAsStream("mybatis.xml");
            //创建SqlSessionFactory对象
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
            //创建SqlSession对象
            sqlSession = sessionFactory.openSession();
            //新增数据操作
            sku = sqlSession.selectOne("findById",id);
            //提交SqlSession
            sqlSession.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sqlSession != null){
                sqlSession.close();
            }
        }
        return sku;
    }

    public List<Sku> findAll() {
        List<Sku> skuList = null;

        try {
            //读取主配置文件
            InputStream input = Resources.getResourceAsStream("mybatis.xml");
            //创建SqlSessionFactory对象
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
            //创建SqlSession对象
            sqlSession = sessionFactory.openSession();
            //新增数据操作
            skuList = sqlSession.selectList("findAll");
            //提交SqlSession
            sqlSession.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqlSession != null){
                sqlSession.close();
            }
        }
        return skuList;
    }

    @Test
    public void fun(){
   //     List<Sku> skuList = findAll();
   //     System.out.println(skuList.get(0));
       importSku();

    }

    public   void importSku() {
        List<Sku> skuList = findAll();

        //1.连解rest接口

        HttpHost http = new HttpHost("127.0.0.1", 9200, "http");
        RestClientBuilder builder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        //2.封装请求对象
        BulkRequest bulkRequest = new BulkRequest();
        for (Sku sku : skuList) {
            IndexRequest indexRequest = new IndexRequest("sku", "doc", sku.getId());
            Map<String, Object> skuMap = new HashMap();

            skuMap.put("name", sku.getName());
            skuMap.put("brandName", sku.getBrandName());
            skuMap.put("categoryName", sku.getCategoryName());
            skuMap.put("price", sku.getPrice());
            skuMap.put("createTime", sku.getCreateTime());
            skuMap.put("saleNum", sku.getSaleNum());
            skuMap.put("image",sku.getImage());
            skuMap.put("commentNum", sku.getCommentNum());
            Map<String, Object> spec = JSON.parseObject(sku.getSpec());
            skuMap.put("spec", spec);

            indexRequest.source(skuMap);
            bulkRequest.add(indexRequest);
        }
        //3.获取响应结果
        int status = 0;
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest);
            status = bulkResponse.status().getStatus();
            System.out.println("status:"+status);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
