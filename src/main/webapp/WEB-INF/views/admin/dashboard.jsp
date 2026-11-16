<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>Admin dashboard</h2>

<div class="grid grid-3">
    <div class="card stat"><div class="num">${totalCompanies}</div><div class="label">Companies</div></div>
    <div class="card stat"><div class="num">${pendingCount}</div><div class="label">Pending approval</div></div>
    <div class="card stat"><div class="num">${applicantsPerJob.size()}</div><div class="label">Jobs with applicants</div></div>
</div>

<div class="card">
    <h3>Recruiters pending approval</h3>
    <c:choose>
        <c:when test="${empty pending}">
            <p class="muted">No recruiters awaiting approval.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr><th>Company</th><th>Recruiter</th><th>Website</th><th>Action</th></tr>
                <c:forEach var="co" items="${pending}">
                    <tr>
                        <td>${co.name}</td>
                        <td>${co.user.name} (${co.user.email})</td>
                        <td>${co.website}</td>
                        <td>
                            <form class="inline-form" method="post" action="${ctx}/admin/companies/${co.id}/approve">
                                <button type="submit" class="btn-sm">Approve</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h3>All companies</h3>
    <table>
        <tr><th>Company</th><th>Recruiter</th><th>Status</th></tr>
        <c:forEach var="co" items="${companies}">
            <tr>
                <td>${co.name}</td>
                <td>${co.user.name}</td>
                <td>
                    <c:choose>
                        <c:when test="${co.approved}"><span class="badge badge-approved">APPROVED</span></c:when>
                        <c:otherwise><span class="badge badge-pending">PENDING</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<div class="card">
    <h3>Applicants per job</h3>
    <p class="muted">Produced by an HQL <code>GROUP BY</code> aggregate query.</p>
    <c:choose>
        <c:when test="${empty applicantsPerJob}">
            <p class="muted">No applications yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr><th>Job ID</th><th>Applicants</th></tr>
                <c:forEach var="row" items="${applicantsPerJob}">
                    <tr><td>${row[0]}</td><td>${row[1]}</td></tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
