package com.juhani.client.sync.demo.mongo;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMongoUtil {

  @Test
  public void testUpsertToken() {
    MongoUtil.getInstance().upsertToken(2,"dummy",4);
    // Todo fetch from mongo to see the change
    // now just check db manually
  }

  @Test
  public void testUpsertSyncContent() {
    MongoUtil.getInstance().upsertSyncContent(2, "token 3 change value");
    // Todo fetch from mongo to see the change
    // now just check db manually
  }

}
