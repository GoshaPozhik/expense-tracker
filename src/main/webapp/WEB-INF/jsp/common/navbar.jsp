<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Подключаем современный шрифт из Google Fonts. Добавьте это в <head> вашего главного шаблона --%>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;700&display=swap" rel="stylesheet">


<%-- Убираем классы bg-dark и navbar-dark, чтобы они не мешали нашим стилям. --%>
<nav class="navbar navbar-expand-lg fixed-top custom-navbar-glass">

  <div class="container">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
      <i class="fas fa-wallet"></i> <!-- Добавим иконку (если используете FontAwesome) -->
      FinanceTracker
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
        <c:if test="${not empty sessionScope.user}">
          <li class="nav-item">
                        <span class="navbar-text welcome-text me-3">
                            Hello, ${sessionScope.user.username}!
                        </span>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">Logout</a>
          </li>
        </c:if>

        <c:if test="${empty sessionScope.user}">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
          </li>
          <li class="nav-item">
            <a class="nav-link auth-button" href="${pageContext.request.contextPath}/register">Registration</a>
          </li>
        </c:if>
      </ul>
    </div>
  </div>
</nav>

<%-- Чтобы эффект стекла был виден, под панелью должен быть какой-то контент. --%>
<%-- Добавьте отступ сверху для основного контента страницы, т.к. панель теперь фиксированная. --%>
<style>
  body {
    padding-top: 80px; /* Примерный отступ, подберите под высоту вашей панели */
    background: url('https://source.unsplash.com/random/1920x1080?nature') no-repeat center center/cover; /* Красивый фон для демонстрации */
    min-height: 100vh;
  }
</style>