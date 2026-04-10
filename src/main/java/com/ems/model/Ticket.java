/*
 * Author : Jagadeep
 * Ticket is a model class that represents a ticket type
 * for an event, containing ticket ID, event ID, ticket
 * name, price, total quantity, and available quantity,
 * mapped to the tickets table.
 */
package com.ems.model;

public class Ticket {

	private int ticketId;
	private int eventId;
	private String ticketType;
	private double price;
	private int totalQuantity;
	private int availableQuantity;

	public Ticket(int ticketId, int eventId, String ticketType, double price, int totalQuantity,
			int availableQuantity) {
		this.ticketId = ticketId;
		this.eventId = eventId;
		this.ticketType = ticketType;
		this.price = price;
		this.totalQuantity = totalQuantity;
		this.availableQuantity = availableQuantity;
	}

	public Ticket() {
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	@Override
	public String toString() {
		return String.format("Ticket [%d] %s | Price: %.2f | Available: %d/%d", ticketId, ticketType, price,
				availableQuantity, totalQuantity);
	}

}
