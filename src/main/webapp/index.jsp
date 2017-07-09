<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="style.css">
    <link rel="stylesheet" href="lib/jquery-ui.min.css">
    <script src="lib/jquery-3.1.1.min.js" type="application/javascript"></script>
    <script src="jscript/functions.js" type="application/javascript"></script>
    <script src="jscript/common.js" type="application/javascript"></script>
    <script src="lib/jquery.selectlistactions.js" type='application/javascript'></script>
    <script src="lib/jquery-ui.min.js" type="application/javascript"></script>
    <script src="lib/UI-pl.js" type="application/javascript"></script>

    <!-- Charts -->
    <script src="lib/amcharts/amcharts.js"></script>
    <script src="lib/amcharts/radar.js"></script>
    <script src="lib/amcharts/serial.js"></script>
    <script src="lib/amcharts/lang/pl.js"></script>
    <script src="lib/amcharts/export.min.js"></script>
    <%--<link rel="stylesheet" href="https://www.amcharts.com/lib/3/plugins/export/export.css" type="text/css" media="all" />--%>
    <script src="lib/amcharts/themes/light.js"></script>

    <title>SZIP – System Zarządzania Innowacjami w Przedsiębiorstwie</title>
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
<div id="dataContainer"></div>
</body>
</html>
