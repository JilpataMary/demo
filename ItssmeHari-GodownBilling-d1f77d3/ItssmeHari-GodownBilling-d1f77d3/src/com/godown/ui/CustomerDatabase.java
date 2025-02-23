package com.godown.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDatabase {
    private static final String FILE_PATH = "customers.json"; 
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static List<Customer> customers = new ArrayList<>();

    // Load customers when the class is initialized
    static {
        loadCustomers();
    }

    // Load customer details from JSON file
    public static void loadCustomers() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                customers = objectMapper.readValue(file, new TypeReference<List<Customer>>() {});
//                System.out.println("Customer data loaded successfully!");
            } catch (IOException e) {
                System.err.println("Error loading customer data: " + e.getMessage());
            }
        } else {
            System.out.println("No existing customer file found, starting fresh.");
        }
    }

    // Save customer details to JSON file
    public static void saveCustomers() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), customers);
            System.out.println("Customer data saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving customer data: " + e.getMessage());
        }
    }

    // Get all customers
    public static List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    // Add a new customer or update existing one
    public static void addCustomer(Customer customer) {
        for (Customer existing : customers) {
            if (existing.getPhoneNumber().equals(customer.getPhoneNumber())) {
                existing.setName(customer.getName()); // Update name if it changed
                saveCustomers();
                System.out.println("Customer updated: " + customer.getName());
                return;
            }
        }
        customers.add(customer);
        saveCustomers();
        System.out.println("Customer added: " + customer.getName());
    }
    
 // Modify deleteCustomer to accept CustomerController instance
    public static void deleteCustomer(Customer customer, CustomerController customerController) {
        customers.remove(customer);
        saveCustomers();
        
        // After deletion, refresh the customer list in the controller
        customerController.refreshCustomerList();  // Now you can call refreshCustomerList() on the passed instance
        System.out.println("Customer deleted: " + customer.getPhoneNumber());
    }

    
    public static Customer getCustomerByPhone(String phoneNumber) {
        return customers.stream()
            .filter(c -> c.getPhoneNumber().equals(phoneNumber))
            .findFirst()
            .orElse(null);
    }

}
