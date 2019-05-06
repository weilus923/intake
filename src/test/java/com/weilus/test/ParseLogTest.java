package com.weilus.test;

import com.weilus.intake.Application;
import com.weilus.intake.MongoDBTransforer;
import org.bson.Document;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 测试解析日志
 *
 * @author 刘太全
 * @program intake
 * @date 2019-05-06 09:50
 **/
public class ParseLogTest {


    public static void main(String[] args) {
        String line = "2019-03-22 15:05:41.737  INFO [zuul,,,] 243916 --- [           main] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@d29f28: startup date [Fri Mar 22 15:05:41 CST 2019]; root of context hierarchy\n";
        LinkedHashMap<String,Object> conf = Application.loadConfig(new String[]{});
        Map<String,Object> zuul = (Map<String, Object>) conf.get("zuul");
        LinkedHashMap<String,String> pattern = (LinkedHashMap<String, String>) zuul.get("pattern");
        Document doc = new MongoDBTransforer().trans(line,pattern);
        System.out.println(doc);
    }

}
