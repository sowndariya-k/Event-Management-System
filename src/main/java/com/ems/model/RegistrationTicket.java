/*
 * Author : Jagadeep
 * RegistrationTicket is a model class that maps the
 * association between a registration and its selected
 * ticket type, used to retrieve ticket details linked
 * to a specific registration.
 */
package com.ems.model;

public class RegistrationTicket {

	private int id;
	private int registrationId;
	private int ticketId;
	private int quantity;

	public RegistrationTicket(int id, int registrationId, int ticketId, int quantity) {
		this.id = id;
		this.registrationId = registrationId;
		this.ticketId = ticketId;
		this.quantity = quantity;
	}

	public RegistrationTicket() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
