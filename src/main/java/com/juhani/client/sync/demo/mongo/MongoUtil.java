package com.juhani.client.sync.demo.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoUtil {

  private static MongoUtil instance = null;

  private MongoClient mongoClient = null;
  private String toKenCollectionName = MongoConstants.SyncTokenCollectionName;
  private String valueCollectionName = MongoConstants.SyncValueCollectionName;
  private String dbName = MongoConstants.SyncDBName;
  private DB db = null;


  public static synchronized MongoUtil getInstance() {
    if (instance == null) {
      instance = new MongoUtil();
    }
    return instance;
  }

  private MongoUtil() {
    mongoClient = new MongoClient("localhost", 27017);
    db = getDB();
  }

  private DB getDB() {
    if (db == null) {
      db = mongoClient.getDB(dbName);
    }
    return db;
  }

  public void upsertToken(int id, long tokenNo) {
    DBCollection col = db.getCollection(toKenCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, id);

    DBObject update =
        new BasicDBObject(MongoConstants.SyncId, id).append(MongoConstants.SyncToken, tokenNo);

    DBObject upsertStatement = new BasicDBObject("$set", update);

    try {
      col.update(query, upsertStatement, true, false);
    } catch (MongoException e) {
      System.out.println("mongo throws exception in upsertToken");
    }

  }

  public void upsertSyncContent(int id, long tokenNo, String value) {
    DBCollection col = db.getCollection(valueCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, id);

    DBObject update =
        new BasicDBObject(MongoConstants.SyncId, id).append(MongoConstants.SyncToken, tokenNo)
            .append(MongoConstants.SyncValue, value);

    DBObject upsertStatement = new BasicDBObject("$set", update);

    try {
      col.update(query, upsertStatement, true, false);
    } catch (MongoException e) {
      System.out.println("mongo throws exception in upsertToken");
    }
  }

}
