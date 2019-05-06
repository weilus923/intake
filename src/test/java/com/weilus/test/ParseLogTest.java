package com.weilus.test;

import com.weilus.intake.Application;
import com.weilus.intake.IntakeProperties;
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
        LinkedHashMap<String,Object> config = Application.loadConfig(new String[]{});
        Map<String,Object> conf = (Map<String, Object>) config.get("zuul");
        String mongo = String.valueOf(conf.get("mongo"));
        String collection = conf.containsKey("collection") ? String.valueOf(conf.get("collection")) : null;
        String path = String.valueOf(conf.get("path"));
        String file = String.valueOf(conf.get("file"));
        String source = String.valueOf(conf.get("source"));
        String time_format = String.valueOf(conf.get("time_format"));
        LinkedHashMap<String,String> pattern = (LinkedHashMap<String, String>) conf.get("pattern");
        IntakeProperties properties = new IntakeProperties(path,file,source,time_format,pattern);
        MongoDBTransforer transforer = new MongoDBTransforer(mongo,collection,properties);
        Document doc = transforer.trans(line);
        System.out.println(doc);
    }

}
