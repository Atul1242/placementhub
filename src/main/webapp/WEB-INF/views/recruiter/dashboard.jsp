<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>${company.name}
    <c:choose>
        <c:when test="${company.approved}"><span class="badge badge-approved">APPROVED</span></c:when>
        <c:otherwise><span class="badge badge-pending">PENDING APPROVAL</span></c:otherwise>
    </c:choose>
</h2>

<c:if test="${not company.approved}">
    <div class="alert alert-info">Your company is awaiting admin approval. You can post jobs once approved.</div>
</c:if>

<div class="card">
    <div style="display:flex;justify-content:space-between;align-items:center;">
        <h3 style="margin:0;">Your job postings</h3>
        <a class="btn-sm" href="${ctx}/recruiter/jobs/new">+ Post a job</a>
    </div>
    <c:choose>
        <c:when test="${empty jobs}">
            <p class="muted">No jobs posted yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr><th>Title</th><th>Location</th><th>Min CGPA</th><th>Deadline</th><th>Status</th><th>Actions</th></tr>
                <c:forEach var="job" items="${jobs}">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.location}</td>
                        <td>${job.minCgpa}</td>
                        <td>${job.deadline}</td>
                        <td>
                            <c:choose>
                                <c:when test="${job.status == 'OPEN'}"><span class="badge badge-open">OPEN</span></c:when>
                                <c:otherwise><span class="badge badge-closed">CLOSED</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="row-actions">
                            <a class="btn-sm btn-outline" href="${ctx}/recruiter/jobs/${job.id}/applicants">Applicants</a>
                            <a class="btn-sm btn-outline" href="${ctx}/recruiter/jobs/${job.id}/edit">Edit</a>
                            <c:if test="${job.status == 'OPEN'}">
                                <form class="inline-form" method="post" action="${ctx}/recruiter/jobs/${job.id}/close">
                                    <button type="submit" class="btn-sm btn-danger">Close</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
