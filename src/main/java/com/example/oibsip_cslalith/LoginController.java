package com.example.oibsip_cslalith;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private Stage primaryStage;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/OReserveSys";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "lalith1.";
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin(ActionEvent event) throws ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", null, "Welcome, " + username + "!");
            // Navigate to the reservation or main system page here
            // Example: Load another FXML or switch scenes
            //loadReservationPage();
            openReservationPage(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", null, "Invalid username or password");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // If result set has at least one row, user is authenticated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Utility method to show an alert
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void loadReservationPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation.fxml"));


            // Pass the primary stage to the ReservationController
            ReservationController reservationController = new ReservationController();
            loader.setController(reservationController);
            Parent root = loader.load();
            //reservationController.setPrimaryStage(primaryStage);

            /*primaryStage.setTitle("Reservation Page");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openReservationPage(ActionEvent event) {
        try {
            // Load the ReservationController's FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservation.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            ReservationController reservationController = new ReservationController();
            loader.setController(reservationController);
            // Set up the stage and scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Set the stage with the new scene
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
}
