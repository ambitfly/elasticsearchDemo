package com.hpu;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Match {
    public static void main(String[] args) {
        //1.连解rest接口
        HttpHost http = new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder builder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        //2.封装请求对象
        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name","手机");
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        //3.获取响应结果
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            System.out.println("totalHits="+totalHits);
            SearchHit[] hits = searchHits.getHits();
            for(SearchHit searchHit:hits){
                System.out.println(searchHit.getSourceAsString());
            }
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
