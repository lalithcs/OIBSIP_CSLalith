package com.example.oibsip_cslalith;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.Optional;

public class CancellationController {

    @FXML
    private TextField pnrField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label trainNumberLabel;

    @FXML
    private Label dateOfJourneyLabel;

    @FXML
    private Label sourceLabel;

    @FXML
    private Label destinationLabel;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/OReserveSys";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "lalith1.";

    @FXML
    private void retrieveTicketDetails(ActionEvent event) {
        String pnr = pnrField.getText().trim();
        if (!pnr.isEmpty()) {
            String query = "SELECT * FROM Ticket WHERE PNR = ?";
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, pnr);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    String trainNumber = resultSet.getString("train_no");
                    String dateOfJourney = resultSet.getString("doj");
                    String source = resultSet.getString("Source");
                    String destination = resultSet.getString("Destination");

                    // Display retrieved ticket details in labels
                    nameLabel.setText(name);
                    trainNumberLabel.setText(trainNumber);
                    dateOfJourneyLabel.setText(dateOfJourney);
                    sourceLabel.setText(source);
                    destinationLabel.setText(destination);
                    confirmCancellation(event);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Ticket Not Found", null, "No ticket found with this PNR.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", null, "Failed to retrieve ticket details.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Empty PNR", null, "Please enter a PNR number.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean confirmationNeeded = false;

    @FXML
    private void confirmCancellation(ActionEvent event) {
        String pnr = pnrField.getText().trim();
        if (!pnr.isEmpty()) {
            if (confirmationNeeded) {
                // If the confirmation is already needed, proceed with deletion without further confirmation
                deleteTicket(pnr);
            } else {
                // If this is the first attempt at cancellation, set the flag and ask for confirmation
                confirmationNeeded = true;
                showAlertConfirmation(pnr);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Empty PNR", null, "Please enter a PNR number.");
        }
    }

    private void showAlertConfirmation(String pnr) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to cancel the ticket with PNR: " + pnr + "?");

        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (((Optional<?>) result).isPresent() && result.get() == buttonTypeYes) {
            deleteTicket(pnr);
        } else {
            confirmationNeeded = false; // Reset confirmation flag if cancel is clicked
        }
    }

    private void deleteTicket(String pnr) {
        String query = "DELETE FROM Ticket WHERE PNR = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, pnr);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Cancellation Successful", null, "Ticket canceled successfully!");
                clearLabels();
            } else {
                showAlert(Alert.AlertType.ERROR, "Cancellation Failed", null, "Failed to cancel the ticket.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", null, "Failed to cancel the ticket.");
        }
    }

    private void clearLabels() {
        nameLabel.setText("");
        trainNumberLabel.setText("");
        dateOfJourneyLabel.setText("");
        sourceLabel.setText("");
        destinationLabel.setText("");
    }
}
