<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Редактирование кошелька">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <h2>Редактировать кошелек</h2>
            <div class="auth-card">
                <c:if test="${not empty requestScope.error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${requestScope.error}"/>
                    </div>
                </c:if>

                <form method="post" action="<c:url value="/wallets/edit"/>">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="walletId" value="${wallet.id}">

                    <div class="mb-3">
                        <label for="walletName" class="form-label">Название кошелька</label>
                        <input type="text" class="form-control" id="walletName" name="walletName" 
                               value="<c:out value="${wallet.name}"/>" 
                               maxlength="100" required>
                        <div class="form-text">Максимум 100 символов</div>
                    </div>

                    <div class="d-flex justify-content-end">
                        <a href="<c:url value="/home"/>" class="btn btn-secondary me-2">Отмена</a>
                        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</t:main>

