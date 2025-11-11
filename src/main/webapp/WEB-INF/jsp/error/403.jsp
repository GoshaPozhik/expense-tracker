<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Доступ запрещен">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <div class="auth-card">
                <h1 class="display-1">403</h1>
                <h2 class="mb-4">Доступ запрещен</h2>
                <p class="text-muted mb-4">У вас нет прав для доступа к этому ресурсу.</p>
                <a href="<c:url value="/home"/>" class="btn btn-primary">Вернуться на главную</a>
            </div>
        </div>
    </div>
</t:main>

