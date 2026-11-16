<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>Applicants &mdash; ${job.title}</h2>
<p class="muted">${job.company.name} &middot; ${job.location} &middot; Min CGPA ${job.minCgpa}</p>

<div class="card">
    <c:choose>
        <c:when test="${empty applications}">
            <p class="muted">No applicants yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr><th>Student</th><th>Branch</th><th>CGPA</th><th>Skills</th><th>Status</th><th>Actions</th></tr>
                <c:forEach var="app" items="${applications}">
                    <tr>
                        <td>${app.student.user.name}</td>
                        <td>${app.student.branch}</td>
                        <td>${app.student.cgpa}</td>
                        <td>${app.student.skills}</td>
                        <td><span class="badge badge-${app.status.name().toLowerCase()}">${app.status}</span></td>
                        <td class="row-actions">
                            <c:if test="${app.status == 'APPLIED'}">
                                <form class="inline-form" method="post" action="${ctx}/recruiter/applications/${app.id}/status">
                                    <input type="hidden" name="status" value="SHORTLISTED">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <button type="submit" class="btn-sm">Shortlist</button>
                                </form>
                                <form class="inline-form" method="post" action="${ctx}/recruiter/applications/${app.id}/status">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <button type="submit" class="btn-sm btn-danger">Reject</button>
                                </form>
                            </c:if>
                            <c:if test="${app.status == 'SHORTLISTED'}">
                                <form class="inline-form" method="post" action="${ctx}/recruiter/applications/${app.id}/status">
                                    <input type="hidden" name="status" value="SELECTED">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <button type="submit" class="btn-sm">Select</button>
                                </form>
                                <form class="inline-form" method="post" action="${ctx}/recruiter/applications/${app.id}/status">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <button type="submit" class="btn-sm btn-danger">Reject</button>
                                </form>
                            </c:if>
                            <c:if test="${app.status == 'SELECTED' or app.status == 'REJECTED'}">
                                <span class="muted">&mdash;</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
    <p><a class="btn btn-outline" href="${ctx}/recruiter/dashboard">Back to dashboard</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
