package model;

import java.util.List;

public class PriceLevel {

    String ticker;
    double priceLevel;
    int quantity;
    List<Order> orders;


    public PriceLevel(String ticker, double priceLevel, int quantity, List<Order> orders) {
        this.ticker = ticker;
        this.priceLevel = priceLevel;
        this.quantity = quantity;
        this.orders = orders;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(double priceLevel) {
        this.priceLevel = priceLevel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
