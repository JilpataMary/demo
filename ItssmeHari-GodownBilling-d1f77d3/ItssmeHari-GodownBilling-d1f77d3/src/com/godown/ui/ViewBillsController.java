package com.godown.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
//import java.util.stream.Collectors;

public class ViewBillsController {

    @FXML private TableColumn<BillItem, Integer> billNumberColumn;
    @FXML private TableColumn<BillItem, String> dateColumn;
    @FXML private TableColumn<BillItem, String> customerNameColumn;
    @FXML private TableColumn<BillItem, String> riceVarietyColumn;
    @FXML private TableColumn<BillItem, Double> weightColumn;
    @FXML private TableColumn<BillItem, Double> costColumn;
    @FXML private TableColumn<BillItem, Double> totalPriceColumn;
    @FXML private TableView<BillItem> billsTable;
    @FXML private DatePicker datePicker; // Reference to the search TextField

    private ObservableList<BillItem> bills;  // List of all bills

    @FXML
    public void initialize() {
        bills = FXCollections.observableArrayList(BillDatabase.getAllBills());

        // Set up the columns
        billNumberColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        dateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        riceVarietyColumn.setCellValueFactory(new PropertyValueFactory<>("riceVariety"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPerKg"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

     // Filter bills based on date selected in the DatePicker
        setupDateFilter();

        // Load bills into the table
        loadBills();
    }

    private void loadBills() {
        List<BillItem> billList = BillDatabase.getAllBills();
        bills.setAll(billList);
        billsTable.setItems(bills);
    }

    private void setupDateFilter() {
        // Create a filtered list that is tied to the bills list
        FilteredList<BillItem> filteredBills = new FilteredList<>(bills, p -> true);

        // Add listener to the DatePicker to update the filter dynamically
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredBills.setPredicate(bill -> {
                // If the DatePicker is not set, show all bills
                if (newValue == null) {
                    return true;
                }

                // Compare the selected date with the BillItem's date
                LocalDate billDate = bill.getDate(); // Assuming BillItem has a getDate() method
                return billDate != null && billDate.isEqual(newValue); // Match by exact date
            });
        });

        // Bind the filtered list to the TableView
        billsTable.setItems(filteredBills);
    }
}
