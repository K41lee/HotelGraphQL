package Impl;

import java.time.LocalDate;

public class Offer {
    private final String id;
    private final int beds;
    private final LocalDate from;
    private final LocalDate to;
    private final double price;

    public Offer(String id, int beds, LocalDate from, LocalDate to, double price) {
        this.id = id;
        this.beds = beds;
        this.from = from;
        this.to = to;
        this.price = price;
    }

    public String getId() { return id; }
    public int getBeds() { return beds; }
    public LocalDate getFrom() { return from; }
    public LocalDate getTo() { return to; }
    public double getPrice() { return price; }
}
