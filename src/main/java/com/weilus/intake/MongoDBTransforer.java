package com.weilus.intake;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.weilus.intake.LogParserUtil.*;

/**
 * Created by liutq on 2019/3/21.
 */
public class MongoDBTransforer implements LogTransforer{
    public static final Logger LOGGER = Logger.getLogger(MongoDBTransforer.class.getName());
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private String collection = "intake";
    public MongoDBTransforer(String uri,String collection) {
        this.mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
        if(collection != null && collection.length() > 0)this.collection = collection;
    }

    @Override
    public void out(List<String> lines,String source) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
        MongoCollection collection = mongoDatabase.getCollection(this.collection);
        List<Document> list = lines.stream()
                .map(line-> {
                    try {
                        Document doc = new Document()
                                .append("time",LogParserUtil.parseTime(line))
                                .append("level", LogParserUtil.getMatcher(REG_LEVEL, line))
                                .append("spanid", LogParserUtil.getMatcher(REG_SELTH, line))
                                .append("logger", LogParserUtil.getMatcher(REG_CLZZ, line))
                                .append("message", LogParserUtil.parseMsg(line))
                                .append("source", source);
                        return doc;
                    }catch (Exception e){
                        LOGGER.info("parse error: "+ line);
                    }return null;
                })
                .filter(doc->doc != null)
                .collect(Collectors.toList());
        if(list.size() > 0)collection.insertMany(list);
    }


}
