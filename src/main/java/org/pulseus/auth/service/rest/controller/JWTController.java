package org.pulseus.auth.service.rest.controller;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalUtil;

@RestController
public class JWTController {
	
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
		
		List<String> orgs = new ArrayList<String>();
		if(!userLoggedIn.getOrganizations().isEmpty()){
			for(Organization org : userLoggedIn.getOrganizations()){
				orgs.add(org.getName());
			}
		}
		
		jwtClaims.put("Orgs", orgs);
		jwtClaims.put("Identity", identity);
		jwtClaims.put("Authorities", jwtAuthorities);
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 1);  // number of days to add
		Date expires = c.getTime();

		
		String jwt = Jwts.builder()
    	  .setSubject(userLoggedIn.getEmailAddress())
    	  .setExpiration(expires)
    	  .addClaims(jwtClaims)
    	  .signWith(SignatureAlgorithm.HS512, Base64.decode(("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").getBytes()))
    	  .compact();
		
        String jwtJSON = "{\"token\": \""+ jwt +"\"}";
        
        return jwtJSON;
    }
 
 
}
