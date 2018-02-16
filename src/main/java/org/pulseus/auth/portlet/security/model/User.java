package org.pulseus.auth.portlet.security.model;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface User extends Authentication {
	
	public Long getId();	
	public String getSubjectName();
	public void setSubjectName(String subject);
	
	public void setFirstName(String firstName);
	public String getFirstName();
	public void setLastName(String lastName);
	public String getLastName();
	
	public Set<GrantedPermissionModel> getPermissions();
	public void addPermission(GrantedPermissionModel permission);
	public void removePermission(String permissionValue);
	
	public Set<GrantedOrganizationModel> getOrganizations();
	public void addOrganization(GrantedOrganizationModel organization);
	public void removeOrganization(String organization);
	
	// UserDetails interface
	/*@Override
	public String getPassword();

	@Override
	public String getUsername();

	@Override
	public boolean isAccountNonExpired();

	@Override
	public boolean isAccountNonLocked();

	@Override
	public boolean isCredentialsNonExpired();

	@Override
	public boolean isEnabled();*/
	
	// Authentication Interface
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities();

	@Override
	public Object getCredentials();

	@Override
	public Object getDetails();

	@Override
	public Object getPrincipal();
	
	@Override
	public boolean isAuthenticated();

	@Override
	public void setAuthenticated(boolean arg0) throws IllegalArgumentException;
	
	@Override
	public String getName();
	
}
