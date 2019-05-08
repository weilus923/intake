package com.weilus.test;

import com.weilus.intake.Application;
import com.weilus.intake.conf.IntakeProperties;
import com.weilus.intake.mongo.MongoDocumentParser;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试解析日志
 *
 * @author 刘太全
 * @program intake
 * @date 2019-05-06 09:50
 **/
public class ParseLogTest {


    public static void main(String[] args) throws IOException {
//        String line = "2019-03-22 15:05:41.737  INFO [zuul,,,] 243916 --- [           main] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@d29f28: startup date [Fri Mar 22 15:05:41 CST 2019]; root of context hierarchy\n";

//        String line = "2019-04-23 10:28:57.849  WARN 59544 --- [dockerEvent-1] c.w.config.AbstractDockerEventConfig     : event collecting .. org.apache.http.conn.ConnectTimeoutException: Connect to 192.168.198.3:2375 [/192.168.198.3] failed: Read timed out\n";
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

        BufferedReader reader = Files.newBufferedReader(Paths.get("D:\\data\\logs\\zuul.log.1"));
        List<String> lines = reader.lines().limit(300).collect(Collectors.toList());
        List<Document> docs = new MongoDocumentParser(properties).parseLog(lines);
        System.out.println(docs);
    }

}
