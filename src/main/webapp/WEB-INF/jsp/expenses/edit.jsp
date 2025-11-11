<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Редактирование расхода">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <h2>Редактировать расход</h2>
            <div class="auth-card">
                <c:if test="${not empty requestScope.error}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${requestScope.error}"/>
                    </div>
                </c:if>

                <form method="post" action="<c:url value="/expenses/edit"/>">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <input type="hidden" name="expenseId" value="${expense.id}">
                    <input type="hidden" name="walletId" value="${expense.walletId}">

                    <div class="mb-3">
                        <label for="amount" class="form-label">Сумма</label>
                        <input type="number" step="0.01" class="form-control" id="amount" name="amount" value="${expense.amount}" required>
                    </div>

                    <div class="mb-3">
                        <label for="categoryId" class="form-label">Категория</label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <c:forEach var="category" items="${categories}">
                                <c:choose>
                                    <c:when test="${category.id == expense.categoryId}">
                                        <option value="${category.id}" selected>${category.name}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${category.id}">${category.name}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Описание</label>
                        <input type="text" class="form-control" id="description" name="description" value="${expense.description}" maxlength="255">
                    </div>

                    <div class="d-flex justify-content-end">
                        <a href="<c:url value="/expenses"><c:param name="walletId" value="${expense.walletId}"/></c:url>" class="btn btn-secondary me-2">Отмена</a>
                        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="<c:url value="/js/app.js"/>"></script>
</t:main>
