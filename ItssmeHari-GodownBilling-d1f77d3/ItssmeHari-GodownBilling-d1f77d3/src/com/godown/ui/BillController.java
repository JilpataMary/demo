package com.godown.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;


public class BillController {

    @FXML private ComboBox<String> riceVarietyField;
    @FXML private Label priceLabel;
    @FXML private TextField weightField;
    @FXML private Label totalPriceLabel;
    @FXML private ComboBox<String> customerNameField;
    @FXML private TextField mobileNumberField;
    @FXML private Label dateTimeLabel;
    @FXML private Label availableStockLabel;

    private ObservableList<String> riceVarieties;
    private List<RiceStock> riceStocks;
    private List<Customer> customers;
 //   private InventoryController inventoryController = new InventoryController(); // Reference to the InventoryController
    private MainController mainController;  // Reference to MainController

    // Setter method to pass MainController
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    public void initialize() {
        try {
            // Step 1: Load inventory from JSON
            List<InventoryItem> inventoryList = JsonUtility.readInventoryFromJson();
            riceVarieties = FXCollections.observableArrayList();
            riceStocks = new ArrayList<>(); // Initialize riceStocks

            for (InventoryItem item : inventoryList) {
                riceVarieties.add(item.getRiceVariety());
                riceStocks.add(new RiceStock(item.getRiceVariety(), item.getCost(), item.getQuantity()));
            }

            // Step 2: Set items & enable editing
            riceVarietyField.setItems(riceVarieties);
            riceVarietyField.setEditable(true);

            // Step 3: Always show dropdown
            riceVarietyField.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
                filterRiceVarieties(newValue);
                riceVarietyField.show();
            });

            // Step 4: Handle selection
            riceVarietyField.setOnAction(this::handleRiceVarietySelection);

            // Step 5: Load customers & autofill on number input
            customers = CustomerDatabase.getCustomers();
            customerNameField.setEditable(true);
            mobileNumberField.textProperty().addListener((obs, oldVal, newVal) -> checkAndFillCustomer(newVal));

            // Add a listener to weightField to update total price in real-time
            weightField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateTotalPrice();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to filter rice varieties based on the typed text
    private void filterRiceVarieties(String typedText) {
        if (typedText == null || typedText.isEmpty()) {
            riceVarietyField.setItems(riceVarieties); // Reset to all varieties
        } else {
            ObservableList<String> filteredList = FXCollections.observableArrayList(
                riceVarieties.stream()
                    .filter(variety -> variety.toLowerCase().contains(typedText.toLowerCase()))
                    .collect(Collectors.toList())
            );
            riceVarietyField.setItems(filteredList); // Update the ComboBox with filtered items
            riceVarietyField.show();
        }
    }

