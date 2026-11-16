<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>My applications</h2>

<div class="card">
    <c:choose>
        <c:when test="${empty applications}">
            <p class="muted">You haven't applied to any jobs yet.
                <a href="${ctx}/student/jobs">Browse jobs</a>.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr><th>Job</th><th>Company</th><th>Applied</th><th>Status</th></tr>
                <c:forEach var="app" items="${applications}">
                    <tr>
                        <td>${app.job.title}</td>
                        <td>${app.job.company.name}</td>
                        <td>${app.appliedAt.toLocalDate()}</td>
                        <td><span class="badge badge-${app.status.name().toLowerCase()}">${app.status}</span></td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
