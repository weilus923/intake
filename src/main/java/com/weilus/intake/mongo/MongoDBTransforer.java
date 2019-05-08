package com.weilus.intake.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.weilus.intake.LogReader;
import com.weilus.intake.LogTransforer;
import com.weilus.intake.conf.IntakeProperties;
import org.bson.Document;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by liutq on 2019/3/21.
 */
public class MongoDBTransforer implements LogTransforer {
    public static final Logger LOGGER = Logger.getLogger(MongoDBTransforer.class.getName());
    private MongoClient mongoClient;
    private MongoClientURI mongoClientURI;
    private String collection = "intake";
    private IntakeProperties properties;

    public MongoDBTransforer(String uri, String collection,IntakeProperties properties) {
        this.mongoClientURI = new MongoClientURI(uri);
        this.mongoClient = new MongoClient(mongoClientURI);
        if(collection != null && collection.length() > 0)this.collection = collection;
        this.properties = properties;
    }

    @Override
    public void readAndWrite(Path log_path, Path logpospath) {
        LogReader<Document> reader = new LogReader(properties,new MongoDocumentParser(properties));
        reader.readLastLine(log_path, logpospath, (docs, num) -> out(docs));
    }

    private void out(List<Document> list) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
        MongoCollection collection = mongoDatabase.getCollection(this.collection);
        if(list.size() > 0)collection.insertMany(list);
    }
}
