package org.pulseus.auth.portlet.security.filter;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pulseus.auth.portlet.security.model.GrantedOrganizationModel;
import org.pulseus.auth.portlet.security.model.GrantedPermissionModel;
import org.pulseus.auth.portlet.security.model.UserModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletSession;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

public class PortletSecurityFilter implements PortletFilter, ActionFilter, RenderFilter, EventFilter, ResourceFilter {
	
	private static final Logger log = LogManager.getLogger(PortletSecurityFilter.class);

    private static final String SECURITY_TOKEN = "SECURITY_TOKEN";

    @Override
    public void doFilter(ActionRequest request, ActionResponse response, FilterChain chain) throws IOException,
            PortletException {
        if (filter(request, response, chain)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(ResourceRequest request, ResourceResponse response, FilterChain chain) throws IOException,
            PortletException {
        if (filter(request, response, chain)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(EventRequest request, EventResponse response, FilterChain chain) throws IOException,
            PortletException {
        if (filter(request, response, chain)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void doFilter(RenderRequest request, RenderResponse response, FilterChain chain) throws IOException,
            PortletException {
        if (filter(request, response, chain)) {
            chain.doFilter(request, response);
        }
    }

    private boolean filter(PortletRequest request, PortletResponse response, FilterChain chain) throws IOException,
            PortletException {
    	log.info("-------------------------- doFilter");
        User user = null;
        List<Organization> organizations = null;
        
        UserModel usermodel = new UserModel();
        
        try {
            user = PortalUtil.getUser(request);
            if (user != null) {
                usermodel.setLastName(user.getLastName());
                usermodel.setFirstName(user.getFirstName());
                usermodel.setAuthenticated(true);
                usermodel.setSubjectName(user.getScreenName());
                usermodel.setEmail(user.getEmailAddress());
                usermodel.setId(user.getUserId());
                
                organizations = user.getOrganizations();
                for (Organization org: organizations) {
                	GrantedOrganizationModel organization = new GrantedOrganizationModel();
                	organization.setOrgId(org.getOrganizationId());
                	usermodel.addOrganization(organization);
                }
                
                for(Role role : user.getRoles()){
                	GrantedPermissionModel gpm = new GrantedPermissionModel(role.getName(), true);
                	usermodel.addPermission(gpm);
                }
               
            }
        } catch (PortalException | SystemException e) {
        	log.error("PortalException", e);
        }

        if (user == null) {
        	log.info("-------------------------- no logged in user");
            // If no User from Portal stop the chain here.
            return false;
        }

        successfulAuthentication(request, response, usermodel);
        return true;
    }
    
    public boolean checkPermission(String actionId, Long groupId, PortletRequest request)
    { 
    	boolean result=false; 
    	ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY); 
    	
    	long localGroupId = (groupId == null) ? themeDisplay.getSiteGroupId() : groupId; 
    	
    	String name = PortalUtil.getPortletId(request); 
    	String primKey = themeDisplay.getLayout().getPlid() + LiferayPortletSession.LAYOUT_SEPARATOR + name; 
    	
    	PermissionChecker permissionChecker = themeDisplay.getPermissionChecker(); 
    	
    	result = permissionChecker.hasPermission(localGroupId, name, primKey, actionId); 
    	
    	return result; 
    }

    protected void successfulAuthentication(PortletRequest request, PortletResponse response,
            Authentication authResult) throws IOException, PortletException {

    	log.info("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        SecurityContextHolder.getContext().setAuthentication(authResult);

        PortletSession session = request.getPortletSession();
        session.setAttribute(SECURITY_TOKEN, authResult);
    }

    @Override
    public void destroy() {
    	log.info("Action filter destroy.");
    }

    @Override
    public void init(FilterConfig arg0) throws PortletException {
    	log.info("Action filter init.");
    }

}
