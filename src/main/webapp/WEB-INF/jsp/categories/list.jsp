<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Категории расходов">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Категории расходов</h1>
        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
            + Добавить категорию
        </button>
    </div>

    <%@ include file="/WEB-INF/jsp/common/alerts.jsp" %>

    <div class="row">
        <div class="col-md-6 mb-4">
            <div class="card">
                <div class="card-header bg-transparent text-grey">
                    <h5 class="mb-0">Глобальные категории</h5>
                    <small>Доступны всем пользователям, редактирование недоступно</small>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty globalCategories}">
                            <ul class="list-group list-group-flush">
                                <c:forEach var="category" items="${globalCategories}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><c:out value="${category.name}"/></span>
                                        <span class="badge bg-info">Глобальная</span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Глобальные категории отсутствуют</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="col-md-6 mb-4">
            <div class="card">
                <div class="card-header bg-transparent text-grey">
                    <h5 class="mb-0">Мои категории</h5>
                    <small>Ваши персональные категории</small>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty userCategories}">
                            <ul class="list-group list-group-flush">
                                <c:forEach var="category" items="${userCategories}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><c:out value="${category.name}"/></span>
                                        <div>
                                            <a href="<c:url value="/categories/edit"><c:param name="id" value="${category.id}"/></c:url>" 
                                               class="btn btn-warning btn-sm me-1">Ред.</a>
                                            <form method="post" action="<c:url value="/categories/delete"/>" class="d-inline">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                <input type="hidden" name="categoryId" value="${category.id}">
                                                <button type="submit" class="btn btn-danger btn-sm" 
                                                        onclick="return confirm('Вы уверены, что хотите удалить категорию?');">Удалить</button>
                                            </form>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">У вас пока нет персональных категорий. Создайте первую!</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/jsp/categories/modals/addCategoryModal.jsp" %>
</t:main>

