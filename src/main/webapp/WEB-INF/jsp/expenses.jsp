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

    <%-- Обработка уведомлений об успехе/ошибке шаринга --%>
    <c:if test="${param.share_success}">
        <div class="alert alert-success">Вы успешно поделились кошельком!</div>
    </c:if>
    <c:if test="${not empty param.share_error}">
        <div class="alert alert-danger">Ошибка: ${param.share_error}</div>
    </c:if>

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
        <c:forEach var="expense" items="${requestScope.expenses}">
            <tr>
                    <%-- ИСПРАВЛЕННАЯ СТРОКА - вариант 1 (простой) --%>
                <td>
                    <c:if test="${not empty expense.expenseDate}">
                        ${expense.expenseDate.toLocalDate()} ${expense.expenseDate.toLocalTime()}
                    </c:if>
                </td>

                    <%-- ИЛИ вариант 2 (если добавили метод в DTO) --%>
                    <%-- <td>${expense.formattedDate}</td> --%>

                <td><fmt:formatNumber value="${expense.amount}" type="currency" currencySymbol="₽"/></td>
                <td><span class="badge bg-secondary">${expense.categoryName}</span></td>
                <td>${expense.description}</td>
                <td>${expense.userName}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/expenses/edit?id=${expense.id}" class="btn btn-warning btn-sm me-1">
                        Ред.
                    </a>
                    <form method="post" action="${pageContext.request.contextPath}/expenses/delete" style="display:inline;">
                        <input type="hidden" name="expenseId" value="${expense.id}">
                        <input type="hidden" name="walletId" value="${requestScope.currentWalletId}">
                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Вы уверены?');">Удалить</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <%-- Модальные окна остаются без изменений --%>
    <div class="modal fade" id="addExpenseModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/expenses/add" id="addExpenseForm">
                    <input type="hidden" name="walletId" value="${requestScope.currentWalletId}">
                    <div class="modal-header"><h5 class="modal-title">Новый расход</h5></div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="amount" class="form-label">Сумма</label>
                            <input type="number" step="0.01" class="form-control" id="amount" name="amount" required>
                        </div>
                        <div class="mb-3">
                            <label for="categoryId" class="form-label">Категория</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <c:forEach var="category" items="${requestScope.categories}">
                                    <option value="${category.id}">${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="description" class="form-label">Описание</label>
                            <input type="text" class="form-control" id="description" name="description">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Добавить</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="modal fade" id="shareWalletModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/wallets/share">
                    <input type="hidden" name="walletId" value="${requestScope.currentWalletId}">
                    <div class="modal-header"><h5 class="modal-title">Поделиться кошельком</h5></div>
                    <div class="modal-body">
                        <p>Введите email пользователя, с которым хотите поделиться доступом к этому кошельку.</p>
                        <input type="email" name="emailToShare" class="form-control" placeholder="user@example.com" required>
                    </div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary">Поделиться</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</t:main>
<script src="${pageContext.request.contextPath}/js/app.js"></script>