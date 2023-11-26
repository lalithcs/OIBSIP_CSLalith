package com.example.oibsip_cslalith;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField trainNumberField;

    @FXML
    private TextField classField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField destinationField;

    @FXML
    private Button reserveButton;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/OReserveSys";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "lalith1.";
    private Stage primaryStage;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    private void handleReservation(ActionEvent event) {
        String name = nameField.getText();
        String trainNumber = trainNumberField.getText();
        String travelClass = classField.getText();
        String date = dateField.getText();
        String source = sourceField.getText();
        String destination = destinationField.getText();
        boolean isReserved = reserveTicket(name, trainNumber, travelClass, date, source, destination);

        if (isReserved) {
            showAlert(Alert.AlertType.INFORMATION, "Reservation Success", null, "Ticket reserved successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Reservation Failed", null, "Failed to reserve the ticket.");
        }
        // Perform reservation logic (replace with actual reservation process)
        // For demonstration purposes, show reservation details in an alert
        String reservationInfo = "Name: " + name + "\nTrain Number: " + trainNumber
                + "\nClass: " + travelClass + "\nDate: " + date
                + "\nFrom: " + source + "\nTo: " + destination;

        showAlert(Alert.AlertType.INFORMATION, "Reservation Details", null, reservationInfo);
    }
    private boolean reserveTicket(String name, String trainNumber, String travelClass, String date,
                                  String source, String destination) {
        // JDBC connection and insertion logic
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO Tickets (Name, TrainNumber, TravelClass, Date, Source, Destination) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, trainNumber);
            preparedStatement.setString(3, travelClass);
            preparedStatement.setString(4, date);
            preparedStatement.setString(5, source);
            preparedStatement.setString(6, destination);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
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
}
