<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="hero">
    <h1>Campus placements, organised.</h1>
    <p>PlacementHub connects students, recruiters and placement cells in one place &mdash;
       post jobs, track applications and manage the whole drive end to end.</p>
    <a class="btn" href="${ctx}/register">Get started</a>
    <a class="btn btn-outline" href="${ctx}/login">Login</a>
</div>

<div class="grid grid-3">
    <div class="card stat"><div class="num">Students</div><div class="label">Browse &amp; apply</div></div>
    <div class="card stat"><div class="num">Recruiters</div><div class="label">Post &amp; shortlist</div></div>
    <div class="card stat"><div class="num">Admins</div><div class="label">Approve &amp; oversee</div></div>
</div>

<div class="card">
    <h3>Demo accounts</h3>
    <p class="muted">The database is seeded on first run. Passwords in parentheses.</p>
    <table>
        <tr><th>Role</th><th>Email</th><th>Password</th></tr>
        <tr><td>Admin</td><td>admin@placementhub.test</td><td>admin123</td></tr>
        <tr><td>Student</td><td>student@placementhub.test</td><td>student123</td></tr>
        <tr><td>Recruiter</td><td>priya@acme.test</td><td>recruiter123</td></tr>
    </table>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
