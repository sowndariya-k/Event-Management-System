/*
 * Author : Sowndariya, Jagadeep
 * EventRevenueReport is a model class that encapsulates
 * revenue data for an event, including event ID, title,
 * total ticket sales, and total revenue generated, used
 * for organizer and admin reports.
 */
package com.ems.model;

public class EventRevenueReport {

    private int eventId;
    private String eventTitle;
    private int totalRegistrations;
    private int ticketsSold;
    private double totalRevenue;
    private double avgTicketPrice;
    private double totalDiscountGiven;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(int totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getAvgTicketPrice() {
        return avgTicketPrice;
    }

    public void setAvgTicketPrice(double avgTicketPrice) {
        this.avgTicketPrice = avgTicketPrice;
    }

    public double getTotalDiscountGiven() {
        return totalDiscountGiven;
    }

    public void setTotalDiscountGiven(double totalDiscountGiven) {
        this.totalDiscountGiven = totalDiscountGiven;
    }
}