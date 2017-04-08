<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="main.app.Common" %>
<html>
<head>
    <link rel="stylesheet" href="style.css">
    <script src="lib/jquery-3.1.1.min.js" type="application/javascript"></script>
    <script src="jscript/functions.js" type="application/javascript"></script>
    <script src="jscript/common.js" type="application/javascript"></script>
    <title>Audyt innowacojności przedsiębiorstwa</title>
</head>
<body>
<div id="main">
    <div id="header">
        <jsp:include page="header.jsp"/>
    </div>
    <div id="content">
        <jsp:include page="content.jsp"/>
    </div>
    <div id="footer">
        <jsp:include page="footer.jsp"/>
    </div>
</div>
<div id="infoBox"></div>
</body>
</html>
