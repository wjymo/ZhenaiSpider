package com.wjy;

import com.google.common.collect.Maps;
import com.wjy.entity.UserProfile;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestEs {
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void testEsGet(){
        GetRequest getRequest=new GetRequest("ecommerce","_doc","1");
        Map map=new HashMap();
        GetResponse response=null;
        try{
            response= client.get(getRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.isExists()){
            Map<String, Object> source = response.getSource();
        }
        System.out.println(1);
    }

    @Test
    public void testPost() throws IOException {
        UserProfile userProfile = UserProfile.builder().userId("112233").age("12").city("镇州")
                .constellation("ddd")
//                .interests((Map<String, String>) Maps.newHashMap().put("兴趣1", "杀"))
                .height("192cm").build();
        XContentBuilder builder=null;
        IndexRequest request=new IndexRequest("zhenai");
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
                    .field("age",userProfile.getAge()).field("city",userProfile.getCity())
                    .field("constellation",userProfile.getConstellation())
                    .field("interests",userProfile.getInterests())
                    .field("height",userProfile.getHeight())
                    .endObject()
            ;
        } catch (IOException e) {
            e.printStackTrace();
        }

        request.id(userProfile.getUserId()).opType("create").type("_doc").source(builder);

        IndexResponse response=client.index(request,RequestOptions.DEFAULT);
        RestStatus status = response.status();
        System.out.println(1);

    }
}
