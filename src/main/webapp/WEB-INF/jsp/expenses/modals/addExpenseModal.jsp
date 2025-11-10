<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="addExpenseModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="<c:url value="/expenses/add"/>" id="addExpenseForm">
                <input type="hidden" name="walletId" value="${currentWalletId}">
                <div class="modal-header">
                    <h5 class="modal-title">Новый расход</h5>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="amount" class="form-label">Сумма</label>
                        <input type="number" step="0.01" class="form-control" id="amount" name="amount" required>
                    </div>
                    <div class="mb-3">
                        <label for="categoryId" class="form-label">Категория</label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.id}">${category.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Описание</label>
                        <input type="text" class="form-control" id="description" name="description" maxlength="255">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Добавить</button>
                </div>
            </form>
        </div>
    </div>
</div>

