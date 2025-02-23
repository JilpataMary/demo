package com.godown.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TextField searchField;
    @FXML private TableView<Customer> customerTableView;

    private ObservableList<Customer> customerData;

    @FXML
    public void initialize() {
        // Initialize the TableView columns
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());

        // Load and display the customers
        loadCustomerData();

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterCustomers(newValue));
    }

    private void loadCustomerData() {
        List<Customer> customers = CustomerDatabase.getCustomers();
        customerData = FXCollections.observableArrayList(customers);
        customerTable.setItems(customerData);
    }

    private void filterCustomers(String query) {
        if (query == null || query.isEmpty()) {
            customerTable.setItems(customerData);
        } else {
            List<Customer> filteredCustomers = customerData.stream()
                .filter(c -> {
                    String name = c.getName();
                    return (name != null && name.toLowerCase().contains(query.toLowerCase())) ||
                           c.getPhoneNumber().contains(query);
                })
                .collect(Collectors.toList());
            customerTable.setItems(FXCollections.observableArrayList(filteredCustomers));
        }
    }

    @FXML
    private void onEdit(ActionEvent event) {
        // Get the selected customer from the TableView
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            // Show an edit dialog (you can customize it with fields to edit customer name/phone)
            TextInputDialog dialog = new TextInputDialog(selectedCustomer.getName());
            dialog.setTitle("Edit Customer");
            dialog.setHeaderText("Edit Customer Name");
            dialog.setContentText("Customer Name:");

            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    selectedCustomer.setName(name);
                    CustomerDatabase.saveCustomers();  // Save changes
                    loadCustomerData();  // Refresh the TableView
                }
            });
        } else {
            showAlert("Error", "No customer selected for editing.");
        }
    }

    @FXML
    private void onDelete(ActionEvent event) {
        // Get the selected customer from the TableView
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            // Confirm the deletion
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this customer?");
            confirmationAlert.setContentText("Customer: " + selectedCustomer.getName());
            
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Call deleteCustomer method and pass both the customer and the controller
                    CustomerDatabase.deleteCustomer(selectedCustomer, this);
                }
            });
        } else {
            showAlert("Error", "Please select a customer to delete.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onSearch(KeyEvent event) {
        String query = searchField.getText();
        filterCustomers(query);
    }
    
    public void refreshCustomerList() {
        // Get the updated list of customers
        List<Customer> updatedCustomers = CustomerDatabase.getCustomers();
        
        // Convert to ObservableList and set it to the correct TableView
        ObservableList<Customer> observableCustomerList = FXCollections.observableArrayList(updatedCustomers);
        
        // Use customerTable instead of customerTableView
        customerTable.setItems(observableCustomerList);
    }

}
