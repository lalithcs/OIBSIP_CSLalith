package com.example.oibsip_cslalith;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField trainNumberField;

    @FXML
    private TextField classField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField destinationField;

    @FXML
    private Button reserveButton;
    @FXML
    private Label trainNameLabel;


    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/OReserveSys";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "lalith1.";
    private Stage primaryStage;
    private Connection connection;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleReservation(ActionEvent event) {
        // Remaining code for handling reservations
        String name = nameField.getText();
        String trainNumber = trainNumberField.getText();
        showTrainName(event);
        String travelClass = classField.getText();
        String date = String.valueOf(dateField.getValue());
        String source = sourceField.getText();
        String destination = destinationField.getText();

        boolean isReserved = reserveTicket(name, trainNumber, travelClass, date, source, destination);

        if (isReserved) {
            showAlert(Alert.AlertType.INFORMATION, "Reservation Success", null, "Ticket reserved successfully!");
            String reservationInfo = "Name: " + name + "\nTrain Number: " + trainNumber
                    + "\nClass: " + travelClass + "\nDate: " + date
                    + "\nFrom: " + source + "\nTo: " + destination + "\nPNR:"+ getPnr(name);

            showAlert(Alert.AlertType.INFORMATION, "Reservation Details", null, reservationInfo);
        } else {
            showAlert(Alert.AlertType.ERROR, "Reservation Failed", null, "Failed to reserve the ticket.");
        }


    }

    private String getPnr(String name) {
        String PNR = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT Pnr FROM ticket WHERE name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                PNR = resultSet.getString("Pnr");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PNR;
    }

    private boolean reserveTicket(String name, String trainNumber, String travelClass, String date,
                                  String source, String destination) {
        // Remaining code for ticket reservation
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO Ticket (Name, train_no,class, doj, Source, Destination) " +
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

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        // Remaining code for showing alerts
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        trainNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            String trainName = fetchTrainName(newValue);
            trainNameLabel.setText(trainName);
        });
    }

    private String fetchTrainName(String trainNumber) {
        String name = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT train_number FROM Train WHERE train_no = ?");
            preparedStatement.setString(1, trainNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("train_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    @FXML
    private void showTrainName(ActionEvent event) {
        String trainNumber = trainNumberField.getText();
        String trainName = fetchTrainName(trainNumber);
        if (!trainName.isEmpty()) {
            nameField.setText(trainName);
        } else {
            showAlert(Alert.AlertType.ERROR, "Train Not Found", null, "Train number not found in the database.");
        }
    }
    @FXML
    private Button cancelButton;

    @FXML
    private void handleCancellation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cancellation.fxml"));
            Parent root = loader.load();
            CancellationController controller = new CancellationController();
            loader.setController(controller);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Set the stage with the new scene
            stage.setScene(scene);
            stage.show();// Close the current window
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
