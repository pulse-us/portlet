<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="init.jsp" %>
<%
String portalUrl = (String) renderRequest.getAttribute("portalUrl");
%>
<iframe width="100%" height="100%" frameborder="0" src="<%= portalUrl %>"></iframe>
