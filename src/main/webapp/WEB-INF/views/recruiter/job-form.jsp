<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<c:set var="action" value="${mode == 'edit' ? ctx.concat('/recruiter/jobs/').concat(jobForm.id) : ctx.concat('/recruiter/jobs')}" />

<h2>${mode == 'edit' ? 'Edit job' : 'Post a new job'}</h2>

<div class="card form-narrow">
    <form:form modelAttribute="jobForm" method="post" action="${action}">
        <form:hidden path="id" />
        <div class="form-row">
            <label for="title">Title</label>
            <form:input path="title" id="title" />
            <form:errors path="title" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="description">Description</label>
            <form:textarea path="description" id="description" />
            <form:errors path="description" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="minCgpa">Minimum CGPA</label>
            <form:input path="minCgpa" id="minCgpa" type="number" step="0.01" min="0" max="10" />
            <form:errors path="minCgpa" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="location">Location</label>
            <form:input path="location" id="location" />
            <form:errors path="location" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="deadline">Deadline</label>
            <form:input path="deadline" id="deadline" type="date" />
            <form:errors path="deadline" cssClass="field-error" element="span" />
        </div>
        <button type="submit">${mode == 'edit' ? 'Save changes' : 'Post job'}</button>
        <a class="btn btn-muted" href="${ctx}/recruiter/dashboard">Cancel</a>
    </form:form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
