package com.weilus.intake;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

import static com.weilus.intake.LogParserUtil.*;

/**
 * Created by liutq on 2019/3/21.
 */
public class MongoDBTransforer implements LogTransforer{

    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    public MongoDBTransforer(String uri) {
        this.mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
    }

    @Override
    public void out(List<String> lines) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
        MongoCollection collection = mongoDatabase.getCollection("logs");
        List<Document> list = lines.stream()
                .map(line->
                        new Document()
                                .append("time",LogParserUtil.getMatcher(REG_DATE_YMDHMSS,line))
                                .append("level",LogParserUtil.getMatcher(REG_LEVEL,line))
                                .append("spanid",LogParserUtil.getMatcher(REG_SELTH,line))
                                .append("logger",LogParserUtil.getMatcher(REG_CLZZ,line))
                                .append("message",LogParserUtil.parseMsg(line))
        ).collect(Collectors.toList());
        collection.insertMany(list);
    }
}
