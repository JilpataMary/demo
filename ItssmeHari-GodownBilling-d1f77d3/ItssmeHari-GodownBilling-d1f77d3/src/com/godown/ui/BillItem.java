package com.godown.ui;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"billNumber", "date", "customerName", "riceVariety", "weight", "costPerKg", "totalPrice"})  // Specify the order here
public class BillItem {
    private int billNumber;
    private String customerName;
    private String riceVariety;
    private double weight;
    private double costPerKg;
    private double totalPrice;
    private LocalDate date;

    // Default constructor for Jackson
    public BillItem() {
    }

    @JsonCreator
    public BillItem(@JsonProperty("billNumber") int billNumber,
                    @JsonProperty("date") LocalDate date,
                    @JsonProperty("customerName") String customerName,
                    @JsonProperty("riceVariety") String riceVariety,
                    @JsonProperty("weight") double weight,
                    @JsonProperty("costPerKg") double costPerKg,
                    @JsonProperty("totalPrice") double totalPrice)
    {
        this.billNumber = billNumber;
        this.date = date;
        this.customerName = customerName;
        this.riceVariety = riceVariety;
        this.weight = weight;
        this.costPerKg = costPerKg;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getBillNumber() { return billNumber; }
    public LocalDate getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getRiceVariety() { return riceVariety; }
    public double getWeight() { return weight; }
    public double getCostPerKg() { return costPerKg; }
    public double getTotalPrice() { return totalPrice; }

    // Override toString method to display relevant information
    @Override
    public String toString() {
        return "BillItem{" +
                "billNumber=" + billNumber +
                ", date=" + date +  // Add date to the toString method to ensure it's printed
                ", customerName='" + customerName + '\'' +
                ", riceVariety='" + riceVariety + '\'' +
                ", weight=" + weight +
                ", costPerKg=" + costPerKg +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
