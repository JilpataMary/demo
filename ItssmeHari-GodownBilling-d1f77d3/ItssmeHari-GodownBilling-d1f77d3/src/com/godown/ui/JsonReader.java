package com.godown.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonReader {

	public Set<RiceStock> readRiceStocksFromJson(String filePath) throws IOException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    // Read JSON file and map it to a Set of RiceStock
	    List<RiceStock> stockList = objectMapper.readValue(new File(filePath),
	            objectMapper.getTypeFactory().constructCollectionType(List.class, RiceStock.class));
	    
	    // Convert to Set to remove duplicates
	    return new HashSet<>(stockList);
	}


    public static void main(String[] args) {
        JsonReader jsonReader = new JsonReader();

        try {
            // Path to the JSON file
            Set<RiceStock> riceStocks = jsonReader.readRiceStocksFromJson("stock.json");

            // Print the data
            for (RiceStock stock : riceStocks) {
                System.out.println("Rice Variety: " + stock.getRiceVariety());
                System.out.println("Cost: " + stock.getCost());
                System.out.println("Quantity: " + stock.getQuantity());
                System.out.println("Selected: " + stock.isSelected()); // Print selected status
                System.out.println("-----------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

