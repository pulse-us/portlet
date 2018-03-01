package org.pulseus.auth.service.rest.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringPool;

@RestController
public class CSVController {
	Configuration configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");

	private static final Logger log = LogManager.getLogger(JWTController.class);
	
	@RequestMapping(value = "/addusersnew", method = RequestMethod.GET)
	public void addusers() throws Exception {
		System.out.println("adding users");
		ServiceContext serviceContext = new ServiceContext();
		Long companyId = CompanyThreadLocal.getCompanyId();	
		


		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		br = new BufferedReader(new FileReader("/opt/pulse/users.csv"));
		while ((line = br.readLine()) != null) {

			// use comma as separator
			String[] value = line.split(cvsSplitBy);
			Role role = RoleLocalServiceUtil.getRole(companyId, value[5]);
			Long roleId = 	role.getRoleId();
			
			Organization org1 = OrganizationLocalServiceUtil.getOrganization(companyId, value[6]);
			Long orgId1 = org1.getOrganizationId();

			Organization org2 = OrganizationLocalServiceUtil.getOrganization(companyId, value[7]);
			Long orgId2 = org2.getOrganizationId();
			
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
			long[] organizationIds = {orgId1,orgId2};
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

			user = UserLocalServiceUtil.updateEmailAddressVerified(user.getUserId(), true);
			user = UserLocalServiceUtil.updatePasswordReset(user.getUserId(), false);

			System.out.println("user added");
		}

		br.close();
	}
}