package com.ems.actions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.service.EventService;
import com.ems.service.OfferService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

public class OrganizerOfferManagementAction {
	private final OfferService offerService;
    private final EventService eventService;
    private final OrganizerService organizerService;

    public OrganizerOfferManagementAction() {
        this.offerService = ApplicationUtil.offerService();
        this.eventService = ApplicationUtil.eventService();
        this.organizerService = ApplicationUtil.organizerService();
    }
    
    public List<Offer> getAllOffers(int userId) throws DataAccessException {

        List<Event> myEvents = organizerService.getOrganizerEvents(userId);

        Set<Integer> eventIds = myEvents.stream()
                .map(Event::getEventId)
                .collect(Collectors.toSet());

        return offerService.getAllOffers()
                .stream()
                .filter(o -> eventIds.contains(o.getEventId()))
                .collect(Collectors.toList());
    }
    
    
	public void createOffer(int userId) {
		// TODO Auto-generated method stub
		
	}

	public void viewAllOffers(int userId) {
		//use getoffers
		
	}

	public void activateOffer(int userId) {
		// use getoffers
		
	}

	public void deactivateOffer(int userId) {
		//use getoffer
		
	}
	
	public void viewOfferUsageReport(int userId) {
		
		//use getOfferUsageReport
	  }

//need to create like getoffers
//public boolean createOffer(int eventId, String code, int discount,LocalDateTime from, LocalDateTime to) throws DataAccessException
// public void toggleOfferStatus
//public Map<String, Integer> getOfferUsageReport(int userId) throws DataAccessException 

}
