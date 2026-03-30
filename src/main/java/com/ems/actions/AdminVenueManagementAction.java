package com.ems.actions;

import java.util.List;
import java.util.Scanner;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Venue;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;

public class AdminVenueManagementAction {
	private final AdminService adminService;
	private final EventService eventService;
	private final Scanner scanner;

	public AdminVenueManagementAction(Scanner scanner) {
		this.scanner=scanner;
		this.adminService = ApplicationUtil.adminService();
		this.eventService = ApplicationUtil.eventService();
	}

	public List<Venue> getAllVenues() throws DataAccessException {
		return eventService.getAllVenues();
	}

	/*
	 * List all the venue 
	 */
	public void listAllVenues() {
		try {
			List<Venue> venues = getAllVenues();

			if (venues.isEmpty()) {
				System.out.println("No venues found.");
			} else {
				printVenues(venues);
			}
		} catch (DataAccessException e) {
			System.out.println("Error listing venues: " + e.getMessage());
		}
	}
	/*
	 * Add Event venue
	 */

	public void addVenue() {
		Venue venue = new Venue();
		String venueName;
		while (true) {
			venueName = InputValidationUtil.readNonEmptyString(scanner, "Enter the venue name (3 - 100 characters): ");
			if (venueName.length() >= 3 && venueName.length() <= 100) {
				venue.setName(venueName);
				break;
			}
			System.out.println("Venue name must be between 3 and 100 characters.");
		}

		String street;
		while (true) {
			street = InputValidationUtil.readNonEmptyString(scanner, "Enter the street name (3 - 100 characters): ");
			if (street.length() >= 3 && street.length() <= 100) {
				venue.setStreet(street);
				break;
			}
			System.out.println("Street name must be between 3 and 100 characters.");
		}

		String city;
		while (true) {
			city = InputValidationUtil.readNonEmptyString(scanner, "Enter the city name (3 - 30 characters): ");
			if (city.length() >= 3 && city.length() <= 30) {
				venue.setCity(city);
				break;
			}
			System.out.println("City name must be between 3 and 30 characters.");
		}

		String state;
		while (true) {
			state = InputValidationUtil.readNonEmptyString(scanner, "Enter the state name (2 - 30 characters): ");
			if (state.length() >= 2 && state.length() <= 30) {
				venue.setState(state);
				break;
			}
			System.out.println("State name must be between 2 and 30 characters.");
		}

		String pincode;
		while (true) {
			pincode = InputValidationUtil.readNonEmptyString(scanner, "Enter the pincode (5 - 10 characters): ");
			if (pincode.length() >= 5 && pincode.length() <= 10) {
				venue.setPincode(pincode);
				break;
			}
			System.out.println("Pincode must be between 5 and 10 characters.");
		}

		int maxCapacity;
		while (true) {
			maxCapacity = InputValidationUtil.readInt(scanner, "Enter the maximum capacity: ");
			if (maxCapacity > 0) {
				venue.setMaxCapacity(maxCapacity);
				break;
			}
			System.out.println("Venue capacity must be greater than 0");
		}
		try {
			adminService.addVenue(venue);
			System.out.println("Venue added successfully.");
		} catch (DataAccessException e) {
			System.out.println("Error adding venue: " + e.getMessage());
		}
	}
	
	/*
	 * Update venue
	 */
	public void updateVenue() {
		try {
			Venue selectedVenue = selectVenue();
			if (selectedVenue == null) {
				return;
			}

			if (selectedVenue.isStatus() == false) {
				char choice = InputValidationUtil.readChar(scanner,
						"The selected venue is inactive\nDo you need activate the venue (Y/N): ");
				if (choice == 'Y' || choice == 'y') {
					adminService.activateVenue(selectedVenue.getVenueId());
					System.out.println("Venue activated successfully.");

					return;
				}
				System.out.println("Action cancelled by user!");
				return;
			}
			System.out.println("Press Enter to keep the current value");

			String name;
			while (true) {
				name = InputValidationUtil.readString(scanner,
						"Venue name (" + selectedVenue.getName() + ") [3 - 100]: ");

				if (name.isBlank()) {
					break;
				}

				if (name.length() >= 3 && name.length() <= 100) {
					selectedVenue.setName(name);
					break;
				}

				System.out.println("Venue name must be between 3 and 100 characters.");
			}

			String street;
			while (true) {
				street = InputValidationUtil.readString(scanner,
						"Street (" + selectedVenue.getStreet() + ") [3 - 100]: ");

				if (street.isBlank()) {
					break;
				}

				if (street.length() >= 3 && street.length() <= 100) {
					selectedVenue.setStreet(street);
					break;
				}

				System.out.println("Street name must be between 3 and 100 characters.");
			}

			String city;
			while (true) {
				city = InputValidationUtil.readString(scanner,
						"City (" + selectedVenue.getCity() + ") [3 - 30]: ");

				if (city.isBlank()) {
					break;
				}

				if (city.length() >= 3 && city.length() <= 30) {
					selectedVenue.setCity(city);
					break;
				}

				System.out.println("City name must be between 3 and 30 characters.");
			}

			String state;
			while (true) {
				state = InputValidationUtil.readString(scanner,
						"State (" + selectedVenue.getState() + ") [2 - 30]: ");

				if (state.isBlank()) {
					break;
				}

				if (state.length() >= 2 && state.length() <= 30) {
					selectedVenue.setState(state);
					break;
				}

				System.out.println("State name must be between 2 and 30 characters.");
			}

			String pincode;
			while (true) {
				pincode = InputValidationUtil.readString(scanner,
						"Pincode (" + selectedVenue.getPincode() + ") [5 - 10]: ");

				if (pincode.isBlank()) {
					break;
				}

				if (pincode.length() >= 5 && pincode.length() <= 10) {
					selectedVenue.setPincode(pincode);
					break;
				}

				System.out.println("Pincode must be between 5 and 10 characters.");
			}

			int capacity = InputValidationUtil.readInt(scanner,
					"Maximum capacity (" + selectedVenue.getMaxCapacity() + ") enter 0 to skip: ");

			if (capacity > 0) {
				selectedVenue.setMaxCapacity(capacity);
			}

			adminService.updateVenue(selectedVenue);
			System.out.println("Venue updated successfully.");
		} catch (DataAccessException e) {
			System.out.println("Error updating venue: " + e.getMessage());
		}
	}
	
