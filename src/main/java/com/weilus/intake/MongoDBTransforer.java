package com.weilus.intake;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.weilus.intake.LogParserUtil.*;

/**
 * Created by liutq on 2019/3/21.
 */
public class MongoDBTransforer implements LogTransforer{

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private String collection;
    public MongoDBTransforer(String uri,String collection) {
        this.mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
        this.collection = collection;
    }

    @Override
    public void out(List<String> lines) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
        MongoCollection collection = mongoDatabase.getCollection(this.collection);
        List<Document> list = lines.stream()
                .map(line->
                        new Document()
                                .append("time",toDate(LogParserUtil.getMatcher(REG_DATE_YMDHMSS,line)))
                                .append("level",LogParserUtil.getMatcher(REG_LEVEL,line))
                                .append("spanid",LogParserUtil.getMatcher(REG_SELTH,line))
                                .append("logger",LogParserUtil.getMatcher(REG_CLZZ,line))
                                .append("message",LogParserUtil.parseMsg(line))
        ).collect(Collectors.toList());
        collection.insertMany(list);
    }

    static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date toDate(String time){
        try {
            return SIMPLE_DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