    private void checkAndFillCustomer(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Only add customer once the phone number is 10 digits long
            if (phoneNumber.length() == 10) {
                Customer existingCustomer = CustomerDatabase.getCustomerByPhone(phoneNumber);

                if (existingCustomer != null) {
                    // If customer exists, autofill name
                    customerNameField.setValue(existingCustomer.getName());
                }
            }
        }
    }

    private void handleRiceVarietySelection(ActionEvent event) {
        if (riceStocks == null || riceStocks.isEmpty()) {
            System.out.println("❌ Error: riceStocks is null or empty!");
            return;
        }

        String selectedVariety = riceVarietyField.getSelectionModel().getSelectedItem();
        if (selectedVariety != null) {
            
        	//for debugging
        	//System.out.println("🟢 Selected Rice Variety: " + selectedVariety);

            RiceStock stock = riceStocks.stream()
                    .filter(s -> s.getRiceVariety().equalsIgnoreCase(selectedVariety))
                    .findFirst().orElse(null);

            if (stock != null) {
               
            	//for debugging 
            	// System.out.println("✅ Found Stock: " + stock);
                
            	priceLabel.setText(" ₹" + stock.getCost());
                availableStockLabel.setText(stock.getQuantity() + " kg");
                updateTotalPrice();
            } else {
                System.out.println("❌ No stock found for: " + selectedVariety);
            }
        }
    }

    private void updateTotalPrice() {
        String selectedVariety = riceVarietyField.getSelectionModel().getSelectedItem();
        String weightText = weightField.getText();

        if (selectedVariety == null || weightText.isEmpty()) {
            totalPriceLabel.setText("0.00");  // Reset if no selection or weight is empty
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            RiceStock stock = riceStocks.stream()
                .filter(s -> s.getRiceVariety().equalsIgnoreCase(selectedVariety))
                .findFirst().orElse(null);

            if (stock == null) {
                totalPriceLabel.setText("0.00");
                return;
            }

            double totalPrice = weight * stock.getCost();

            // Debugging - Check if total price is updating
           // System.out.println("Total Price Updated: " + totalPrice);

            // Update UI in JavaFX thread
            Platform.runLater(() -> totalPriceLabel.setText(String.format("%.2f", totalPrice)));

        } catch (NumberFormatException e) {
            totalPriceLabel.setText("0.00"); // Reset if invalid input
        }
    }

    @FXML
    private void generateBill() {
        String selectedVariety = riceVarietyField.getSelectionModel().getSelectedItem();
        String weightText = weightField.getText();
        String phoneNumber = mobileNumberField.getText();
        String customerName = customerNameField.getValue();

        if (selectedVariety == null || weightText.isEmpty()) {
            showAlert("Error", "Please complete all fields.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            RiceStock stock = riceStocks.stream()
                .filter(s -> s.getRiceVariety().equalsIgnoreCase(selectedVariety))
                .findFirst().orElse(null);

            if (stock == null || stock.getQuantity() < weight) {
                showAlert("Error", "Not enough stock available.");
                return;
            }

            // Deduct stock
            stock.setQuantity(stock.getQuantity() - (int) weight);
            InventoryManager.saveStockToJson(riceStocks); // Save updated stock

            // Ensure customer is stored
            checkAndFillCustomer(phoneNumber);
            if (CustomerDatabase.getCustomerByPhone(phoneNumber) == null) {
                Customer newCustomer = new Customer(customerName, phoneNumber);
                CustomerDatabase.addCustomer(newCustomer);
            }

            // Generate Unique Bill Number
            int billNumber = BillDatabase.generateNewBillNumber();

            // Calculate total price based on weight and cost per kg
            double costPerKg = stock.getCost();
            double totalPrice = weight * costPerKg;
            
         // Get the current date and time
            LocalDate date = LocalDate.now();

            // Save Bill Details
            BillItem bill = new BillItem(billNumber, date, customerName,selectedVariety,  weight, stock.getCost(), totalPrice);
            BillDatabase.saveBill(bill);
            
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a"));

            // Open invoice preview
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Invoice.fxml"));
            Parent root = loader.load();
            InvoiceController controller = loader.getController();
            ObservableList<BillItem> items = FXCollections.observableArrayList(bill);
            
            // new BillItem(selectedVariety, weightText, String.valueOf(stock.getCost()), String.valueOf(totalPrice)));
            controller.setInvoiceDetails(customerNameField.getValue(), mobileNumberField.getText(), timestamp, items, totalPrice, billNumber);

            Stage stage = new Stage();
            stage.setTitle("Invoice Preview");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh the stock in MainController
            if (mainController != null) {
                mainController.updateTotalSales();
            }

            // Refresh UI after stock update
            availableStockLabel.setText(stock.getQuantity() + " kg");
            
         // **Send Thank-You SMS**
            String smsMessage = "Thank you " + customerName + " for purchasing " + weight + "kg of " + selectedVariety +
                                ". Your bill amount is Rs." + totalPrice + ". - [Your Company Name]";
            GsmModemSMS.sendSMS(phoneNumber, smsMessage);

            System.out.println("Bill generated and SMS sent to " + phoneNumber);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid weight input. Please enter a number.");
        } catch (Exception e) {
            showAlert("Error", "System error occurred.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
