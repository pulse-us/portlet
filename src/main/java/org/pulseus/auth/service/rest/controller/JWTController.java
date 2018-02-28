package org.pulseus.auth.service.rest.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;

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

	@RequestMapping(value = "/addusers", method = RequestMethod.GET)
	public void addusers() throws Exception {
		System.out.println("adding users");
		ServiceContext serviceContext = new ServiceContext();
		Long companyId = CompanyThreadLocal.getCompanyId();	
		Role role = RoleLocalServiceUtil.getRole(companyId, "Administrator");
		Long roleId = 	role.getRoleId();


		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/" + "users.csv")));
		while ((line = br.readLine()) != null) {

			// use comma as separator
			String[] value = line.split(cvsSplitBy);


			long creatorUserId = 0;
			boolean autoPassword = false;
			boolean autoScreenName = false;
			long facebookId = 0;
			String openId = StringPool.BLANK;
			Locale locale = serviceContext.getLocale();
			String middleName = StringPool.BLANK;
			int prefixId = 0;
			int suffixId = 0;
			boolean male = true;
			int birthdayMonth = Calendar.JANUARY;
			int birthdayDay = 1;
			int birthdayYear = 1970;
			String jobTitle = StringPool.BLANK;
			long[] groupIds = null;
			long[] organizationIds = null;
			long[] roleIds = {roleId};
			long[] userGroupIds = null;
			boolean sendEmail = false;

			String screenName = value[0];
			String emailAddress = value[1];
			String firstName = value[2];
			String lastName = value[3];
			String password1 = value[4];
			String password2 = value[4];

			String uuid = UUID.randomUUID().toString().replace("-", "");
			serviceContext.setUuid(uuid);

			User user = UserLocalServiceUtil.addUser(
					creatorUserId, companyId, autoPassword, password1, password2,
					autoScreenName, screenName, emailAddress, facebookId, openId,
					locale, firstName, middleName, lastName, prefixId, suffixId, male,
					birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
					organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);

			/*user = UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), true);

			user = UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);*/

			System.out.println("user added");
		}

		br.close();
	}
}
