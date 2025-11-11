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

    <%@ include file="/WEB-INF/jsp/common/alerts.jsp" %>

    <c:choose>
        <c:when test="${not empty wallets}">
            <div class="list-group">
                <c:forEach var="wallet" items="${wallets}">
                    <div class="list-group-item d-flex justify-content-between align-items-center">
                        <div class="flex-grow-1">
                            <a href="<c:url value="/expenses"><c:param name="walletId" value="${wallet.id}"/></c:url>" 
                               class="text-decoration-none text-dark">
                                <h5 class="mb-1"><c:out value="${wallet.name}"/></h5>
                            </a>
                            <c:choose>
                                <c:when test="${wallet.ownerId == sessionScope.user.id}">
                                    <small class="text-muted">Вы владелец</small>
                                </c:when>
                                <c:otherwise>
                                    <small class="text-success">Общий доступ</small>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <c:if test="${wallet.ownerId == sessionScope.user.id}">
                                <a href="<c:url value="/wallets/edit"><c:param name="id" value="${wallet.id}"/></c:url>" 
                                   class="btn btn-warning btn-sm">Ред.</a>
                                <form method="post" action="<c:url value="/wallets/delete"/>" class="d-inline">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="walletId" value="${wallet.id}">
                                    <button type="submit" class="btn btn-danger btn-sm" 
                                            onclick="return confirm('Вы уверены, что хотите удалить кошелек? Все расходы в этом кошельке будут удалены.');">Удалить</button>
                                </form>
                            </c:if>
                            <a href="<c:url value="/expenses"><c:param name="walletId" value="${wallet.id}"/></c:url>" 
                               class="btn btn-primary btn-sm">Открыть</a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">
                У вас пока нет кошельков. Создайте первый!
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="/WEB-INF/jsp/wallets/modals/createWalletModal.jsp" %>
</t:main>

