package com.juhani.client.sync.demo.worker;

import java.util.List;

import com.juhani.client.sync.demo.mongo.MongoUtil;
import com.juhani.client.sync.demo.shared.TokenOperand;
import com.juhani.client.sync.demo.ui.Constants;
import com.juhani.client.sync.demo.ui.DemoClient;

public class SyncWorker implements Runnable {
  private DemoClient dc = null;
  String oldValue = "";

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
        MongoUtil.getInstance().getSyncFieldValue(Constants.SyncFieldId);
    if (!newValue.equalsIgnoreCase(oldValue)) {
      dc.updateSyncField(newValue);
      oldValue = newValue;
    }

    List<TokenOperand> fieldsDisableList =
        MongoUtil.getInstance().queryTokenMode(Constants.TokenTaken);  
    dc.disableFieldsEditingMode(fieldsDisableList);
    
    List<TokenOperand> fieldsEnableList =
        MongoUtil.getInstance().queryTokenMode(Constants.TokenFree);    
    dc.enableFieldsEditingMode(fieldsEnableList);
   
  }

}
