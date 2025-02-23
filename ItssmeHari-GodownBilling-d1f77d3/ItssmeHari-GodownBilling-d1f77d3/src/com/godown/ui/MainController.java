package com.godown.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainController {

    @FXML private Button newBillButton;
    @FXML private Button viewBillsButton;
    @FXML private Button customerButton;
    @FXML private Button inventoryButton;

    @FXML private Label todaySalesLabel;
    @FXML private Label monthSalesLabel;
    @FXML private Hyperlink emailLink;
    @FXML private Hyperlink instagramLink;
    @FXML private Hyperlink linkedinLink;

    @FXML
    private void initialize() {
        // Initialize buttons
        newBillButton.setOnAction(e -> {
            openNewBillWindow();
        });
        viewBillsButton.setOnAction(e -> openWindow("ViewBills.fxml", "View Bills"));
        customerButton.setOnAction(e -> openWindow("Customers.fxml", "Customers"));
        inventoryButton.setOnAction(event -> openWindow("Inventory.fxml", "Inventory"));
        
        // Update total sales labels initially
        updateTotalSales();
        
        emailLink.setOnAction(event -> openLink("mailto:hariprasathsmb@example.com"));
        instagramLink.setOnAction(event -> openLink("https://www.instagram.com/i.am.hp_/"));
        linkedinLink.setOnAction(event -> openLink("www.linkedin.com/in/sm-hariprasath"));
    
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewBillWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewBill.fxml"));
            Parent root = loader.load();
            BillController controller = loader.getController();

            // Pass the current MainController instance to BillController
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("New Bill");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the sales values for today and this month
    public void updateTotalSales() {
        double totalSalesToday = BillDatabase.calculateTotalSalesForToday();
        double totalSalesThisMonth = BillDatabase.calculateTotalSalesForMonth();
        
        // Set the labels with the updated total sales
        todaySalesLabel.setText("Total Sales Today: ₹" + totalSalesToday);
        monthSalesLabel.setText("Total Sales This Month: ₹" + totalSalesThisMonth);
        
        // Debug output
//        System.out.println("Updated Today Sales: ₹" + totalSalesToday);
//        System.out.println("Updated This Month Sales: ₹" + totalSalesThisMonth);
    }
    private void openLink(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
}
}
