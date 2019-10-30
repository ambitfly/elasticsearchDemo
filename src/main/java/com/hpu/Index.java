package com.hpu;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.lucene.index.CheckIndex;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.transport.TransportRequestOptions;

import java.io.IOException;
import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

public class Index {
    public static void main(String[] args) {
        //1.连解rest接口
        HttpHost http = new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder builder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        //2.封装请求对象
        IndexRequest indexRequest = new IndexRequest("sku","doc","6");
        Map<String,Object> skuMap = new HashMap();

        skuMap.put("name","华为p30pro");
        skuMap.put("brandName","华为");
        skuMap.put("categoryName","手机");
        skuMap.put("price",1010221);
        skuMap.put("createTime","2019-05-01");
        skuMap.put("saleNum",101021);
        skuMap.put("commentNum",10102321);
        Map<String,Object> spec=new HashMap();
        spec.put("网络制式","移动4G");
        spec.put("屏幕尺寸","5");
        skuMap.put("spec",spec);

        indexRequest.source(skuMap);
        //3.获取响应结果
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
            int status  = response.status().getStatus();
            System.out.println(status);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
