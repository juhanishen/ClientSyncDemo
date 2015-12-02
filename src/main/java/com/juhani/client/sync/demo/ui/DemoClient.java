package com.juhani.client.sync.demo.ui;

import com.juhani.client.sync.demo.mongo.MongoUtil;
import com.juhani.client.sync.demo.shared.TokenOperand;
import com.juhani.client.sync.demo.worker.SyncWorker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DemoClient extends Application {
  TextField syncValueTextField = new TextField();
  Label syncNameHintLabel = new Label("hintLabel:");

  boolean editMode = false;
  SyncTextFieldChangeListener syncTextValueListener = null;


  final int j = 0;
  SyncWorker syncWorker = null;

  public static String clientName = "";

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("please give clientName");
      System.exit(-1);
    }
    clientName = args[0];

    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    SyncTextFieldChangeListener syncTextValueListener =
        new SyncTextFieldChangeListener(this, clientName);

    primaryStage.setTitle("I am:" + clientName);


    Label syncNameLabel = new Label("SyncField:");

    HBox hb = new HBox();
    Button btn = new Button();
    btn.setText("submit change");
    hb.getChildren().addAll(syncNameLabel, syncValueTextField, btn);
    hb.setSpacing(10);
    VBox vb = new VBox();
    vb.setSpacing(5);
    syncNameHintLabel.setMaxWidth(450);
    vb.getChildren().addAll(hb,syncNameHintLabel);


    btn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        System.out.println("Update value to db");
        MongoUtil.getInstance().upsertSyncContent(Constants.SyncFieldId, Constants.SyncTokenNo,
            syncValueTextField.getText());
        MongoUtil.getInstance().upsertToken(Constants.SyncFieldId, clientName, Constants.TokenFree);
        // edit mode finished
        setEditMode(false);
      }
    });



    if (!editMode) {
      syncValueTextField.textProperty().addListener(syncTextValueListener);
    } else {
      // edit mode true: remove tf property change listener
      syncValueTextField.textProperty().removeListener(syncTextValueListener);
    }

    syncValueTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
      public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
          Boolean newPropertyValue) {
        TokenOperand tokenOperand = MongoUtil.getInstance().queryToken(Constants.SyncFieldId);
        if (tokenOperand.isTokenTaken()
            && (!tokenOperand.getClientName().equalsIgnoreCase(clientName))) {
          // token taken, but not hold by client himself/herself
          handleEditingConflict(tokenOperand.getClientName());
        } else if (!tokenOperand.isTokenTaken()) {
          // token not taken
          MongoUtil.getInstance().upsertToken(Constants.SyncFieldId, clientName,
              Constants.TokenTaken);
          setEditMode(true);
        } else {
          // token taken by client himself/herself
          // do nothing
        }

      }
    });


    StackPane root = new StackPane();
    root.getChildren().add(vb);
    primaryStage.setScene(new Scene(root, 500, 450));
    primaryStage.show();

    syncWorker = new SyncWorker(this);
    Thread sync = new Thread(syncWorker);
    sync.start();
    syncNameLabel.requestFocus();
  }

  public void updateSyncField(final String newValue) {
    if (!editMode) {
      // editmode false, client shall aware of change, disable listener first, prevent recursive
      // call
      if (syncTextValueListener != null) {
        syncValueTextField.textProperty().removeListener(syncTextValueListener);
      }
      Platform.runLater(new Runnable() {
        public void run() {
          syncValueTextField.setText(newValue);
          if (syncTextValueListener != null) {
            syncValueTextField.textProperty().addListener(syncTextValueListener);
          }
        }
      });
    }
  }

  public void setEditMode(boolean b) {
    editMode = b;
    if (editMode) {
      // if editmode is true, there is no need to invoke tf property change listener
      if (syncTextValueListener != null) {
        syncValueTextField.textProperty().removeListener(syncTextValueListener);
      }
      Platform.runLater(new Runnable() {
        public void run() {
          syncNameHintLabel.setText(Constants.HoldingToken);
        }
      });
    } else {
      if (syncTextValueListener != null) {
        syncValueTextField.textProperty().addListener(syncTextValueListener);
      }
    }
  }

  public void handleEditingConflict(final String editedClientName) {
    Platform.runLater(new Runnable() {
      public void run() {
        editMode = false;
        syncNameHintLabel.setText(Constants.EditTokenRejected+","+editedClientName+" is Editing");
        syncNameHintLabel.requestFocus();
      }
    });

  }
}
