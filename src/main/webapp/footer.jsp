<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
    Calendar c = Calendar.getInstance();
    Date date = new Date();
    try {
        date = sdf.parse("2017");
    } catch (ParseException e) {
        e.printStackTrace();
    }
    c.setTime(new Date());

%>
<div class="footer">Copyright &copy Elżbieta Łaciak
    (<%=c.getWeekYear() > 2017 ? sdf.format(date) + " - " + sdf.format(new Date()) : sdf.format(date) %>)
</div>
