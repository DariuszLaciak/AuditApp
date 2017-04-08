<%@ page import="main.app.orm.User" %>
<%@ page import="main.app.Common" %>
<%@ page import="main.app.enums.LoginType" %>
<%@ page import="main.app.HtmlContent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(Common.isSessionActive(session)) {
        User user = (User) session.getAttribute("userData");
        LoginType type = user.getRole();
        out.println(HtmlContent.makeUserMenu(type));

%>
<div id="innerContent"></div>
<%
    }
%>