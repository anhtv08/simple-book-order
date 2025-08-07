package services;

import model.Order;
import model.Trade;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class HPBookOrder {

    // using tree map with revere oder to maintain the buy order
    // higher price going first

    ConcurrentSkipListMap<Double, Queue<Order>> buyOrders = new ConcurrentSkipListMap<>(Collections.reverseOrder());

    // using tree for selling orders
    // Order with the lowest price with got higher priority
    ConcurrentSkipListMap<Double, Queue<Order>> sellOrders = new ConcurrentSkipListMap<>();

    // all trades

    Map<String, Order> allOrders = new ConcurrentHashMap<>();

    List<Trade> trades = new CopyOnWriteArrayList<>();

    public HPBookOrder() {
    }

    public List<Trade> addOrder(Order order) {
        List<Trade> executedTrade = new ArrayList<>();
        switch (order.getOrderStrategy()){

            case MARKET:
                executedTrade.addAll(
                        executeMarketOrder(order)
                );
                break;

            case LIMIT_ORDER:
                executedTrade.addAll(
                        executeLimitMarketOrder(order)
                );


        }
        trades.addAll(executedTrade);

        return executedTrade;

    }

    public void addOrdertoBook(Order order) {
        ConcurrentSkipListMap <Double, Queue<Order>> books = order.getOrderType() == Order.OrderType.BUY ? buyOrders : sellOrders;

        books.computeIfAbsent( order.getPrice(), k -> new LinkedList<>())
                .offer(order);

        allOrders.put( order.getOrderId(), order);

    }

    private Collection<? extends Trade> executeMarketOrder(Order marketOrder) {

        List<Trade> executions = new ArrayList<>();
        ConcurrentSkipListMap<Double, Queue<Order>> oppositeOrderBook  = marketOrder.getOrderType() == Order.OrderType.BUY? sellOrders: buyOrders;

        long remainingQ = marketOrder.getRemaningQuality();

        while(remainingQ >0 && !oppositeOrderBook.isEmpty()){
            double bestOppPrice = oppositeOrderBook.firstKey();
            Queue<Order> ordersAtBestPrice = oppositeOrderBook.get(bestOppPrice);

            Order restingOrder =  ordersAtBestPrice.peek();
            if(restingOrder ==null){
                oppositeOrderBook.remove(bestOppPrice);
                continue;
            }

            long tradeQ = Math.min(remainingQ, restingOrder.getRemaningQuality());
            Trade trade = createTrade(
                    marketOrder,
                    restingOrder,
                    bestOppPrice,
                    tradeQ
            );

            executions.add(trade);
            // update the quantity:
            marketOrder.reduceQuantity(tradeQ);
            restingOrder.reduceQuantity(tradeQ);
            remainingQ -= tradeQ;

            // Remove fully executed order
            if(restingOrder.isOrderFilled()){
                ordersAtBestPrice.poll();
                allOrders.remove(restingOrder.getOrderId());

                if(ordersAtBestPrice.isEmpty()){
                    oppositeOrderBook.remove(bestOppPrice);
                }
            }


        }

        return executions;
    }

    private Trade createTrade(Order agressiveOrder, Order restingOrder, double bestPrice, long tradeQ) {

        String buyOrderId = agressiveOrder.getOrderType() == Order.OrderType.BUY? agressiveOrder.getOrderId():
                restingOrder.getOrderId();

        String sellOrderId = agressiveOrder.getOrderType() == Order.OrderType.BUY? restingOrder.getOrderId():
                agressiveOrder.getOrderId();
        return new Trade(
                UUID.randomUUID().toString(),
                sellOrderId,
                buyOrderId,
                tradeQ,
                bestPrice,
                System.nanoTime()
        );

    }

    public  Double getBestBid(){
        return buyOrders.isEmpty()?null:  buyOrders.firstKey();
    }

    public  Double getBestAsk(){
        return sellOrders.isEmpty()?null:  sellOrders.firstKey();
    }
    public Double  getSpread(){
        Double bid = getBestBid();
        Double ask = getBestAsk();
        return  (bid !=null && ask !=null) ? ask- bid : null;

    }


    private List<Trade> executeLimitMarketOrder(Order limitOrder) {


        List<Trade> executions = new ArrayList<>();
        ConcurrentSkipListMap<Double, Queue<Order>> oppositeOrderBook  = limitOrder.getOrderType() == Order.OrderType.BUY? sellOrders: buyOrders;

        long remainingQ = limitOrder.getRemaningQuality();
        double limitPrice = limitOrder.getPrice();

        while(remainingQ >0 && !oppositeOrderBook.isEmpty()){
            double bestOppPrice = oppositeOrderBook.firstKey();

            boolean canExecute = limitOrder.getOrderType() == Order.OrderType.BUY ? limitPrice >= bestOppPrice : limitPrice <= bestOppPrice;

            // if buy order, only fill order if aks price less than or equal limited price.
            if(!canExecute){
                break;
            }

            Queue<Order> ordersAtBestPrice = oppositeOrderBook.get(bestOppPrice);

            Order restingOrder =  ordersAtBestPrice.peek();
            if(restingOrder ==null){
                oppositeOrderBook.remove(bestOppPrice);
                continue;
            }

            long tradeQ = Math.min(remainingQ, restingOrder.getRemaningQuality());
            Trade trade = createTrade(
                    limitOrder,
                    restingOrder,
                    bestOppPrice,
                    tradeQ
            );

            executions.add(trade);
            // update the quantity:
            limitOrder.reduceQuantity(tradeQ);
            restingOrder.reduceQuantity(tradeQ);
            remainingQ -= tradeQ;

            // Remove fully executed order
            if(restingOrder.isOrderFilled()){
                ordersAtBestPrice.poll();
                allOrders.remove(restingOrder.getOrderId());

                if(ordersAtBestPrice.isEmpty()){
                    oppositeOrderBook.remove(bestOppPrice);
                }
            }
        }

        return executions;
    }

    public  boolean cancelOrder(Order order) {
        Order ord = allOrders.remove(order.getOrderId());
        return ord != null;

    }

    public  List<Trade>  amendOrder(Order order) {
        return null;
    }


    // Statistics
    public int getTotalOrderCount() {
        return allOrders.size();
    }

    public int getBidLevels() {
        return buyOrders.size();
    }

    public int getAskLevels() {
        return sellOrders.size();
    }


    public static void main(String[] args) {
        TradingEngineImpl tradingEngine = new TradingEngineImpl( "USDSGD");
        Order order_1 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.3,
                Order.OrderType.BUY

        );

        Order order_2 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.31,
                Order.OrderType.BUY

        );

        Order order_3 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.32,
                Order.OrderType.BUY

        );



        tradingEngine.addOrdertoBook(
                order_1
        );
        tradingEngine.addOrdertoBook(
                order_2
        );
        tradingEngine.addOrdertoBook(
                order_3
        );

        // adding selling order:

        Order order_4 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.5,
                Order.OrderType.SELL

        );

        Order order_5 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.3,
                Order.OrderType.SELL

        );

        Order order_6 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.34,
                Order.OrderType.SELL

        );

        tradingEngine.addOrdertoBook(
                order_4
        );
        tradingEngine.addOrdertoBook(
                order_5
        );
        tradingEngine.addOrdertoBook(
                order_6
        );




        Order order_7 = new Order(
                UUID.randomUUID().toString(),
                "USDSGD",
                Order.OrderStatus.NEW,
                Order.OrderStrategy.MARKET,
                10,
                0,
                1.3,
                Order.OrderType.SELL

        );

        tradingEngine.addOrder(order_7);
        double bestBid = tradingEngine.getBestBid();
        System.out.printf("best bid : %.2f%n" ,  bestBid);

        double bestAsk = tradingEngine.getBestBid();
        System.out.printf("best ask : %.2f%n" ,  bestAsk);


        double spread = tradingEngine.getSpread();
        System.out.printf("spread : %.1f%n" ,  spread);

    }
}
