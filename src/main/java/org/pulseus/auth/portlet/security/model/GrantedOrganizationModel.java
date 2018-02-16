package org.pulseus.auth.portlet.security.model;

import java.util.HashSet;
import java.util.Set;

public class GrantedOrganizationModel {

	private String organizationName;
	private long groupId;
	private Set<GrantedPermissionModel> permissions = new HashSet<GrantedPermissionModel>();
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public Set<GrantedPermissionModel> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<GrantedPermissionModel> permissions) {
		this.permissions = permissions;
	}
	
	
}
