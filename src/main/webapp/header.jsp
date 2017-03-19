<%@ page import="main.app.Common" %>
<%@ page import="main.app.HtmlContent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if( Common.isSessionActive(session)){
        out.println(HtmlContent.makeLoggedPanel(session));
    } else {
        out.println(HtmlContent.makeLoginForm());
    }
%>