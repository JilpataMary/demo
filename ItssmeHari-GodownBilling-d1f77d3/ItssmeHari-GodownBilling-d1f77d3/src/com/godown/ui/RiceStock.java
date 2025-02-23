package com.godown.ui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
//import javafx.beans.property.*;

import java.util.Objects;

public class RiceStock extends InventoryItem {

    @JsonCreator
    public RiceStock(@JsonProperty("riceVariety") String riceVariety,
                     @JsonProperty("cost") double cost,
                     @JsonProperty("quantity") int quantity) {
        // Call InventoryItem constructor
        super(riceVariety, cost, quantity, false);
    }

    // No longer needed: qualityGrade is removed.

    // Override equals to compare rice variety & cost
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RiceStock that = (RiceStock) obj;
        return Double.compare(that.getCost(), getCost()) == 0 &&
               getRiceVariety().equalsIgnoreCase(that.getRiceVariety());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRiceVariety().toLowerCase(), getCost());
    }

    @Override
    public String toString() {
        return "RiceStock{" +
                "Variety='" + getRiceVariety() + '\'' +
                ", Cost=" + getCost() +
                ", Quantity=" + getQuantity() +
                '}';
    }
}
