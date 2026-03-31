package com.ems.dao.impl;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.dao.EventDao;
import com.ems.dao.CategoryDao;
import com.ems.enums.EventStatus;
import com.ems.exception.DataAccessException;
import com.ems.model.BookingDetail;
import com.ems.model.Event;
import com.ems.model.Category;
import com.ems.model.EventRevenueReport;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.model.UserEventRegistration;
import com.ems.util.DBConnectionUtil;
import com.ems.util.DateTimeUtil;

public class EventDaoImpl implements EventDao {

    Connection con;
 
    public EventDaoImpl()  {
        try {
            con = DBConnectionUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> viewEvents() {

        List<Event> events = new ArrayList<>();

        try {

            String query = "SELECT * FROM events";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Event event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));
                event.setCategoryId(rs.getInt("category_id"));

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    // 2 View Event Details
    @Override
    public Event getEventById(int eventId) {

        Event event = null;

        try {

            String query = "SELECT * FROM events WHERE event_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                event = new Event();

                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setStartDateTime(rs.getTimestamp("start_datetime").toInstant());
                event.setEndDateTime(rs.getTimestamp("end_datetime").toInstant());
                event.setVenueId(rs.getInt("venue_id"));
                event.setOrganizerId(rs.getInt("organizer_id"));
                event.setCategoryId(rs.getInt("category_id"));
                event.setCapacity(rs.getInt("capacity"));
                event.setStatus(EventStatus.valueOf(rs.getString("status")));

                Timestamp approvedAt = rs.getTimestamp("approved_at");
                if (approvedAt != null) {
                    event.setApprovedAt(approvedAt.toInstant());
                }

                event.setApprovedBy(rs.getInt("approved_by"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    event.setCreatedAt(createdAt.toInstant());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    event.setUpdatedAt(updatedAt.toInstant());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return event;
    }

 // 3 View Ticket Options
    @Override
    public List<Ticket> getTicketsByEventId(int eventId) {
        List<Ticket> tickets = new ArrayList<>();

        String sql = "SELECT * FROM tickets WHERE event_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ticket t = new Ticket();
                t.setTicketId(rs.getInt("ticket_id"));
                t.setEventId(rs.getInt("event_id"));
                t.setTicketType(rs.getString("ticket_type"));
                t.setPrice(rs.getDouble("price"));
                t.setTotalQuantity(rs.getInt("total_quantity"));
                t.setAvailableQuantity(rs.getInt("available_quantity"));

                tickets.add(t);   // 🔥 VERY IMPORTANT
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }

    // 4 List Available Events
    @Override
	public List<Event> listAvailableEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "SELECT * FROM events WHERE status = ?";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.PUBLISHED.toString());
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching available events", e);
		}
		return events;
	}

 // Shared mapper to convert ResultSet into Event objects
 	public List<Event> getEventList(ResultSet rs) throws DataAccessException {
 		List<Event> events = new ArrayList<>();
 		try {
 			while (rs.next()) {
 				Event event = new Event();
 				event.setEventId(rs.getInt("event_id"));
 				event.setOrganizerId(rs.getInt("organizer_id"));
 				event.setTitle(rs.getString("title"));
 				String desc = rs.getString("description");
 				if (desc != null && !desc.isEmpty()) {
 					event.setDescription(desc);
 				}
 				event.setCategoryId(rs.getInt("category_id"));
 				event.setVenueId(rs.getInt("venue_id"));
 				event.setStartDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("start_datetime")));
 				event.setEndDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("end_datetime")));

 				Timestamp updatedAt = rs.getTimestamp("updated_at");
 				if (updatedAt != null)
 					event.setUpdatedAt(DateTimeUtil.fromTimestamp(updatedAt));

 				Timestamp approvedAt = rs.getTimestamp("approved_at");
 				if (approvedAt != null)
 					event.setApprovedAt(DateTimeUtil.fromTimestamp(approvedAt));

 				Timestamp createdAt = rs.getTimestamp("created_at");
 				if (createdAt != null)
 					event.setCreatedAt(DateTimeUtil.fromTimestamp(createdAt));

 				event.setCapacity(rs.getInt("capacity"));
 				try {
 					event.setStatus(EventStatus.valueOf(rs.getString("status")));
 				} catch (IllegalArgumentException e) {
 					event.setStatus(EventStatus.DRAFT);
 				}
 				Integer approvedBy = rs.getInt("approved_by");
 				if (approvedBy != 0) {
 					event.setApprovedBy(approvedBy);
 				}

 				events.add(event);
 			}
 			rs.close();
 		} catch (SQLException e) {
 			throw new DataAccessException("Error while fetching event list");
 		}
 		return events;
 	}


 	@Override
 	public List<Event> listAllEvents() throws DataAccessException {
 	    List<Event> events = new ArrayList<>();
 	    String query = "SELECT * FROM events"; // get everything, ignore filters
 	    try (PreparedStatement ps = con.prepareStatement(query);
 	         ResultSet rs = ps.executeQuery()) {
 	        events = getEventList(rs); // reuse your mapper
 	    } catch (SQLException e) {
 	        throw new DataAccessException("Error fetching all events", e);
 	    }
 	    return events;
 	}

	@Override
	public List<Event> listEventsYetToApprove() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "SELECT e.* " +
				"FROM events e " +
				"WHERE e.status = ? " +
				"AND e.approved_at IS NULL " +
				"AND e.start_datetime > UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, EventStatus.DRAFT.name());
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
			return events;
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching yet to approve events");
		}
	}

	@Override
	public boolean approveEvent(int eventId, int userId) throws DataAccessException {
		String sql = "UPDATE events " +
				"SET status = ?, approved_by = ?, updated_at = ?, approved_at = ? " +
				"WHERE event_id = ? " +
				"AND start_datetime > ? " +
				"AND approved_by IS NULL";

		Instant now = DateTimeUtil.nowUtc();

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, EventStatus.APPROVED.name());
			ps.setInt(2, userId);
			ps.setTimestamp(3, Timestamp.from(now));
			ps.setTimestamp(4, Timestamp.from(now));
			ps.setInt(5, eventId);
			ps.setTimestamp(6, Timestamp.from(now));

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new DataAccessException("Failed to approve event with id " + eventId, e);
		}
	}
 
	@Override
	public boolean cancelEvent(int eventId) throws DataAccessException {
	    String sqlRegistrations = "UPDATE registrations SET status = 'CANCELLED' WHERE event_id = ? AND status = 'CONFIRMED'";
	    String sqlEvent = "UPDATE events SET status = 'CANCELLED' WHERE event_id = ?";
	    
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement psReg = con.prepareStatement(sqlRegistrations);
	         PreparedStatement psEvent = con.prepareStatement(sqlEvent)) {

	        con.setAutoCommit(false); 
	        
	        psReg.setInt(1, eventId);
	        psReg.executeUpdate();
	        psEvent.setInt(1, eventId);
	        int rows = psEvent.executeUpdate();

	        con.commit();
	        return rows > 0;

	    } catch (SQLException e) {
	        try { con.rollback(); } catch (SQLException ex) { /* ignore */ }
	        throw new DataAccessException("Error cancelling event", e);
	    }
	}
	@Override
	public int getOrganizerId(int eventId) throws DataAccessException {
		String sql = "select organizer_id from events where event_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, eventId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("organizer_id");
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error fetching the event organiszer");
		}
		throw new DataAccessException("Organizer not found");
	}

	@Override
	public List<Event> listAvailableAndDraftEvents() throws DataAccessException {
		List<Event> events = new ArrayList<>();
		String sql = "SELECT DISTINCT e.* " +
				"FROM events e " +
				"WHERE e.status IN (?, ?) " +
				"AND e.start_datetime > UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, EventStatus.PUBLISHED.name());
			ps.setString(2, EventStatus.DRAFT.name());
			ResultSet rs = ps.executeQuery();
			events = getEventList(rs);
			rs.close();
		} catch (SQLException e) {
			throw new DataAccessException("Error while fetching available and draft events");
		}
		return events;
	}

	// Marks past approved or published events as completed
	@Override
	public void completeEvents() throws DataAccessException {
		String sql = "update events " +
				"set status = ? " +
				"where status = ? " +
				"and end_datetime <= UTC_TIMESTAMP()";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, EventStatus.COMPLETED.name());
			ps.setString(2, EventStatus.PUBLISHED.name());

			ps.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException("Error while updating events", e);
		}
		
	}

	@Override
	public List<UserEventRegistration> getUserRegistrations(int userId)
			throws DataAccessException {

		List<UserEventRegistration> list = new ArrayList<>();

		String sql = "SELECT " +
				"    r.registration_id, " +
				"    r.registration_date, " +
				"    r.status AS registration_status, " +
				"    e.event_id, " +
				"    e.title, " +
				"    e.start_datetime, " +
				"    e.end_datetime, " +
				"    c.name AS category_name, " +
				"    t.ticket_type, " +
				"    " +
				"    COALESCE(SUM(rt.quantity), 0) AS tickets_purchased, " +
				"    " +
				"    COALESCE(SUM( " +
				"        CASE " +
				"            WHEN p.payment_status = 'SUCCESS' THEN p.amount " +
				"            WHEN p.payment_status = 'REFUNDED' THEN -p.amount " +
				"            ELSE 0 " +
				"        END " +
				"    ), 0) AS amount_paid " +
				" " +
				"FROM registrations r " +
				"JOIN events e ON r.event_id = e.event_id " +
				"JOIN categories c ON e.category_id = c.category_id " +
				"JOIN registration_tickets rt ON rt.registration_id = r.registration_id " +
				"JOIN tickets t ON rt.ticket_id = t.ticket_id " +
				"LEFT JOIN payments p ON p.registration_id = r.registration_id " +
				" " +
				"WHERE r.user_id = ? AND e.status <> 'CANCELLED' AND r.status = 'CONFIRMED' " +
				"GROUP BY " +
				"    r.registration_id, " +
				"    r.registration_date, " +
				"    r.status, " +
				"    e.event_id, " +
				"    e.title, " +
				"    e.start_datetime, " +
				"    e.end_datetime, " +
				"    c.name, " +
				"    t.ticket_type";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				UserEventRegistration uer = new UserEventRegistration();

				uer.setRegistrationId(rs.getInt("registration_id"));
				uer.setRegistrationStatus(rs.getString("registration_status"));
				uer.setEventId(rs.getInt("event_id"));
				uer.setTitle(rs.getString("title"));
				uer.setCategory(rs.getString("category_name"));
				uer.setRegistrationDate(DateTimeUtil.fromTimestamp(rs.getTimestamp("registration_date")));
				uer.setStartDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("start_datetime")));
				uer.setEndDateTime(DateTimeUtil.fromTimestamp(rs.getTimestamp("end_datetime")));
				uer.setTicketsPurchased(rs.getInt("tickets_purchased"));
				uer.setAmountPaid(rs.getDouble("amount_paid"));
				uer.setTicketType(rs.getString("ticket_type"));

				list.add(uer);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("Error fetching user registrations");
		}

		return list;
	}

	@Override
	public List<BookingDetail> viewBookingDetails(int userId) throws DataAccessException {
	    List<BookingDetail> bookings = new ArrayList<>();
	    String sql = "SELECT r.registration_id, e.title, e.start_datetime, v.name AS venue_name, v.city, " +
	                 "t.ticket_type, rt.quantity, p.amount AS total_cost " +
	                 "FROM registrations r " +
	                 "JOIN events e ON r.event_id = e.event_id " +
	                 "JOIN venues v ON e.venue_id = v.venue_id " +
	                 "LEFT JOIN registration_tickets rt ON r.registration_id = rt.registration_id " +
	                 "LEFT JOIN tickets t ON rt.ticket_id = t.ticket_id " +
	                 "LEFT JOIN payments p ON r.registration_id = p.registration_id " +
	                 "WHERE r.user_id = ? AND r.status = 'CONFIRMED'";
	    try (Connection con = DBConnectionUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, userId);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            BookingDetail booking = new BookingDetail(
	            	rs.getInt("registration_id"), 	
	                rs.getString("title"),
	                rs.getTimestamp("start_datetime").toInstant(),
	                rs.getString("venue_name"),
	                rs.getString("city"),
	                rs.getString("ticket_type"),
	                rs.getInt("quantity"),
	                rs.getDouble("total_cost")
	            );
	            bookings.add(booking);
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DataAccessException("Error fetching booking details", e);
	    }
	    return bookings;
	}

	@Override
	public Map<String, Integer> getOrganizerWiseEventCount() throws DataAccessException {
		Map<String, Integer> result = new HashMap<>();
		String sql = "select u.full_name, count(e.event_id) as total_events " + "from events e "
				+ "join users u on e.organizer_id = u.user_id " + "group by u.user_id";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				result.put(rs.getString("full_name"), rs.getInt("total_events"));
			}
		} catch (SQLException e) {
			throw new DataAccessException("Failed to fetch organizer performance");
		}

		return result;
	}
	
	
	
	// ----------------------organizer functions----------------------

	@Override
	public int createEvent(Event event) throws DataAccessException {
		String sql = "insert into events (organizer_id,title,description,category_id,venue_id,start_datetime,end_datetime,capacity,status,created_at) values (?,?,?,?,?,?,?,?,?,?)";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, event.getOrganizerId());
			ps.setString(2, event.getTitle());
			ps.setString(3, event.getDescription());
			ps.setInt(4, event.getCategoryId());
			ps.setInt(5, event.getVenueId());
			ps.setTimestamp(6, DateTimeUtil.toTimestamp(event.getStartDateTime()));

			ps.setTimestamp(7, DateTimeUtil.toTimestamp(event.getEndDateTime()));
			ps.setInt(8, event.getCapacity());
			ps.setString(9, EventStatus.DRAFT.toString());
			ps.setTimestamp(10, DateTimeUtil.toTimestamp(DateTimeUtil.nowUtc()));
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				} else {
					throw new DataAccessException("Event creation failed");
				}
			}

		} catch (Exception e) {
			throw new DataAccessException("Failed to create event");
		}
	}

	
	@Override
	public boolean updateEventSchedule(int eventId, Instant start, Instant end) throws DataAccessException {
		String sql = "update events set start_datetime=?, end_datetime=?, updated_at=? where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setTimestamp(1, DateTimeUtil.toTimestamp(start));
			ps.setTimestamp(2, DateTimeUtil.toTimestamp(end));
			Instant now = DateTimeUtil.nowUtc();
			ps.setTimestamp(3, Timestamp.from(now));

			ps.setInt(4, eventId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Failed to update the event schedule");
		}
	}

	@Override
	public boolean updateEventDetails(int eventId, String title, String description, int categoryId, int venueId)
			throws DataAccessException {
		
		String sql = "update events set title=?, description=?, category_id=?, venue_id=?, updated_at=? where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, title);
			ps.setString(2, description);
			ps.setInt(3, categoryId);
			ps.setInt(4, venueId);
			Instant now = DateTimeUtil.nowUtc();
			ps.setTimestamp(5, Timestamp.from(now));
			ps.setInt(6, eventId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Failed to update event details");
		}
	}

	@Override
	public boolean updateEventCapacity(int eventId, int capacity) throws DataAccessException {
		String sql = "update events set capacity=?, updated_at=? where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, capacity);
			Instant now = DateTimeUtil.nowUtc();
			ps.setTimestamp(2, Timestamp.from(now));

			ps.setInt(3, eventId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Failed to update event capacity");
		}
	}

	@Override
	public boolean updateEventStatus(int eventId, EventStatus status) throws DataAccessException {
		String sql = "update events set status=?, updated_at=? where event_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, status.name());
			Instant now = DateTimeUtil.nowUtc();
			ps.setTimestamp(2, Timestamp.from(now));

			ps.setInt(3, eventId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DataAccessException("Failed to update event status");
		}
	}
	
	//----------------------end

	@Override
	public List<Event> getEventsByOrganizer(int organizerId) throws DataAccessException {
		String sql = "select * from events where organizer_id=?";
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, organizerId);
			ResultSet rs = ps.executeQuery();
			return getEventList(rs);
		} catch (SQLException e) {
			throw new DataAccessException("Failed to fetch organizer events");
		}
	}

	@Override
	public List<OrganizerEventSummary> getEventSummaryByOrganizer(int organizerId) throws DataAccessException {
		String sql = "SELECT "
				+ "    e.event_id, "
				+ "    e.title, "
				+ "    e.status, "
				+ "    e.start_datetime, "
				+ "    COALESCE(SUM(t.total_quantity), 0) AS total_tickets, "
				+ "    COALESCE(SUM(t.total_quantity - t.available_quantity), 0) AS booked_tickets "
				+ "FROM events e "
				+ "LEFT JOIN tickets t ON e.event_id = t.event_id "
				+ "WHERE e.organizer_id = ? "
				+ "GROUP BY "
				+ "    e.event_id, "
				+ "    e.title, "
				+ "    e.status, "
				+ "    e.start_datetime "
				+ "ORDER BY "
				+ "    e.status, "
				+ "    e.start_datetime;";

		List<OrganizerEventSummary> eventSummaries = new ArrayList<>();
		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, organizerId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				OrganizerEventSummary eventSummary = new OrganizerEventSummary();
				eventSummary.setEventId(rs.getInt("event_id"));
				eventSummary.setTitle(rs.getString("title"));
				eventSummary.setStatus(rs.getString("status"));
				eventSummary.setTotalTickets(rs.getInt("total_tickets"));
				eventSummary.setBookedTickets(rs.getInt("booked_tickets"));
				eventSummaries.add(eventSummary);
			}

		} catch (SQLException e) {
			throw new DataAccessException("Failed to fetch organizer events");
		}
		return eventSummaries;
	}

	@Override
	public List<EventRevenueReport> getEventWiseRevenueReport() throws DataAccessException {
		List<EventRevenueReport> reports = new ArrayList<>();

		String sql = "select " +
				"    e.event_id, " +
				"    e.title, " +
				"    count(distinct r.registration_id) as total_registrations, " +
				"    sum(rt.quantity) as tickets_sold, " +
				"    sum(p.amount) as total_revenue, " +
				"    round(sum(p.amount) / nullif(sum(rt.quantity), 0), 2) as avg_ticket_price, " +
				"    sum( " +
				"        case " +
				"            when p.offer_id is not null then (t.price * rt.quantity) - p.amount " +
				"            else 0 " +
				"        end " +
				"    ) as total_discount_given " +
				"from events e " +
				"join registrations r on r.event_id = e.event_id " +
				"join registration_tickets rt on rt.registration_id = r.registration_id " +
				"join tickets t on t.ticket_id = rt.ticket_id " +
				"join payments p on p.registration_id = r.registration_id " +
				"where r.status = 'CONFIRMED' " +
				"  and p.payment_status = 'SUCCESS' " +
				"group by e.event_id, e.title " +
				"order by total_revenue desc";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				EventRevenueReport report = new EventRevenueReport();

				report.setEventId(rs.getInt("event_id"));
				report.setEventTitle(rs.getString("title"));
				report.setTotalRegistrations(rs.getInt("total_registrations"));
				report.setTicketsSold(rs.getInt("tickets_sold"));
				report.setTotalRevenue(rs.getDouble("total_revenue"));
				report.setAvgTicketPrice(rs.getDouble("avg_ticket_price"));
				report.setTotalDiscountGiven(rs.getDouble("total_discount_given"));

				reports.add(report);
			}

		} catch (Exception e) {
			throw new DataAccessException("failed to fetch event wise revenue report", e);
		}

		return reports;
	}

	

	@Override
	public List<EventRevenueReport> getEventWiseRevenueReportByOrganizer(int organizerId)
	        throws DataAccessException {
		List<EventRevenueReport> reports = new ArrayList<>();

		String sql = "select " +
				"    e.event_id, " +
				"    e.title, " +
				"    count(distinct r.registration_id) as total_registrations, " +
				"    sum(rt.quantity) as tickets_sold, " +
				"    sum(p.amount) as total_revenue, " +
				"    round(sum(p.amount) / nullif(sum(rt.quantity), 0), 2) as avg_ticket_price, " +
				"    sum( " +
				"        case " +
				"            when p.offer_id is not null then (t.price * rt.quantity) - p.amount " +
				"            else 0 " +
				"        end " +
				"    ) as total_discount_given " +
				"from events e " +
				"join registrations r on r.event_id = e.event_id " +
				"join registration_tickets rt on rt.registration_id = r.registration_id " +
				"join tickets t on t.ticket_id = rt.ticket_id " +
				"join payments p on p.registration_id = r.registration_id " +
				"where r.status = 'CONFIRMED' " +
				"  and p.payment_status = 'SUCCESS' " +
				"  and e.organizer_id = ? " + // important filter
				"group by e.event_id, e.title " +
				"order by total_revenue desc";

		try (Connection con = DBConnectionUtil.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, organizerId);

			try (ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					EventRevenueReport report = new EventRevenueReport();

					report.setEventId(rs.getInt("event_id"));
					report.setEventTitle(rs.getString("title"));
					report.setTotalRegistrations(rs.getInt("total_registrations"));
					report.setTicketsSold(rs.getInt("tickets_sold"));
					report.setTotalRevenue(rs.getDouble("total_revenue"));
					report.setAvgTicketPrice(rs.getDouble("avg_ticket_price"));
					report.setTotalDiscountGiven(rs.getDouble("total_discount_given"));

					reports.add(report);
				}
			}

		} catch (Exception e) {
			throw new DataAccessException(
					"failed to fetch organizer revenue report", e);
		}

		return reports;
	}
	

	// ---------------------- SEARCH & FILTER METHODS ----------------------
	
	// ---------------------- SEARCH BY CATEGORY ----------------------

	@Override
	public List<Event> searchByCategory(int categoryId) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE category_id = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, categoryId);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by category", e);
	    }
	    return events;
	}
	
	@Override
	public List<Category> getAllCategories() throws DataAccessException {

	    List<Category> categories = new ArrayList<>();

	    String query = "SELECT * FROM categories";

	    try (PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Category c = new Category(
	                    rs.getInt("category_id"),
	                    rs.getString("name")
	            );
	            categories.add(c);
	        }

	    } catch (SQLException e) {
	        throw new DataAccessException("Error fetching categories", e);
	    }

	    return categories;
	}

	// ---------------------- SEARCH BY DATE ----------------------
	
	@Override
	public List<Event> searchByDate(LocalDate date) throws DataAccessException {
	    if (date == null) return new ArrayList<>();
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE DATE(start_datetime) = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, date.toString());
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by date", e);
	    }
	    return events;
	}
	// ---------------------- SEARCH BY DATE RANGE ----------------------
	
	@Override
	public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
	    if (startDate == null || endDate == null || startDate.isAfter(endDate)) return new ArrayList<>();
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE DATE(start_datetime) BETWEEN ? AND ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, startDate.toString());
	        ps.setString(2, endDate.toString());
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by date range", e);
	    }
	    return events;
	}
	
	// ---------------------- SEARCH BY CITY ----------------------

	@Override
	public List<Event> searchByCity(int venueId) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT * FROM events WHERE venue_id = ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, venueId);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error searching by city", e);
	    }
	    return events;
	}

	
	// ---------------------- FILTER BY PRICE ----------------------

	@Override
	public List<Event> filterByPrice(double minPrice, double maxPrice) throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT DISTINCT e.* FROM events e " +
	                   "JOIN tickets t ON e.event_id = t.event_id " +
	                   "WHERE t.price BETWEEN ? AND ?";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setDouble(1, minPrice);
	        ps.setDouble(2, maxPrice);
	        try (ResultSet rs = ps.executeQuery()) {
	            events = getEventList(rs);
	        }
	    } catch (SQLException e) {
	        throw new DataAccessException("Error filtering by price", e);
	    }
	    return events;
	}

	// ---------------------- FILTER BY AVAILABILITY ----------------------
	
	@Override
	public List<Event> filterByAvailability() throws DataAccessException {
	    List<Event> events = new ArrayList<>();
	    String query = "SELECT DISTINCT e.* FROM events e " +
	                   "JOIN tickets t ON e.event_id = t.event_id " +
	                   "WHERE t.available_quantity > 0";
	    try (PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	        events = getEventList(rs);
	    } catch (SQLException e) {
	        throw new DataAccessException("Error filtering by availability", e);
	    }
	    return events;
	}
}