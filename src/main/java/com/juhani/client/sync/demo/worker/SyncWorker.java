package com.juhani.client.sync.demo.worker;

import com.juhani.client.sync.demo.mongo.MongoUtil;
import com.juhani.client.sync.demo.ui.Constants;
import com.juhani.client.sync.demo.ui.DemoClient;

public class SyncWorker implements Runnable {
  private DemoClient dc = null;

  public SyncWorker(DemoClient client) {
    dc = client;
  }

  public void run() {
    while (true) {
      syncFieldsAndNotify();
      try {
        Thread.sleep(WorkConstants.OneSeconds);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private void syncFieldsAndNotify() {
    String newValue =
        MongoUtil.getInstance().getSyncFieldValue(Constants.SyncFieldId, Constants.SyncTokenNo);
    dc.updateSyncField(newValue);
  }

}
