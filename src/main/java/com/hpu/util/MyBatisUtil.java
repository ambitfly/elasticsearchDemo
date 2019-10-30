package com.hpu.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtil {
    private static volatile SqlSessionFactory sqlSessionFactory;

    public static SqlSession getSqlSession() {
        try {
            if (sqlSessionFactory == null) {
                //读取主配置文件
                InputStream input = Resources.getResourceAsStream("mybatis.xml");
                synchronized (MyBatisUtil.class) {
                    if (sqlSessionFactory == null){
                        sqlSessionFactory = new SqlSessionFactoryBuilder().build(input);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sqlSessionFactory.openSession();
    }


}