	/*
	 * Remove venue
	 */

	public void removeVenue() {
		try {
			Venue selectedVenue = selectVenue();
			if (selectedVenue == null)
				return;

			char confirm = InputValidationUtil.readChar(scanner,
					"Are you sure you want to remove this venue (Y/N): ");

			if (Character.toUpperCase(confirm) != 'Y') {
				System.out.println("Venue removal cancelled.");
				return;
			}

			adminService.removeVenue(selectedVenue.getVenueId());

			System.out.println("Venue removed successfully.");
		} catch (DataAccessException e) {
			System.out.println("Error removing venue: " + e.getMessage());
		}
	}

	/*
	 * List event venue details
	 */
	public void listEventsByCity() {
		try {
			Venue selectedVenue = selectVenue();
			if (selectedVenue == null)
				return;

			List<Event> events = eventService.searchByCity(selectedVenue.getVenueId());

			if (events.isEmpty()) {
				System.out.println("No events for this venue");
			} else {
				printEventSummaries(events);
			}

		} catch (DataAccessException e) {
			System.out.println("Error searching events by city: " + e.getMessage());
		}
	}
	
	
	
	private Venue selectVenue() throws DataAccessException {

		List<Venue> venues = getAllVenues();

		if (venues.isEmpty()) {
			System.out.println("No venues found.");
			return null;
		}

		printVenues(venues);

		int choice = InputValidationUtil.readInt(scanner,
				"Select a venue (1-" + venues.size() + "): ");

		while (choice < 1 || choice > venues.size()) {
			choice = InputValidationUtil.readInt(scanner, "Enter a valid choice: ");
		}

		return venues.get(choice - 1);
	}

	// ===== required display methods =====

	private static final int TABLE_WIDTH = 110;
	private static final String SEPARATOR = "=".repeat(TABLE_WIDTH);
	private static final String SUB_SEPARATOR = "-".repeat(TABLE_WIDTH);

	private void printVenues(List<Venue> venues) {
		System.out.println("\nAVAILABLE VENUES");
		System.out.println(SEPARATOR);

		System.out.printf("%-5s %-25s %-20s %-15s %-12s %-10s %-10s%n",
				"NO", "VENUE NAME", "STREET", "CITY", "STATE", "CAPACITY", "STATUS");

		System.out.println(SUB_SEPARATOR);

		int i = 1;
		for (Venue v : venues) {
			System.out.printf("%-5d %-25s %-20s %-15s %-12s %-10d %-10s%n",
					i++,
					truncate(v.getName(), 24),
					truncate(v.getStreet(), 19),
					v.getCity(),
					v.getState(),
					v.getMaxCapacity(),
					v.isStatus() ? "ACTIVE" : "INACTIVE");
		}

		System.out.println(SEPARATOR);
	}

	private void printEventSummaries(List<Event> events) {
		System.out.println("\nAVAILABLE EVENTS");
		System.out.println(SEPARATOR);

		System.out.printf("%-5s %-30s %-20s %-20s %-10s%n",
				"NO", "TITLE", "CATEGORY", "START DATE", "TICKETS");

		System.out.println(SUB_SEPARATOR);

		int i = 1;
		for (Event e : events) {
			try {
				String categoryName = eventService
						.getCategory(e.getCategoryId())
						.getName();

				int tickets = eventService
						.getAvailableTickets(e.getEventId());
				
				String formattedDate = DateTimeUtil
						.formatForDisplay(e.getStartDateTime());

				System.out.printf("%-5d %-30s %-20s %-20s %-10d%n",
						i++,
						truncate(e.getTitle(), 29),
						categoryName,
						formattedDate,
						tickets);

			} catch (DataAccessException ex) {
				System.out.printf("%-5d %-30s %-50s%n",
						i++,
						truncate(e.getTitle(), 29),
						"[Error fetching details]");
			}
		}

		System.out.println(SEPARATOR);
	}
	
	private String truncate(String value, int max) {
		if (value == null) return "";
		if (value.length() <= max) return value;
		return value.substring(0, max - 3) + "...";
	}
}
