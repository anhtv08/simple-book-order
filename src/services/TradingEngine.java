package services;

import model.Order;
import model.Trade;

import java.util.List;

public interface TradingEngine {
   List<Trade> addOrder(Order order);
    boolean cancelOrder(Order order);
    List<Trade>  amendOrder( Order order);
    void addOrdertoBook(Order order);
}
