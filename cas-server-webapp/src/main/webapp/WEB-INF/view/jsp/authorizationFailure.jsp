<%@ page pageEncoding="UTF-8"
%><%@ page contentType="text/html; charset=UTF-8"
%><%@ page import="org.springframework.security.web.authentication.AbstractProcessingFilter"
%><jsp:directive.include file="/WEB-INF/view/jsp/asu/ui/includes/top.jsp" />

	<div id="login">
		<h2>Authorization Failure</h2>
		<div class="content internal_error">
			<div class="headline">You are not authorized to use this application for the following reason:</div>
			<%final Exception e = (Exception) request.getSession().getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
		    request.setAttribute("e", e);%>
			<div class="subtext"><c:out value="${e.message}" escapeXml="true" /></div>
		</div>
	</div>
	
<jsp:directive.include file="/WEB-INF/view/jsp/asu/ui/includes/bottom.jsp" />

	<script type="text/javascript" src="js/asu-signin.js"></script>
	<script type="text/javascript" src="themes/asu/onload.js"></script>
</body>
</html>