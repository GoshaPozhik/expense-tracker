<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Finance Tracker - ${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="/WEB-INF/jsp/common/navbar.jsp" %>
    <main class="container mt-4">
        <jsp:doBody/>
    </main>
<%--    <footer class="footer mt-auto py-3 position-absolute bottom-0 w-100">--%>
<%--        <div class="container text-center">--%>
<%--            <span class="text-muted">&copy; 2025 Finance Tracker. Все права защищены.</span>--%>
<%--        </div>--%>
<%--    </footer>--%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
