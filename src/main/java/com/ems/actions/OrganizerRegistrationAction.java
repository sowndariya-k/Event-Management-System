package com.ems.actions;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.AdminMenuHelper;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;

public class OrganizerRegistrationAction {
	
	private final Scanner scanner;
	private OrganizerService organizerService;
	private final EventService eventService;
	
	public OrganizerRegistrationAction(Scanner scanner) {
        this.organizerService = ApplicationUtil.organizerService();
        this.eventService = ApplicationUtil.eventService();
        this.scanner = scanner;
    }

	
	/**
	 * Views the total number of registrations for a specific event.
	 * @param organizerId the ID of the organizer
	 * @return the count of registrations
	 * @throws DataAccessException 
	 */
	public void viewEventRegistrations(int organizerId) throws DataAccessException {
		try {
			List<Event> events = organizerService.getOrganizerEvents(organizerId);

			if (events.isEmpty()) {
				System.out.println("No events available at the moment.");
				return;
			}

			MenuHelper.printEventSummaries(events);

			int eChoice = InputValidationUtil.readInt(scanner,
					"Select an event (1-" + events.size() + "): ");

			while (eChoice < 1 || eChoice > events.size()) {
				eChoice = InputValidationUtil.readInt(scanner,
						"Enter a valid choice: ");
			}

			Event selectedEvent = events.get(eChoice - 1);

			List<EventRegistrationReport> reports =
					organizerService.getEventWiseRegistrations(selectedEvent.getEventId());

			if (reports.isEmpty()) {
				System.out.println("No registrations found for this event");
				return;
			}

			reports.sort(Comparator
					.comparing(EventRegistrationReport::getRegistrationDate)
					.reversed());

			AdminMenuHelper.printEventRegistrationReport(reports);

		} catch (DataAccessException e) {
			System.out.println("Error viewing registrations: " + e.getMessage());
		}
	}	

}