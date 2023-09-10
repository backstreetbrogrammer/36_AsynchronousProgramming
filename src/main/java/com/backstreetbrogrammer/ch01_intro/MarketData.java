package com.backstreetbrogrammer.ch01_intro;

public class MarketData {

    private final String server;
    private final String symbol;
    private final double price;

    public MarketData(final String server, final String symbol, final double price) {
        this.server = server;
        this.symbol = symbol;
        this.price = price;
    }

    public String getServer() {
        return server;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "server='" + server + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }
}
