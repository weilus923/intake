package com.weilus.intake;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
        List<Document> list = trans(lines,source);
        if(list.size() > 0)collection.insertMany(list);
    }


    public List<Document> trans(List<String> lines,String source){
        List<Document> result = new ArrayList<>();
        if(null != lines) {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Document doc = trans(line);
                if(doc == null)continue;
                else doc.append("source", source);
                if (LogParserUtil.isExceptionLine(line)) {
                    String nextLine;
                    List<String> stacktrace = new ArrayList<>();
                    getStacktrace:
                    do {
                        if(i++ < lines.size()) {
                            nextLine = lines.get(i);
                            if (LogParserUtil.isExceptionLine(nextLine)) stacktrace.add(nextLine);
                            else {
                                i--;
                                break getStacktrace;
                            }
                        }else break;
                    } while (true);
                    doc.append("exception", stacktrace);
                }
                result.add(doc);
            }
        }
        return result;
    }

    private Document trans(String line){
        try {
            return new Document()
                    .append("time",LogParserUtil.parseTime(line))
                    .append("level", LogParserUtil.getMatcher(REG_LEVEL, line))
                    .append("spanid", LogParserUtil.getMatcher(REG_SELTH, line))
                    .append("logger", LogParserUtil.getMatcher(REG_CLZZ, line))
                    .append("message", LogParserUtil.parseMsg(line));
        }catch (ParseException e){
            e.printStackTrace();
            LOGGER.info("parse error: "+ line);
        }
        return null;
    }

}
