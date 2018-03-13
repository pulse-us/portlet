package org.pulseus.auth.service.rest.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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

	private static final Logger log = LogManager.getLogger(JWTController.class);

	@RequestMapping(value = "/addusers", method = RequestMethod.GET)
	public void addusers() throws Exception {
		log.info("adding users");
		ServiceContext serviceContext = new ServiceContext();
		Long companyId = CompanyThreadLocal.getCompanyId();	
		Configuration configuration = ConfigurationFactoryUtil.getConfiguration(PortalClassLoaderUtil.getClassLoader(), "portlet");
		User user1 = UserLocalServiceUtil.getUserByScreenName(companyId, "test");

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		Organization parentOrganization = OrganizationLocalServiceUtil.getOrganization(companyId, "pulse-us");
		Long parentOrganizationId = parentOrganization.getOrganizationId();
		List<Organization> orgList = OrganizationLocalServiceUtil.getOrganizations(companyId, parentOrganizationId);
		List<String> orgNames = new ArrayList<String>();
		List<String> subOrgNames = new ArrayList<String>();
		for (Organization org : orgList) {
			orgNames.add(org.getName());
		}


		br = new BufferedReader(new FileReader(configuration.get("csvfile")));

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
		long[] userGroupIds = null;
		boolean sendEmail = false;
		Organization org1;
		Organization org2;
		Long orgId1 = null;
		Long orgId2 = null;


		while (true) {
			try {
				if ((line = br.readLine()) != null) {
					// use comma as separator
					String[] value = line.split(cvsSplitBy);
					Role role = RoleLocalServiceUtil.getRole(companyId, value[5]);
					Long roleId = 	role.getRoleId();

					//create an org if the org is not present
					if(orgNames.contains(value[6])) {
						org1 = OrganizationLocalServiceUtil.getOrganization(companyId, value[6]);
						orgId1 = org1.getOrganizationId();
					}
					else {
						OrganizationLocalServiceUtil.addOrganization(user1.getPrimaryKey(), parentOrganizationId, value[6], false);
						log.info("New organization created "+value[6]);
						org1 = OrganizationLocalServiceUtil.getOrganization(companyId, value[6]);
						orgId1 = org1.getOrganizationId();
					}
					List<Organization> subOrgList = OrganizationLocalServiceUtil.getOrganizations(companyId, orgId1);
					for (Organization org : subOrgList) {
						subOrgNames.add(org.getName());
					}

					//create an suborg if the org is not present
					if(subOrgNames.contains(value[7])) {
						org2 = OrganizationLocalServiceUtil.getOrganization(companyId, value[7]);
						orgId2 = org2.getOrganizationId();
					}

					else {
						OrganizationLocalServiceUtil.addOrganization(user1.getPrimaryKey(), orgId1, value[7], false);
						log.info("New sub-organization created "+value[7]);
						org2 = OrganizationLocalServiceUtil.getOrganization(companyId, value[7]);
						orgId2 = org2.getOrganizationId();
					}
					long[] organizationIds = {orgId1,orgId2};
					long[] roleIds = {roleId};

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

					log.info("user added "+value[0]);
				}

				else {
					break;
				}
			}

			catch(Exception e) {
				log.info(e.getMessage());
				log.info("The line causing error is " + Arrays.toString(line.split(cvsSplitBy)));
				continue;
			}

		}

	}
}