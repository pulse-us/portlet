package org.pulseus.auth.service.rest.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
@RestController
public class JWTController {

	Configuration configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");

	private static final Logger log = LogManager.getLogger(JWTController.class);

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
				.signWith(SignatureAlgorithm.HS512, Base64.decode((configuration.get("jwt.key").getBytes()))).compact();

		String jwtJSON = "{\"token\": \""+ jwt +"\"}";

		return jwtJSON;
	}
}
