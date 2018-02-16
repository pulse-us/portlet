package org.pulseus.auth.portlet.security.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

public class UserModel implements User {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String subjectName;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String title;
	private Date signatureDate;
	private Date complianceSignatureDate;
	
	private int failedLoginCount;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	private boolean accountEnabled;
	private boolean authenticated;
	
	private String password;
	
	private Set<GrantedPermissionModel> permissions = new HashSet<GrantedPermissionModel>();
	private Set<GrantedOrganizationModel> organizations = new HashSet<GrantedOrganizationModel>();
	
	@Override
	public void addPermission(GrantedPermissionModel permission) {
		this.permissions.add(permission);
	}
	
	@Override
	public void removePermission(String permission) {
		this.permissions.remove(new GrantedPermissionModel(permission, true));
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getAuthorities();
	}
	
	@Override
	public Object getCredentials() {
		return this.getPassword();
	}
	@Override
	public Object getDetails() {
		return this;
	}
	@Override
	public Object getPrincipal() {
		return this;
	}
	
	@Override
	public String getName() {
		return subjectName;
	}

	@Override
	public void addOrganization(GrantedOrganizationModel organization) {
		this.organizations.add(organization);
		
	}

	@Override
	public void removeOrganization(String organization) {
		for(GrantedOrganizationModel model : this.organizations) {
			if (model.getOrganizationName().equalsIgnoreCase(organization)) {
				this.organizations.remove(model);
			}
				
		}
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}

	public Date getComplianceSignatureDate() {
		return complianceSignatureDate;
	}

	public void setComplianceSignatureDate(Date complianceSignatureDate) {
		this.complianceSignatureDate = complianceSignatureDate;
	}

	public int getFailedLoginCount() {
		return failedLoginCount;
	}

	public void setFailedLoginCount(int failedLoginCount) {
		this.failedLoginCount = failedLoginCount;
	}

	public boolean isAccountExpired() {
		return accountExpired;
	}

	public void setAccountExpired(boolean accountExpired) {
		this.accountExpired = accountExpired;
	}

	public boolean isAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public boolean isAccountEnabled() {
		return accountEnabled;
	}

	public void setAccountEnabled(boolean accountEnabled) {
		this.accountEnabled = accountEnabled;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<GrantedPermissionModel> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<GrantedPermissionModel> permissions) {
		this.permissions = permissions;
	}

	public Set<GrantedOrganizationModel> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(Set<GrantedOrganizationModel> organizations) {
		this.organizations = organizations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
