<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty param.success}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <c:out value="${param.success}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:out value="${param.error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <c:out value="${requestScope.error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

