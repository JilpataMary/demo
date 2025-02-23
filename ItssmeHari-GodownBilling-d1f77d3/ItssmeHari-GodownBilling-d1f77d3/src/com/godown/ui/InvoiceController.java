package com.godown.ui;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class InvoiceController {
    @FXML private Label customerNameLabel;
    @FXML private Label customerPhoneLabel;
    @FXML
    private Label billDateLabel; // Use this instead of timestampLabel
    @FXML private Label totalAmountLabel;
    @FXML private Label billNumberLabel;
    @FXML private TableView<BillItem> billTable;
//    @FXML private TableColumn<BillItem, Integer> billNoColumn;
    @FXML private TableColumn<BillItem, String> riceVarietyColumn;
    @FXML private TableColumn<BillItem, Double> weightColumn;
    @FXML private TableColumn<BillItem, Double> costColumn;
    @FXML private TableColumn<BillItem, Double> totalPriceColumn;
    

    public void setInvoiceDetails(String customerName, String customerPhone, String timestamp,
                                  ObservableList<BillItem> billItems, double totalAmount, int billNumber) {
        customerNameLabel.setText(customerName);
        customerPhoneLabel.setText(customerPhone);
        billDateLabel.setText(timestamp);
        totalAmountLabel.setText("₹ " + String.format("%.2f", totalAmount));
        billNumberLabel.setText(String.valueOf(billNumber));

//        billNoColumn.setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        riceVarietyColumn.setCellValueFactory(new PropertyValueFactory<>("riceVariety"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPerKg"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        billTable.setItems(billItems);
    }
    @FXML
    private void printInvoice() {
        System.out.println("Printing invoice...");
        // Add actual printing logic
    }

}
