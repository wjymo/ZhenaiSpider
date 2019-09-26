package com.wjy.service.store;

import com.wjy.dao.UserProfileDAO;
import com.wjy.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.util.List;

@Slf4j
@Component
public class StoreService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Async
    public void stoeUsers(List<UserProfile> list){
        SqlSession openSession=sqlSessionFactory.openSession(ExecutorType.BATCH);
        //批量保存执行前时间
        long start=System.currentTimeMillis();
        try{
            UserProfileDAO userProfileDAO=openSession.getMapper(UserProfileDAO.class);
            for (UserProfile userProfile : list) {
                userProfileDAO.insertUserProfile(userProfile);
            }
            openSession.commit();
            long end=  System.currentTimeMillis();
            //批量保存执行后的时间
            System.out.println("执行时长"+(end-start));
            //插入成功后清空列表，避免重复插入
            list.clear();
        }catch (Exception e){
            log.error("批量插入数据库失败：{}",e.getMessage(),e);
        }finally{
            openSession.close();
        }

    }
}
