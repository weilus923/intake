package com.weilus.test;

import com.weilus.intake.LogReader;
import com.weilus.intake.MongoDBTransforer;
import org.bson.Document;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by liutq on 2019/4/9.
 */
public class LogReaderTest {
    public static void main(String[] args) {
        MongoDBTransforer transforer = new MongoDBTransforer("mongodb://weilua:weilus@192.168.198.128:27017/test","intake");
        Path logPath = Paths.get("D:\\tmp\\logs\\dms.log");
        Path logpospath =Paths.get("D:\\tmp\\logs\\dms.log.data");
        LogReader.readLastLine(logPath,logpospath,(lines,num)->{
            List<Document> docs = transforer.trans(lines,"test");
            System.out.println(docs);
        });
    }
}
