<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<h2>Browse jobs</h2>

<div class="card">
    <form method="get" action="${ctx}/student/jobs" class="search-bar">
        <div>
            <label for="keyword">Keyword</label>
            <input type="text" id="keyword" name="keyword" value="${keyword}" placeholder="title or description">
        </div>
        <div>
            <label for="location">Location</label>
            <input type="text" id="location" name="location" value="${location}" placeholder="e.g. Remote">
        </div>
        <div>
            <label for="minCgpa">Max required CGPA</label>
            <input type="number" id="minCgpa" name="minCgpa" value="${minCgpa}" step="0.01" min="0" max="10">
        </div>
        <div><button type="submit">Search</button></div>
    </form>
</div>

<div class="card">
    <p class="muted">${result.totalItems} job(s) found.</p>
    <c:choose>
        <c:when test="${empty result.items}">
            <p class="muted">No jobs match your filters.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="job" items="${result.items}">
                <div class="job">
                    <h3>${job.title} <span class="badge badge-open">OPEN</span></h3>
                    <div class="meta">
                        <span>${job.company.name}</span>
                        <span>${job.location}</span>
                        <span>Min CGPA: ${job.minCgpa}</span>
                        <span>Deadline: ${job.deadline}</span>
                    </div>
                    <p>${job.description}</p>
                    <form class="inline-form" method="post" action="${ctx}/student/jobs/${job.id}/apply">
                        <button type="submit">Apply</button>
                    </form>
                </div>
            </c:forEach>

            <div class="pagination">
                <c:if test="${result.hasPrevious()}">
                    <a class="btn-sm btn-outline"
                       href="${ctx}/student/jobs?page=${result.page - 1}&keyword=${keyword}&location=${location}&minCgpa=${minCgpa}">Prev</a>
                </c:if>
                <span class="current">Page ${result.page} of ${result.totalPages}</span>
                <c:if test="${result.hasNext()}">
                    <a class="btn-sm btn-outline"
                       href="${ctx}/student/jobs?page=${result.page + 1}&keyword=${keyword}&location=${location}&minCgpa=${minCgpa}">Next</a>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
