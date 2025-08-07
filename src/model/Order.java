package model;

import java.util.concurrent.atomic.AtomicLong;

public class Order {

    String orderId;
    String ticker;
    OrderStatus orderStatus;
    OrderStrategy orderStrategy;
    long orginalQuality;
    long remaningQuality;
    double price;
    OrderType orderType;
    long timestamp;

    public synchronized  boolean reduceQuantity(long executedQuanitty){
         if(remaningQuality >= executedQuanitty){
             this.remaningQuality -= executedQuanitty;
             return  true;

         }
         return false;
    }
    public boolean isOrderFilled (){
        return remaningQuality <=0;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStrategy getOrderStrategy() {
        return orderStrategy;
    }

    public void setOrderStrategy(OrderStrategy orderStrategy) {
        this.orderStrategy = orderStrategy;
    }

    public long getOrginalQuality() {
        return orginalQuality;
    }

    public void setOrginalQuality(int orginalQuality) {
        this.orginalQuality = orginalQuality;
    }

    public long getRemaningQuality() {
        return remaningQuality;
    }

    public void setRemaningQuality(int remaningQuality) {
        this.remaningQuality = remaningQuality;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Order(String orderId, String ticker, OrderStatus orderStatus, OrderStrategy orderStrategy, int orginalQuality, int remaningQuality, double price, OrderType orderType) {
        this.orderId = orderId;
        this.ticker = ticker;
        this.orderStatus = orderStatus;
        this.orderStrategy = orderStrategy;
        this.orginalQuality = orginalQuality;
        this.remaningQuality = remaningQuality;
        this.price = price;
        this.orderType = orderType;
    }

    public enum OrderType {BUY, SELL}

    public enum OrderStatus {
        NEW,
        CANCELLED,
        FILLED,
        PARTIALLY_FILLED

    }

    public enum OrderStrategy {
        MARKET,
        LIMIT_ORDER
    }
}
