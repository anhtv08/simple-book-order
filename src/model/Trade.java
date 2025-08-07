package model;

public class Trade {
    String tradeId;
    String sellOderId;
    String buyOrderId;
    long tradeQuality;
    long timestamp;
    double price;


    public Trade(String tradeId, String sellOderId, String buyOrderId, long tradeQuality, double price, long timestamp) {
        this.tradeId = tradeId;
        this.sellOderId = sellOderId;
        this.buyOrderId = buyOrderId;
        this.tradeQuality = tradeQuality;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Trade() {
    }

    public Trade(String sellOderId, String buyOrderId, long tradeQuality, long timestamp, double price) {
        this.sellOderId = sellOderId;
        this.buyOrderId = buyOrderId;
        this.tradeQuality = tradeQuality;
        this.timestamp = timestamp;
        this.price = price;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getSellOderId() {
        return sellOderId;
    }

    public void setSellOderId(String sellOderId) {
        this.sellOderId = sellOderId;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public long getTradeQuality() {
        return tradeQuality;
    }

    public void setTradeQuality(int tradeQuality) {
        this.tradeQuality = tradeQuality;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
