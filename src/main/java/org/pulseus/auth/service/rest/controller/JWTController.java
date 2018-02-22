package org.pulseus.auth.service.rest.controller;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.SignatureAlgorithm;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pulseus.auth.portlet.security.filter.PortletSecurityFilter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
@RestController
public class JWTController {
	
	Configuration configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");
	
	private static final Logger log = LogManager.getLogger(JWTController.class);
	
	private static final String ORGANIZATIONS = "Orgs";
	private static final String AUTHORITIES = "Authorities";
	private static final String IDENTITY = "Identity";
	
    @RequestMapping(value = "/auth/jwt", method = RequestMethod.GET, produces = "application/json")
    public String getJwt(HttpServletRequest request) throws PortalException {
		User userLoggedIn = PortalUtil.getUser(request);

		Map<String, Object> jwtClaims = new HashMap<String, Object>();

		List<String> identity = new ArrayList<String>();

		identity.add(String.valueOf(userLoggedIn.getUserId()));
		identity.add(userLoggedIn.getEmailAddress());
		identity.add(userLoggedIn.getFullName());
		List<String> jwtAuthorities = new ArrayList<String>();

		List<Role> roles = userLoggedIn.getRoles();

		for(Role role : roles){
			jwtAuthorities.add(role.getName());
		}
		
		Map<String, Long> orgs = new HashMap<String, Long>();
		if(!userLoggedIn.getOrganizations().isEmpty()){
			for(Organization org : userLoggedIn.getOrganizations()){
				orgs.put(org.getName(), org.getOrganizationId());
			}
		}
		
		jwtClaims.put(ORGANIZATIONS, orgs);
		jwtClaims.put(IDENTITY, identity);
		jwtClaims.put(AUTHORITIES, jwtAuthorities);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 1);  // number of days to add
		Date expires = c.getTime();

		
		String jwt = Jwts.builder()
    	  .setExpiration(expires)
    	  .addClaims(jwtClaims)
    	  .signWith(SignatureAlgorithm.HS512, Base64.decode((configuration.get("jwt.key").getBytes()))).compact();
		
        String jwtJSON = "{\"token\": \""+ jwt +"\"}";
        
        return jwtJSON;
    }
    
    @RequestMapping(value = "/auth/jwt/setAcf", method = RequestMethod.POST, produces = "application/json")
    public String setAcf(@RequestHeader(value="Authorization") String authorization, @RequestBody String acf) throws PortalException {
    	String jwt = null;
        String oldJwt = authorization.split(" ")[1];

        // Parse old Jwt
        Jwt<Header, Claims> claims = Jwts.parser().setSigningKey(Base64.decode(configuration.get("jwt.key").getBytes())).parse(oldJwt);
        
        List<String> authorityInfo = (List<String>) claims.getBody().get("Authorities");
        List<String> identityInfo = (List<String>) claims.getBody().get("Identity");
        HashMap<String, Long> orgInfo = (HashMap<String, Long>) claims.getBody().get("Orgs");
        identityInfo.add(acf);
        
        Map<String, Object> jwtClaims = new HashMap<String, Object>();
        jwtClaims.put("Authorities", authorityInfo);
        jwtClaims.put("Identity", identityInfo);
        jwtClaims.put("Orgs", orgInfo);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 1);  // number of days to add
		Date expires = c.getTime();

		jwt = Jwts.builder()
    	  .setExpiration(expires)
    	  .addClaims(jwtClaims)
    	  .signWith(SignatureAlgorithm.HS512, Base64.decode((configuration.get("jwt.key").getBytes()))).compact();
		
        String jwtJSON = "{\"token\": \""+ jwt +"\"}";
        
        return jwtJSON;
    }
    
    @RequestMapping(value = "/auth/jwt/keepAlive", method = RequestMethod.POST, produces = "application/json")
    public String setKeepAlive(@RequestHeader(value="Authorization") String authorization) throws PortalException {
    	String jwt = null;
        String oldJwt = authorization.split(" ")[1];

        // Parse old Jwt
        Jwt<Header, Claims> claims = Jwts.parser().setSigningKey(Base64.decode(configuration.get("jwt.key").getBytes())).parse(oldJwt);
        
        List<String> authorityInfo = (List<String>) claims.getBody().get("Authorities");
        List<String> identityInfo = (List<String>) claims.getBody().get("Identity");
        HashMap<String, Long> orgInfo = (HashMap<String, Long>) claims.getBody().get("Orgs");
        
        Map<String, Object> jwtClaims = new HashMap<String, Object>();
        jwtClaims.put("Authorities", authorityInfo);
        jwtClaims.put("Identity", identityInfo);
        jwtClaims.put("Orgs", orgInfo);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 1);  // number of days to add
		Date expires = c.getTime();

		jwt = Jwts.builder()
    	  .setExpiration(expires)
    	  .addClaims(jwtClaims)
    	  .signWith(SignatureAlgorithm.HS512, Base64.decode((configuration.get("jwt.key").getBytes()))).compact();
		
        String jwtJSON = "{\"token\": \""+ jwt +"\"}";
        
        return jwtJSON;
    }
}
