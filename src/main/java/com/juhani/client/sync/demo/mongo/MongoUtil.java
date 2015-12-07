package com.juhani.client.sync.demo.mongo;

import java.util.ArrayList;
import java.util.List;

import com.juhani.client.sync.demo.shared.TokenOperand;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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

  public void upsertToken(int id, String clientName, int token) {
    DBCollection col = db.getCollection(toKenCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, id);

    DBObject update = new BasicDBObject(MongoConstants.SyncId, id)
        .append(MongoConstants.SyncToken, token).append(MongoConstants.ClientName, clientName);

    DBObject upsertStatement = new BasicDBObject("$set", update);

    try {
      col.update(query, upsertStatement, true, false);
    } catch (MongoException e) {
      System.out.println("mongo throws exception in upsertToken");
    }

  }

  public void upsertSyncContent(int id, String value) {
    DBCollection col = db.getCollection(valueCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, id);

    DBObject update =
        new BasicDBObject(MongoConstants.SyncId, id).append(MongoConstants.SyncValue, value);

    DBObject upsertStatement = new BasicDBObject("$set", update);

    try {
      col.update(query, upsertStatement, true, false);
    } catch (MongoException e) {
      System.out.println("mongo throws exception in upsertToken");
    }
  }

  public TokenOperand queryToken(int id) {
    DBCollection col = db.getCollection(toKenCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, id);

    int tokenStatus = -1;
    String clientName = "";
    DBCursor cursor = col.find(query);
    if (cursor.size() > 1) {
      // log error
      System.out.println("error find multiple token for syncId:" + id);
    }

    TokenOperand operand = new TokenOperand();
    while (cursor.hasNext()) {
      DBObject obj = cursor.next();
      if (obj != null && obj.get(MongoConstants.SyncToken) != null
          && obj.get(MongoConstants.ClientName) != null) {
        tokenStatus = ((Integer) obj.get(MongoConstants.SyncToken)).intValue();
        clientName = (String) obj.get(MongoConstants.ClientName);
        operand.setClientName(clientName);
        if (tokenStatus > 0) {
          operand.setTokenTaken(true);
        } else {
          operand.setTokenTaken(false);
        }
      }
    }
    return operand;
  }



  public String getSyncFieldValue(int syncFieldId) {
    DBCollection col = db.getCollection(valueCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncId, syncFieldId);
    String ret = "";
    DBCursor cursor = col.find(query);
    while (cursor.hasNext()) {
      DBObject obj = cursor.next();
      if (obj != null && obj.get(MongoConstants.SyncValue) != null) {
        ret = (String) obj.get(MongoConstants.SyncValue);
      }
    }

    return ret;
  }

  public List<TokenOperand> queryTokenMode(int tokenEditing) {
    DBCollection col = db.getCollection(toKenCollectionName);

    DBObject query = new BasicDBObject(MongoConstants.SyncToken, tokenEditing);

    int syncId = -1;
    String clientName = "";


    DBCursor cursor = col.find(query);
    List<TokenOperand> tokenModeList = new ArrayList<TokenOperand>();
    while (cursor.hasNext()) {
      TokenOperand operand = new TokenOperand();
      DBObject obj = cursor.next();
      if (obj != null && obj.get(MongoConstants.SyncId) != null
          && obj.get(MongoConstants.ClientName) != null) {
        syncId = ((Integer) obj.get(MongoConstants.SyncId)).intValue();
        clientName = (String) obj.get(MongoConstants.ClientName);
        operand.setSyncId(syncId);
        operand.setClientName(clientName);
        if (tokenEditing > 0) {
          operand.setTokenTaken(true);
        } else {
          operand.setTokenTaken(false);
        }
      }
      tokenModeList.add(operand);
    }
    return tokenModeList;
  }
}
