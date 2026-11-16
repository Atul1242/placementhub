<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>Welcome, ${profile.user.name}</h2>

<div class="grid grid-3">
    <div class="card stat"><div class="num">${profile.cgpa}</div><div class="label">CGPA</div></div>
    <div class="card stat"><div class="num">${eligibleJobs.size()}</div><div class="label">Eligible jobs</div></div>
    <div class="card stat"><div class="num">${applications.size()}</div><div class="label">Applications</div></div>
</div>

<div class="card">
    <h3>Jobs you're eligible for</h3>
    <p class="muted">Based on your CGPA (${profile.cgpa}) and open postings from approved companies.</p>
    <c:choose>
        <c:when test="${empty eligibleJobs}">
            <p class="muted">No eligible jobs right now. Check back soon.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="job" items="${eligibleJobs}">
                <div class="job">
                    <h3>${job.title}
                        <span class="badge badge-open">OPEN</span></h3>
                    <div class="meta">
                        <span>${job.company.name}</span>
                        <span>${job.location}</span>
                        <span>Min CGPA: ${job.minCgpa}</span>
                        <span>Deadline: ${job.deadline}</span>
                    </div>
                    <p>${job.description}</p>
                </div>
            </c:forEach>
            <a class="btn btn-outline" href="${ctx}/student/jobs">Browse &amp; apply</a>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
