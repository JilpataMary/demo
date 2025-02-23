package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AddStockController {

    @FXML private TextField varietyField, priceField, quantityField;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File stockFile = new File("stock.json");
    private InventoryController inventoryController; // Reference to update UI

    // Method to set InventoryController reference
    public void setInventoryController(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    @FXML
    private void handleSaveStock() {
        try {
            // Validate input
            String variety = varietyField.getText().trim();
            if (variety.isEmpty() || priceField.getText().isEmpty() || quantityField.getText().isEmpty()) {
                showAlert("Error", "All fields must be filled!");
                return;
            }

            double price;
            int quantity;
            try {
                price = Double.parseDouble(priceField.getText());
                quantity = Integer.parseInt(quantityField.getText());
                if (price <= 0 || quantity <= 0) {
                    showAlert("Error", "Price and quantity must be positive numbers!");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid number format!");
                return;
            }

            // Load existing stock
            List<RiceStock> stocks = new ArrayList<>();
            if (stockFile.exists() && stockFile.length() > 0) {
                stocks = objectMapper.readValue(stockFile, new TypeReference<List<RiceStock>>() {});
            }

            // Check if the variety already exists with the same cost
            boolean updated = false;
            for (RiceStock stock : stocks) {
                if (stock.getRiceVariety().trim().equalsIgnoreCase(variety)) {
                    // If cost matches, update quantity
                    if (stock.getCost() == price) {
                        stock.setQuantity(stock.getQuantity() + quantity);
                        updated = true;
                        break;
                    } else {
                        // If cost is different, update the existing entry instead of adding a new one
                        stock.setCost(price);
                        stock.setQuantity(quantity);
                        updated = true;
                        break;
                    }
                }
            }

            // If no matching variety was found, add a new entry
            if (!updated) {
                stocks.add(new RiceStock(variety, price, quantity));
            }

            // Save updated stock list
            objectMapper.writeValue(stockFile, stocks);

            // Refresh the UI in InventoryController
            refreshInventoryUI();

            showAlert("Success", updated ? "Stock updated successfully!" : "New stock added!");
            closeWindow();
        } catch (IOException e) {
            showAlert("Error", "Failed to save stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Refresh inventory UI
    private void refreshInventoryUI() {
        if (inventoryController != null) {
            inventoryController.loadInventoryTable();
        }
    }

    // Helper method to close the window
    private void closeWindow() {
        Stage stage = (Stage) varietyField.getScene().getWindow();
        stage.close();
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
