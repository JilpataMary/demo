package com.godown.ui;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

public class InventoryItem {

    private final StringProperty riceVariety;
    private final DoubleProperty cost;
    private final IntegerProperty quantity;
    private final BooleanProperty selected;

    // ✅ JSON Constructor for Deserialization
    @JsonCreator
    public InventoryItem(
        @JsonProperty("riceVariety") String riceVariety,
        @JsonProperty("cost") double cost,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("selected") boolean selected) {

        this.riceVariety = new SimpleStringProperty(riceVariety);
        this.cost = new SimpleDoubleProperty(cost);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.selected = new SimpleBooleanProperty(false);
    }

    // ✅ Override equals() and hashCode() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Double.compare(that.cost.get(), cost.get()) == 0 &&
               quantity.get() == that.quantity.get() &&
               riceVariety.get().equals(that.riceVariety.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(riceVariety.get(), cost.get(), quantity.get());
    }

    // ✅ JavaFX Property Methods
    public StringProperty riceVarietyProperty() { return riceVariety; }
    public DoubleProperty costProperty() { return cost; }
    public IntegerProperty quantityProperty() { return quantity; }
    public BooleanProperty selectedProperty() { return selected; }

    // ✅ Getters
    public String getRiceVariety() { return riceVariety.get(); }
    public double getCost() { return cost.get(); }
    public int getQuantity() { return quantity.get(); }
    public boolean isSelected() { return selected.get(); }

    // ✅ Setters
    public void setRiceVariety(String variety) { this.riceVariety.set(variety); }
    public void setCost(double cost) { this.cost.set(cost); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setSelected(boolean selected) { this.selected.set(selected); }

    // ✅ toString() for debugging or logging
    @Override
    public String toString() {
        return "InventoryItem{" +
                "riceVariety='" + riceVariety.get() + '\'' +
                ", cost=" + cost.get() +
                ", quantity=" + quantity.get() +
                ", selected=" + selected.get() +
                '}';
    }
}
