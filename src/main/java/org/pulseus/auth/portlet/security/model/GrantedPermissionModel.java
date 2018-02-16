package org.pulseus.auth.portlet.security.model;

import org.springframework.security.core.GrantedAuthority;

public class GrantedPermissionModel implements GrantedAuthority {
	
	private static final long serialVersionUID = 1L;
	
	private String authority;
	
	private Boolean value;

	public GrantedPermissionModel(String authority, Boolean value) {
		super();
		this.authority = authority;
		this.value = value;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
	
}
