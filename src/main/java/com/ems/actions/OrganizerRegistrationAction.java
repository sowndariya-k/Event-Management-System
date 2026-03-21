package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

public class OrganizerRegistrationAction {
	private final Scanner scanner;
	private OrganizerService organizerService;
	
	public OrganizerRegistrationAction(Scanner scanner) {
        this.organizerService = ApplicationUtil.organizerService();
        this.scanner = scanner;
    }

	
	/**
	 * Views the total number of registrations for a specific event.
	 * @param organizerId the ID of the organizer
	 * @return the count of registrations
	 * @throws DataAccessException 
	 */
	public void viewEventRegistrations(int organizerId) throws DataAccessException {
		List<Event> events = organizerService.getOrganizerEvents(organizerId);
		
	}


	

}
