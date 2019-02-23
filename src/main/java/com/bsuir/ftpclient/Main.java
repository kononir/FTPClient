package com.bsuir.ftpclient;

import com.bsuir.ftpclient.windows.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainWindow();
    }
}
