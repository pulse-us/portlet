package org.pulseus.auth.service.rest.controller;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;

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
		final String CSV_FILE_PATH = configuration.get("csvFile");

		Organization parentOrganization = OrganizationLocalServiceUtil.getOrganization(companyId, "pulse-us");
		Long parentOrganizationId = parentOrganization.getOrganizationId();
		List<Organization> orgList = OrganizationLocalServiceUtil.getOrganizations(companyId, parentOrganizationId);
		List<String> orgNames = new ArrayList<String>();
		List<String> subOrgNames = new ArrayList<String>();
		for (Organization org : orgList) {
			orgNames.add(org.getName());
		}

		Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
				.withFirstRecordAsHeader()
				.withIgnoreHeaderCase()
				.withTrim());

		Iterable<CSVRecord> csvRecords = csvParser.getRecords();

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
		Organization stateOrg;
		Organization acfOrg;
		
		Role orgAdminRole = RoleLocalServiceUtil.getRole(companyId, RoleConstants.ORGANIZATION_ADMINISTRATOR);
		Long orgAdminRoleId = 	orgAdminRole.getRoleId();
		
		
		for (CSVRecord csvRecord : csvRecords) {
			long[] organizationIds = null;
			long[] roleIds = null;
			Long orgId1 = null;
			Long orgId2 = null;
			try {			
				Role role = RoleLocalServiceUtil.getRole(companyId, csvRecord.get("role"));
				Long roleId = 	role.getRoleId();


				if(csvRecord.get("role").equals("ROLE_ADMIN")) {
					organizationIds=new long[] {parentOrganizationId};
				}
				else {
					if(csvRecord.get("role").equals("ROLE_ORG_ADMIN") || csvRecord.get("role").equals("ROLE_PROVIDER")) {
						//create an org if the org is not present
						if(orgNames.contains(csvRecord.get("stateOrg"))) {
							stateOrg = OrganizationLocalServiceUtil.getOrganization(companyId, csvRecord.get("stateOrg"));
							orgId1 = stateOrg.getOrganizationId();
						}
						else {
							OrganizationLocalServiceUtil.addOrganization(user1.getPrimaryKey(), parentOrganizationId, csvRecord.get("stateOrg"), false);
							log.info("New organization created "+csvRecord.get("stateOrg"));
							stateOrg = OrganizationLocalServiceUtil.getOrganization(companyId, csvRecord.get("stateOrg"));
							orgId1 = stateOrg.getOrganizationId();
						}
					}

					else {
						log.info("A state org can only be assigned to a user with roles ROLE_ORG_ADMIN or ROLE_PROVIDER");
					}
					List<Organization> subOrgList = OrganizationLocalServiceUtil.getOrganizations(companyId, orgId1);
					for (Organization org : subOrgList) {
						subOrgNames.add(org.getName());
					}
					if((!(csvRecord.get("role").equals("ROLE_ORG_ADMIN")))) {
						//create a suborg if the suborg is not present
						if(subOrgNames.contains(csvRecord.get("acfOrg"))) {
							acfOrg = OrganizationLocalServiceUtil.getOrganization(companyId, csvRecord.get("acfOrg"));
							orgId2 = acfOrg.getOrganizationId();
						}

						else {
							OrganizationLocalServiceUtil.addOrganization(user1.getPrimaryKey(), orgId1, csvRecord.get("acfOrg"), false);
							log.info("New sub-organization created "+csvRecord.get("acfOrg"));
							acfOrg = OrganizationLocalServiceUtil.getOrganization(companyId, csvRecord.get("acfOrg"));
							orgId2 = acfOrg.getOrganizationId();
						}
					}
				}
				if(orgId1 != null && orgId2 != null && organizationIds == null) {
					organizationIds=  new long[]{orgId1,orgId2};
				}
				else if(orgId1 != null && organizationIds == null){
					organizationIds=new long[]{orgId1};
				}
				 roleIds = new long[]{roleId};
				 
				String screenName = csvRecord.get("username");
				String emailAddress = csvRecord.get("email");
				String firstName = csvRecord.get("firstname");
				String lastName = csvRecord.get("lastname");
				String password1 = csvRecord.get("password");
				String password2 = csvRecord.get("password");

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
				if(csvRecord.get("role").equals("ROLE_ORG_ADMIN")){
					long[] adminIds = {orgAdminRoleId};
					Organization adminOrg = OrganizationLocalServiceUtil.getOrganization(companyId, csvRecord.get("stateOrg"));
					UserGroupRoleLocalServiceUtil.addUserGroupRoles(user.getUserId(), adminOrg.getGroupId(), adminIds);
				}
				log.info("user added "+csvRecord.get("username"));

			}

			catch(Exception e) {
				log.info(e.getMessage());
				log.info("The line causing error is " + csvRecord.toString());
				continue;
			}

		}
	}
}