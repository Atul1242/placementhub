<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>My profile</h2>

<div class="card form-narrow">
    <p class="muted">${profile.user.name} &middot; ${profile.user.email}</p>
    <form:form modelAttribute="profileForm" method="post" action="${ctx}/student/profile">
        <div class="form-row">
            <label for="branch">Branch</label>
            <form:input path="branch" id="branch" />
            <form:errors path="branch" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="cgpa">CGPA</label>
            <form:input path="cgpa" id="cgpa" type="number" step="0.01" min="0" max="10" />
            <form:errors path="cgpa" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="skills">Skills</label>
            <form:input path="skills" id="skills" />
            <form:errors path="skills" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="gradYear">Graduation year</label>
            <form:input path="gradYear" id="gradYear" type="number" />
            <form:errors path="gradYear" cssClass="field-error" element="span" />
        </div>
        <button type="submit">Save profile</button>
    </form:form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
