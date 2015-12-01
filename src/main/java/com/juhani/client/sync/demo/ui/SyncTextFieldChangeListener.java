package com.juhani.client.sync.demo.ui;

import com.juhani.client.sync.demo.mongo.MongoUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class SyncTextFieldChangeListener implements ChangeListener<String> {
  private boolean syncValueTokenChecked = false;
  private boolean syncValueToken = false;
  private DemoClient dc=null;
  
  public SyncTextFieldChangeListener(DemoClient client){
    dc = client;
  }
  
  
  public void changed(ObservableValue<? extends String> observable, String oldValue,
      String newValue) {
    if (!syncValueTokenChecked) {
      syncValueToken = MongoUtil.getInstance().getAndSetToken();
      syncValueTokenChecked = true;
      dc.setEditMode(true);
    }
  }  
}
