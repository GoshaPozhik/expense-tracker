<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Мои кошельки">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Ваши кошельки</h1>
        <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createWalletModal">
            + Создать кошелек
        </button>
    </div>

    <div class="list-group">
        <c:forEach var="wallet" items="${requestScope.wallets}">
            <a href="${pageContext.request.contextPath}/expenses?walletId=${wallet.id}" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                <div>
                    <h5 class="mb-1">${wallet.name}</h5>
                    <c:if test="${wallet.ownerId == sessionScope.user.id}">
                        <small class="text-muted">Вы владелец</small>
                    </c:if>
                    <c:if test="${wallet.ownerId != sessionScope.user.id}">
                        <small class="text-success">Общий доступ</small>
                    </c:if>
                </div>
                <span class="badge bg-primary rounded-pill">></span>
            </a>
        </c:forEach>
        <c:if test="${empty requestScope.wallets}">
            <p class="text-center text-muted">У вас пока нет кошельков. Создайте первый!</p>
        </c:if>
    </div>

    <!-- Модальное окно для создания кошелька (использование JS от Bootstrap) -->
    <div class="modal fade" id="createWalletModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/wallets/create">
                    <div class="modal-header">
                        <h5 class="modal-title">Создание нового кошелька</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="walletName" class="form-label">Название кошелька</label>
                            <input type="text" class="form-control" id="walletName" name="walletName" required>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Закрыть</button>
                        <button type="submit" class="btn btn-primary">Создать</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</t:main>