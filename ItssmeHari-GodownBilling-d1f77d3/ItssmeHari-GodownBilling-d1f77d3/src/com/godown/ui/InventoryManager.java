package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class InventoryManager {

    public static final String STOCK_FILE_PATH = "stock.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<InventoryItem> inventorySet = new HashSet<>();

    // Load stock from JSON while preventing duplicates
    public static List<InventoryItem> loadStockFromJson() {
        File file = new File(STOCK_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
        	List<InventoryItem> stockList = objectMapper.readValue(file,
        		    objectMapper.getTypeFactory().constructCollectionType(List.class, InventoryItem.class));
        		System.out.println("Loaded Inventory: " + stockList);  // Debug print

            // Update inventorySet to avoid duplicate conversions
            inventorySet.clear();
            inventorySet.addAll(stockList);

            return new ArrayList<>(inventorySet);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    // Load only RiceStock items
    public static List<RiceStock> loadRiceStockFromJson() {
        List<InventoryItem> inventory = loadStockFromJson();

        // Add null or empty check here
        if (inventory == null || inventory.isEmpty()) {
            System.out.println("Inventory is empty or failed to load.");
            return new ArrayList<>(); // Return an empty list if no inventory is loaded
        }

        System.out.println("Raw Loaded Inventory: " + inventory); // Debug line

        List<RiceStock> riceStocks = inventory.stream()
            .filter(RiceStock.class::isInstance)  // Ensure only RiceStock objects
            .map(RiceStock.class::cast)           // Safe cast
            .collect(Collectors.toList());

        System.out.println("Filtered Rice Stocks: " + riceStocks); // Debug line

        return riceStocks;
    }




    public static void addStock(InventoryItem newItem) {
        // Add new item if not already in the set
        if (!inventorySet.contains(newItem)) {
            inventorySet.add(newItem);
            saveStockToJson();  // Save updated stock list to JSON
        }
    }

    public static void saveStockToJson() {
        try {
            // Convert inventorySet to List and save to JSON
            List<InventoryItem> inventoryList = new ArrayList<>(inventorySet);
            objectMapper.writeValue(new File(STOCK_FILE_PATH), inventoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save a provided list of RiceStock to JSON (used for updating stock after sales)
    public static void saveStockToJson(List<RiceStock> riceStockList) {
        try {
            objectMapper.writeValue(new File(STOCK_FILE_PATH), riceStockList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update a specific RiceStock item
    public void updateStock(RiceStock updatedStock) {
        List<RiceStock> stockList = loadStock();
        for (RiceStock stock : stockList) {
            if (stock.getRiceVariety().equalsIgnoreCase(updatedStock.getRiceVariety())) {
                stock.setQuantity(updatedStock.getQuantity());
                break;
            }
        }
        saveStock(stockList);
    }
    public List<RiceStock> loadStock() {
        List<RiceStock> stockList = JsonUtility.readJson("stock.json", new TypeReference<List<RiceStock>>() {});
        return stockList != null ? stockList : new ArrayList<>();
    }

    public void saveStock(List<RiceStock> stockList) {
        JsonUtility.writeJson("stock.json", stockList);
    }


    // Edit an InventoryItem (RiceStock or other types)
    public static void editStockInInventory(InventoryItem updatedItem) {
        List<InventoryItem> inventory = loadStockFromJson();

        for (InventoryItem item : inventory) {
            if (item.getRiceVariety().equals(updatedItem.getRiceVariety())) {
                item.setCost(updatedItem.getCost());
                item.setQuantity(updatedItem.getQuantity());
                break;
            }
        }

        // Update inventorySet in memory
        inventorySet.clear();
        inventorySet.addAll(inventory);

        saveStockToJson();  // Save updated stock to JSON
    }

    public static void deleteStock(String variety) {
        // Remove item and update JSON
        inventorySet.removeIf(item -> item.getRiceVariety().equals(variety));
        saveStockToJson();
    }

    // Get all inventory items
    public static List<InventoryItem> getAllStock() {
        return new ArrayList<>(inventorySet);
    }
}
