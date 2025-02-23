package com.godown.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InventoryController {

    @FXML private VBox contentPlaceholder;
    @FXML private Button addStockButton;
    @FXML private Button editStockButton;
    @FXML private Button deleteStockButton;
    @FXML private TableView<InventoryItem> stockTable;
    @FXML private TableColumn<InventoryItem, String> varietyColumn;
    @FXML private TableColumn<InventoryItem, Double> costColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML private TableView<RiceStock> inventoryTable;
    @FXML private Label totalStockLabel;

    private final File stockFile = new File("stock.json");
    private final ObjectMapper objectMapper = new ObjectMapper();
    
 //   private ObservableList<InventoryItem> riceStockList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        addStockButton.setOnAction(e -> handleAddStock());
        editStockButton.setOnAction(e -> handleEditStock());
        deleteStockButton.setOnAction(e -> handleDeleteStock());
        initializeTable();
        loadStockFromJson();
    }

    private void initializeTable() {
        varietyColumn.setCellValueFactory(cellData -> cellData.getValue().riceVarietyProperty());
        costColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        stockTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void handleAddStock() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Stock");
        dialog.setHeaderText("Enter new stock details");
        dialog.setContentText("Rice Variety:");

        Optional<String> varietyResult = dialog.showAndWait();
        if (varietyResult.isEmpty()) return;
        String variety = varietyResult.get().trim();

        double cost = promptForDouble("Enter Cost per kg", "Cost:");
        int quantity = (int) promptForDouble("Enter Quantity", "Quantity:");

        // Load existing stock from JSON file
        List<InventoryItem> currentStock = JsonUtility.readInventoryFromJson();

        // Add new stock item
        InventoryItem newItem = new InventoryItem(variety, cost, quantity, false);
        currentStock.add(newItem);

        // Write updated stock back to JSON file
        JsonUtility.writeInventoryToJson(currentStock);

        loadStockFromJson();  // Reload stock to refresh UI
        showAlert("Success", "Stock added successfully!");
    }


    private void handleEditStock() {
        InventoryItem selectedItem = stockTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a stock item to edit.");
            return;
        }

        double newCost = promptForDouble("Edit Cost", "New Cost:", selectedItem.getCost());
        int newQuantity = (int) promptForDouble("Edit Quantity", "New Quantity:", selectedItem.getQuantity());

        // Modify the selected item's properties
        selectedItem.setCost(newCost);
        selectedItem.setQuantity(newQuantity);

        // Load the current inventory from the file
        List<InventoryItem> currentStock = JsonUtility.readInventoryFromJson();

        // Update the modified item in the list (find the index of the item and update it)
        for (int i = 0; i < currentStock.size(); i++) {
            if (currentStock.get(i).getRiceVariety().equals(selectedItem.getRiceVariety())) {
                currentStock.set(i, selectedItem);  // Replace the item with the modified one
                break;
            }
        }

        // Save the updated inventory back to the JSON file
        JsonUtility.writeInventoryToJson(currentStock);

        loadStockFromJson();  // Reload stock to refresh UI
        showAlert("Updated", "Stock updated successfully.");
    }


    private void handleDeleteStock() {
        InventoryItem selectedItem = stockTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Selection", "Please select a stock item to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Stock");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Load current inventory
            List<InventoryItem> currentStock = JsonUtility.readInventoryFromJson();

            // Remove the selected item from the inventory
            currentStock.removeIf(item -> item.getRiceVariety().equals(selectedItem.getRiceVariety()));

            // Save the updated inventory back to the JSON file
            JsonUtility.writeInventoryToJson(currentStock);

            loadStockFromJson();  // Reload stock to refresh UI
            showAlert("Deleted", "Stock deleted successfully!");
        }
    }


    public void loadStockFromJson() {
        List<InventoryItem> inventory = JsonUtility.readInventoryFromJson(); // Read from JSON
        inventory.forEach(item -> System.out.println(item));
        int totalQuantity = inventory.stream().mapToInt(InventoryItem::getQuantity).sum();
        totalStockLabel.setText("Total Stock: " + totalQuantity);
        stockTable.setItems(FXCollections.observableArrayList(inventory));

        // Add this line to confirm the items are being set to the table
        System.out.println("Stock items set to the table: " + stockTable.getItems());
    }

    private double promptForDouble(String title, String content) {
        return promptForDouble(title, content, 0);
    }

    private double promptForDouble(String title, String content, double defaultValue) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(defaultValue));
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Double::parseDouble).orElse(defaultValue);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
            return defaultValue;
        }
    }
    
 // Method to refresh the inventory table by loading data from the file
    public void loadInventoryTable() {
        try {
            List<RiceStock> stocks = loadStocksFromFile();  // Load the updated stock data
            ObservableList<RiceStock> stockList = FXCollections.observableArrayList(stocks);
            inventoryTable.setItems(stockList);  // Update the TableView with new data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 // Method to load stock data from the file (could be moved to a utility class if needed)
    private List<RiceStock> loadStocksFromFile() throws IOException {
        if (stockFile.exists() && stockFile.length() > 0) {
            return objectMapper.readValue(stockFile, new TypeReference<List<RiceStock>>() {});
        }
        return new ArrayList<>();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f0f0f0;");
        alert.showAndWait();
    }
}
