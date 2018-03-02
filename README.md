# Portlet for PULSE-US

Designed to provide JWT from Liferay CE for use as authentication/authorization token

## Deploy Instructions

1. Copy the portlet.properties.template file to portlet.properties file
1. Copy the keylocation property from the service app
1. Set jwtIssuer and jwtAudience to PULSE-US in both service application.properties and portlet.properties here
to `PULSE-US`
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
