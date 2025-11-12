<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:main title="Расходы">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Расходы по кошельку</h1>
        <div>
            <button class="btn btn-info" data-bs-toggle="modal" data-bs-target="#shareWalletModal">Поделиться</button>
            <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addExpenseModal">+ Добавить расход</button>
        </div>
    </div>

    <%@ include file="/WEB-INF/jsp/common/alerts.jsp" %>
    <c:if test="${param.share_success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            Вы успешно поделились кошельком!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.share_error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            Ошибка: <c:out value="${param.share_error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty expenses}">
            <table class="table table-striped table-hover" id="expensesTable">
                <thead>
                    <tr>
                        <th>Дата</th>
                        <th>Сумма</th>
                        <th>Категория</th>
                        <th>Описание</th>
                        <th>Кто добавил</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="expense" items="${expenses}">
                        <tr>
                            <td>${expense.formattedDate}</td>
                            <td><fmt:formatNumber value="${expense.amount}" type="currency" currencySymbol="₽"/></td>
                            <td><span class="badge"><c:out value="${expense.categoryName}"/></span></td>
                            <td><c:out value="${expense.description}"/></td>
                            <td><c:out value="${expense.userName}"/></td>
                            <td>
                                <a href="<c:url value="/expenses/edit"><c:param name="id" value="${expense.id}"/></c:url>" class="btn btn-warning btn-sm me-1">Ред.</a>
                                <form method="post" action="<c:url value="/expenses/delete"/>" class="d-inline">
                                    <input type="hidden" name="expenseId" value="${expense.id}">
                                    <input type="hidden" name="walletId" value="${currentWalletId}">
                                    <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Вы уверены?');">Удалить</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">
                Расходы отсутствуют. Добавьте первый расход.
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/jsp/expenses/modals/addExpenseModal.jsp" %>
    <%@ include file="/WEB-INF/jsp/expenses/modals/shareWalletModal.jsp" %>

    <script src="<c:url value="/js/app.js"/>"></script>
</t:main>

