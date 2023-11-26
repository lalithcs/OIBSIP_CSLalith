package com.example.oibsip_cslalith;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OnlineReservationSystem extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));

        LoginController loginController = new LoginController(); // Replace with your actual controller class
        loader.setController(loginController);
        Parent root = loader.load();
        primaryStage.setTitle("Online Reservation System - Login");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
