<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal fade" id="shareWalletModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="post" action="<c:url value="/wallets/share"/>">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <input type="hidden" name="walletId" value="${currentWalletId}">
                <div class="modal-header">
                    <h5 class="modal-title">Поделиться кошельком</h5>
                </div>
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

