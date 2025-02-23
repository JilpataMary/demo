package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class BillDatabase {

    private static final String BILL_FILE = "bills.json";
    private static List<BillItem> billList = new ArrayList<>();
    
    // Create and configure ObjectMapper globally
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle LocalDate and LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Optional: Handle unknown properties gracefully
        loadBills();  // Load bills when the class is initialized
    }

    public static void saveBill(BillItem bill) {
        billList.add(bill);
//        System.out.println("Saving bill: " + bill);  // Debugging line to check what is being saved
        saveBillsToFile();
    }

    public static List<BillItem> getAllBills() {
        return billList;
    }

    public static int generateNewBillNumber() {
        return billList.size() + 1;
    }

    private static void loadBills() {
        try {
            File file = new File(BILL_FILE);
            if (file.exists()) {
                billList = readBillsFromFile(file);
            } else {
                System.out.println("No existing bill file found. Starting with an empty list.");
            }
        } catch (IOException e) {
            System.err.println("Error loading bills from file: " + BILL_FILE);
            e.printStackTrace();
        }
    }

    private static List<BillItem> readBillsFromFile(File file) throws IOException {
        // Use the pre-configured ObjectMapper here to read the file
        return objectMapper.readValue(file, new TypeReference<List<BillItem>>() {});
    }

    private static void saveBillsToFile() {
        try {
            // Use the pre-configured ObjectMapper to save the bills
            objectMapper.writeValue(new File(BILL_FILE), billList);
        } catch (IOException e) {
            System.err.println("Error saving bills to file: " + BILL_FILE);
            e.printStackTrace();
        }
        
    }
    
    // Method to calculate total sales for today
    public static double calculateTotalSalesForToday() {
        double totalSales = 0.0;
        LocalDate today = LocalDate.now();
        
        // Loop through all bills and calculate sales for today
        for (BillItem bill : billList) {
            if (bill.getDate().equals(today)) {
                totalSales += bill.getTotalPrice();
            }
        }
        return totalSales;
    }

    // Method to calculate total sales for this month
    public static double calculateTotalSalesForMonth() {
        double totalSales = 0.0;
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        Month currentMonth = today.getMonth();

        // Loop through all bills and calculate sales for the current month
        for (BillItem bill : billList) {
            if (bill.getDate().getYear() == currentYear && bill.getDate().getMonth() == currentMonth) {
                totalSales += bill.getTotalPrice();
            }
        }
        return totalSales;
    }
    
    
}
