<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main title="Вход">
  <div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
      <h2 class="text-center mb-4">Вход в систему</h2>

        <%-- Показываем ошибку, если она есть в запросе --%>
      <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger" role="alert">
            ${requestScope.error}
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input type="email" class="form-control" id="email" name="email" required>
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">Пароль</label>
          <input type="password" class="form-control" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">Войти</button>
      </form>
      <div class="text-center mt-3">
        <p>Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a></p>
      </div>
    </div>
  </div>
</t:main>