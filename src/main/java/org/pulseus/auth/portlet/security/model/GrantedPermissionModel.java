package org.pulseus.auth.portlet.security.model;

import org.springframework.security.core.GrantedAuthority;

public class GrantedPermissionModel implements GrantedAuthority {
	
	private static final long serialVersionUID = 1L;
	
	private String authority;
	
	private Boolean boolGrantedValue;

	public GrantedPermissionModel(String authority, Boolean boolGrantedValue) {
		super();
		this.authority = authority;
		this.boolGrantedValue = boolGrantedValue;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Boolean getBoolGrantedValue() {
		return boolGrantedValue;
	}

	public void setBoolGrantedValue(Boolean value) {
		this.boolGrantedValue = value;
	}
	
}
