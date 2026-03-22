package com.ems.actions;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.EventRegistrationReport;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

public class AdminReportAction {
	private final AdminService adminService;
    private final EventService eventService;
    private final OrganizerService organizerService;

    public AdminReportAction() {
        this.adminService = ApplicationUtil.adminService();
        this.eventService = ApplicationUtil.eventService();
        this.organizerService = ApplicationUtil.organizerService();
    }

	public void viewEventWiseRegistrations() {
		
		
		
	}

	public void viewOrganizerReport() {


		
	}

	public void viewRevenueReport() {


		
	}
	
	 // -----------------------------------------------------------------------
    // Data access helpers
    // -----------------------------------------------------------------------

    public List<EventRegistrationReport> getEventWiseRegistrations(int eventId) throws DataAccessException {
        return adminService.getEventWiseRegistrations(eventId);
    }

    public List<OrganizerEventSummary> getOrganizerEventSummary(int organizerId) throws DataAccessException {
        return organizerService.getOrganizerEventSummary(organizerId);
    }

    public List<EventRevenueReport> getRevenueReport() throws DataAccessException {
        return adminService.getRevenueReport();
    }

	

}
