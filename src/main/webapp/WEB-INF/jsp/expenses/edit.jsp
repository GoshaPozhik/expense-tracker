<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:main title="Редактирование расхода">
  <div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
      <h2>Редактировать расход</h2>
      <div class="auth-card">
        <form method="post" action="${pageContext.request.contextPath}/expenses/edit">
            <%-- Скрытые поля для отправки ID --%>
          <input type="hidden" name="expenseId" value="${expense.id}">
          <input type="hidden" name="walletId" value="${expense.walletId}">

          <div class="mb-3">
            <label for="amount" class="form-label">Сумма</label>
            <input type="number" step="0.01" class="form-control" id="amount" name="amount" value="${expense.amount}" required>
          </div>

          <div class="mb-3">
            <label for="categoryId" class="form-label">Категория</label>
            <select class="form-select" id="categoryId" name="categoryId" required>
              <c:forEach var="category" items="${requestScope.categories}">
                <option value="${category.id}" ${category.id == expense.categoryId ? 'selected' : ''}>
                    ${category.name}
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="mb-3">
            <label for="description" class="form-label">Описание</label>
            <input type="text" class="form-control" id="description" name="description" value="${expense.description}">
          </div>

          <div class="d-flex justify-content-end">
            <a href="${pageContext.request.contextPath}/expenses?walletId=${expense.walletId}" class="btn btn-secondary me-2">Отмена</a>
            <button type="submit" class="btn btn-primary">Сохранить изменения</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</t:main>