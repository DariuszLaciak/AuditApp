<%@ page import="main.app.Common" %>
<%@ page import="main.app.HtmlContent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if( Common.isSessionActive(session)){
%>
<%=session.getId()%>
<div>blabla</div>
<%
    } else {
%>
<%=HtmlContent.makeLoginForm() %>
<%
    }
%>