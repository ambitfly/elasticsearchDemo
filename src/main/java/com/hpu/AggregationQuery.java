package com.hpu;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AggregationQuery {
    public static void main(String[] args) {
        //1.连解rest接口
        HttpHost http = new HttpHost("127.0.0.1",9200,"http");
        RestClientBuilder builder = RestClient.builder(http);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        //2.封装请求对象
        SearchRequest searchRequest = new SearchRequest("sku");
       searchRequest.types("doc");
       SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchSourceBuilder.size(0);
        searchRequest.source(searchSourceBuilder);




        //3.获取响应结果

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String,Aggregation> map = aggregations.getAsMap();
            Terms terms = (Terms) map.get("sku_category");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for(Terms.Bucket bucket:buckets){
                System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
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
