# Portlet for PULSE-US

Designed to provide JWT from Liferay CE for use as authentication/authorization token. Also provides capability to upload users in a CSV file.

## Deploy Instructions

1. Copy the portlet.properties.template file to portlet.properties file
1. Copy the keylocation property from the service app
1. Set jwtIssuer and jwtAudience to PULSE-US in both service application.properties and portlet.properties here to `PULSE-US`
1. `./gradlew assemble` in root directory
1. `cp build/libs/PULSEAuthPortlet.war <liferay-installation>/deploy/`
1. Sign into Liferay
1. Click addition symbol in top right corner
1. Applications -> PULSE -> PULSE Auth Portlet -> Add
1. Should see a portlet show up on the left called PULSE Auth Portlet

## Re-deployment

Once the portlet has been deployed, when it needs to be re-deployed set an environment variable and then run the deploy script:

```
> export PULSE_LIFERAY=/the/directory/where/liferay/lives
> ./deploy-portlet.sh
```

## User & Group Management

PULSE leverages the User Management capabilities of Liferay CE 7.2, and detailed documentation for that management can be found at the Liferay [Community Documentation](https://dev.liferay.com/discover/portal/-/knowledge_base/7-0/user-management).

### User Upload

The portlet has a REST based endpoint initiates a CSV upload of users. The system will look for a file of users at the location specified in the `csvFile` value from the portlet.properties file. Clicking on the url `https://LOCATION OF PULSE DMZ/o/PULSEAuthPortlet/rest/addusers` will initiate the user upload. The structure of the upload CSV is defined in the [CSV upload data description file](./CSV_upload_data_description.xlsx).

### User ROLES

PULSE Users have one of three ROLES
* **ROLE_PROVIDER**: A health care provider. All providers must be assigned to a Organization and an Alternate Care Facility (ACF) under that organization-specific implementation of PULSE
* **ROLE_ORG_ADMIN**: An administrator of an organization-specific implementation of PULSE (e.g., a state agency or a county department)
* **ROLE_ADMIN**: Administrator or Operator of PULSE-US, not tied specifically to any organization-specific implementation

ROLEs determine what a PULSE User is able to do in the PULSE system. The Role Based Access Control (RBAC) matrix is defined in the [RBAC Matrix](./RBAC_Specs.xlsx)

### Liferay Organizations

The top level Organization in Liferay is `pulse-us`. Only ROLE_ADMIN may be assigned to this Organization. The first sub-organization level are the implementation-specific organizationss, and all have the form `pulse-*`, where `*` can be any string except `us`. All ACFs are sub-organizations of one of the implementation-specific organizations, and can take any form, as long as it doesn't start with `pulse-`

## Checkstyle

During development checkstyle is run as part of the build process. If a build needs to bypass checkstyle, it can be done with a command similar to:

```
> ./gradlew build -x checkstyleMain
```

Though the end goal is to have the project pass all of the checkstyle rules, until that is possible they can be ignored for now, with the understanding that as development progresses we want to avoid adding new errors, and continue to burn down old ones.
