### Portlet for PULSE-US
Designed to provide JWT from Liferay CE for use as authentication/authorization token

### Deploy Instructions

1. Copy the portlet.properties.template file to portlet.properties file
1. Go to the location of the service's `keyLocation` application.properties file and Base64 encode that text
1. Take the base64 encoded key and put it as the jwt.key value in portlet.properties
1. `./gradlew assemble` in root directory
1. `cp build/libs/PULSEAuthPortlet.war <liferay-installation>/deploy/`
1. Sign into Liferay
1. Click addition symbol in top right corner
1. Applications -> PULSE -> PULSE Auth Portlet -> Add
1. Should see a portlet show up on the left called PULSE Auth Portlet