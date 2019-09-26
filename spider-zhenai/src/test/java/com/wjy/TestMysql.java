package com.wjy;

import com.google.common.collect.Maps;
import com.wjy.dao.UserProfileDAO;
import com.wjy.entity.UserProfile;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestMysql {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void testBatchInsert(){
        //可以执行批量操作的sqlSession
        SqlSession openSession=sqlSessionFactory.openSession(ExecutorType.BATCH);

        //批量保存执行前时间
        long start=System.currentTimeMillis();
        try{
            UserProfileDAO userProfileDAO=openSession.getMapper(UserProfileDAO.class);
            Map<String, String> map =  Maps.newHashMap();
            map.put("xx","xx");


            for (int i = 0; i < 1000; i++) {
                userProfileDAO.insertUserProfile(UserProfile.builder().userId("112233").city("镇州").username("huyao")
                        .userImg("xxx//xxx").age("12").maritalStatus("sdas")
                        .constellation("白羊").height("192cm").weight("67").workplace("fsgsd").income("sdsad")
                        .job("jj").education("大专").baseContent("...").interests(map.toString())
                        .nation("中国").birthplace("大连").bodyType("运动").smoke("chou")
                        .drink("he").house("da").car("no").children("si").toHaveChildren("sas")
                        .marriedTime("dsdsd").extraContent("xx...").criteriaContent("xxxxx:"+i)
                        .build());
            }
            openSession.commit();
            long end=  System.currentTimeMillis();
            //批量保存执行后的时间
            System.out.println("执行时长"+(end-start));
            //批量 预编译sql一次==》设置参数==》10000次==》执行1次   677
            //非批量  （预编译=设置参数=执行 ）==》10000次   1121
        }catch (Exception e){
            System.out.println(1);
            e.printStackTrace();
        }finally{
            openSession.close();
        }
    }

}
