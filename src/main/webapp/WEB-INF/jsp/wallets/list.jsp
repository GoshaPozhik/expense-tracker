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
                    <a href="<c:url value="/expenses"><c:param name="walletId" value="${wallet.id}"/></c:url>" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                        <div>
                            <h5 class="mb-1">${wallet.name}</h5>
                            <c:choose>
                                <c:when test="${wallet.ownerId == sessionScope.user.id}">
                                    <small class="text-muted">Вы владелец</small>
                                </c:when>
                                <c:otherwise>
                                    <small class="text-success">Общий доступ</small>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <span class="badge">></span>
                    </a>
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

