package com.ems.dao;

<<<<<<< HEAD
public class TicketDao {

	
=======
import java.util.List;

import com.ems.model.Ticket;

public interface TicketDao {
	int getAvailableTickets(int eventId);

	List<Ticket> getTicketTypes(int eventId);

>>>>>>> 3e4d4506029d2d968e9fce24b411d5ec29425433
}
