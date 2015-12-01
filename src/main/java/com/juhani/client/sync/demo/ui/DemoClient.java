package com.juhani.client.sync.demo.ui;

import com.juhani.client.sync.demo.mongo.MongoUtil;
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
import javafx.stage.Stage;

public class DemoClient extends Application {
  final TextField syncValueTextField = new TextField();
  boolean editMode = false;
  SyncTextFieldChangeListener syncTextValueListener = new SyncTextFieldChangeListener(this);

  final int j = 0;
  SyncWorker syncWorker = null;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    syncWorker = new SyncWorker(this);
    Thread sync = new Thread(syncWorker);
    sync.start();

    primaryStage.setTitle("Hello World!");


    Label syncNameLabel = new Label("SyncField:");

    HBox hb = new HBox();
    Button btn = new Button();
    btn.setText("submit change");
    hb.getChildren().addAll(syncNameLabel, syncValueTextField, btn);
    hb.setSpacing(10);
    btn.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent event) {
        System.out.println("Update value to db");
        MongoUtil.getInstance().upsertSyncContent(Constants.SyncFieldId, Constants.SyncTokenNo,
            syncValueTextField.getText());
        //edit mode finished
        setEditMode(false);
      }  
    });

    
    
    
    if (!editMode) {
      syncValueTextField.textProperty().addListener(syncTextValueListener);
    } else {
      // edit mode true: remove tf property change listener
      syncValueTextField.textProperty().removeListener(syncTextValueListener);
    }

    syncValueTextField.focusedProperty().addListener(new ChangeListener<Boolean>()
    {
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
        {
          setEditMode(true);
        }
    });
    
    
    StackPane root = new StackPane();
    root.getChildren().add(hb);
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }

  public void updateSyncField(final String newValue) {
    if (!editMode) {
      //editmode false, client shall aware of change, disable listener first, prevent recursive call
      syncValueTextField.textProperty().removeListener(syncTextValueListener);
      Platform.runLater(new Runnable() {
        public void run() {
          syncValueTextField.setText(newValue);
          syncValueTextField.textProperty().addListener(syncTextValueListener);
        }
      });
    }
  }

  public void setEditMode(boolean b) {
    editMode = b;
    if(editMode){
      //if editmode is true, there is no need to invoke tf property change listener
      syncValueTextField.textProperty().removeListener(syncTextValueListener);
    }else{
      syncValueTextField.textProperty().addListener(syncTextValueListener);
    }
  }
}
