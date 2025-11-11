<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

<nav class="navbar navbar-expand-lg fixed-top">
    <div class="container">
        <a class="navbar-brand" href="<c:url value="/home"/>">
            FinanceTracker
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
                <c:if test="${not empty sessionScope.user}">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/home"/>">Wallets</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/categories"/>">Categories</a>
                    </li>
                    <li class="nav-item">
                        <span class="nav-link">
                            Hello, <c:out value="${sessionScope.user.username}"/>!
                        </span>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/logout"/>">Logout</a>
                    </li>
                </c:if>
                <c:if test="${empty sessionScope.user}">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/login"/>">Login</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="/register"/>">Registration</a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>
