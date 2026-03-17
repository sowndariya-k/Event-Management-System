package com.ems.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.ems.dao.RoleDao;
import com.ems.model.Role;

public class RoleDaoImpl implements RoleDao {
	@Override
    public List<Role> getRoles() {

        List<Role> roles = new ArrayList<>();

        roles.add(new Role(1, "ADMIN", null));
        roles.add(new Role(2, "ATTENDEE", null));
        roles.add(new Role(3, "ORGANIZER", null));

        return roles;
    }

}
