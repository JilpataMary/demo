package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonUtility {

    private static final String FILE_PATH = "C:\\Users\\user\\eclipse-workspace\\riceGodownBilling\\stock.json";  // Path to the JSON file
    private static final String CUSTOMER_FILE_PATH = "C:\\Users\\user\\eclipse-workspace\\riceGodownBilling\\customers.json";  // Path to the JSON file for customers
    private static final ObjectMapper mapper = new ObjectMapper(); // Reusing ObjectMapper
    private static final ObjectMapper objectMapper = new ObjectMapper();
   
    // Method to read the inventory from the JSON file
    public static List<InventoryItem> readInventoryFromJson() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("stock.json")));

            //for debugging if the json file can be read
//            System.out.println("📜 Raw JSON Data: " + json); // Debugging output

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<List<InventoryItem>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // Method to write the inventory to the JSON file
    public static void writeInventoryToJson(List<InventoryItem> inventoryList) {
        try {
            // Convert the list to JSON and write to file
            mapper.writeValue(new File(FILE_PATH), inventoryList);
        } catch (IOException e) {
            System.err.println("Error writing inventory data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to read the customers from the JSON file
    public static List<Customer> readCustomersFromJson() {
        List<Customer> customerList = new ArrayList<>();
        try {
            File file = new File(CUSTOMER_FILE_PATH);
            if (file.exists()) {
                customerList = mapper.readValue(file, new TypeReference<List<Customer>>() {});
            }
        } catch (IOException e) {
            System.err.println("Error reading customer data: " + e.getMessage());
            e.printStackTrace();
        }
        return customerList;
    }

    // Method to write customer data to JSON file
    public static void writeCustomerToJson(List<Customer> customerList) {
        System.out.println("Writing customer data to JSON file...");
        try {
            // Write to file
            mapper.writeValue(new File(CUSTOMER_FILE_PATH), customerList);
            System.out.println("Customer data saved to the file.");
        } catch (IOException e) {
            System.err.println("Error writing customer data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static <T> T readJson(String filePath, TypeReference<T> typeReference) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null; // Return null if file does not exist
            }
            return objectMapper.readValue(file, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public static <T> void writeJson(String filePath, T data) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}   

