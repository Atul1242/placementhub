<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="card form-narrow">
    <h2>Login</h2>
    <form:form modelAttribute="loginForm" method="post" action="${ctx}/login">
        <div class="form-row">
            <label for="email">Email</label>
            <form:input path="email" id="email" type="email" />
            <form:errors path="email" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="password">Password</label>
            <form:password path="password" id="password" />
            <form:errors path="password" cssClass="field-error" element="span" />
        </div>
        <button type="submit">Login</button>
        <p class="muted">No account? <a href="${ctx}/register">Register here</a>.</p>
    </form:form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
