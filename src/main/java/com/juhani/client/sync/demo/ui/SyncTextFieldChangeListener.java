package com.juhani.client.sync.demo.ui;

import com.juhani.client.sync.demo.mongo.MongoUtil;
import com.juhani.client.sync.demo.shared.TokenOperand;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class SyncTextFieldChangeListener implements ChangeListener<String> {
  private boolean syncValueTokenChecked = false;
  private TokenOperand tokenOperand = null;
  private DemoClient dc = null;
  private String clientName = "";

  public SyncTextFieldChangeListener(DemoClient client, String name) {
    dc = client;
    clientName = name;
  }


  public void changed(ObservableValue<? extends String> observable, String oldValue,
      String newValue) {
    if (!syncValueTokenChecked) {
      tokenOperand = MongoUtil.getInstance().queryToken(Constants.SyncFieldId);
      if (tokenOperand.isTokenTaken()
          && (!tokenOperand.getClientName().equalsIgnoreCase(clientName))) {
        syncValueTokenChecked = false;
        dc.handleEditingConflict(tokenOperand.getClientName());
      } else if (!tokenOperand.isTokenTaken())
        // token not yet taken or taken is free
        MongoUtil.getInstance().upsertToken(Constants.SyncFieldId, clientName,
            Constants.TokenTaken);
      syncValueTokenChecked = true;
      dc.setEditMode(true);
    } else {
      // token hold by client himself/herself
      // do nothing
    }
  }
}
