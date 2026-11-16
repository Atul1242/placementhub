<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="card form-narrow">
    <h2>Create an account</h2>
    <form:form modelAttribute="registerForm" method="post" action="${ctx}/register">
        <div class="form-row">
            <label for="role">Account type</label>
            <form:select path="role" id="role" onchange="toggleRoleFields()">
                <form:option value="STUDENT" label="Student" />
                <form:option value="RECRUITER" label="Recruiter" />
            </form:select>
            <form:errors path="role" cssClass="field-error" element="span" />
        </div>
        <div class="form-row">
            <label for="name">Full name</label>
            <form:input path="name" id="name" />
            <form:errors path="name" cssClass="field-error" element="span" />
        </div>
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

        <fieldset id="studentFields" style="border:none;padding:0;margin:0;">
            <div class="form-row">
                <label for="branch">Branch</label>
                <form:input path="branch" id="branch" placeholder="e.g. Computer Science" />
            </div>
            <div class="form-row">
                <label for="cgpa">CGPA</label>
                <form:input path="cgpa" id="cgpa" type="number" step="0.01" min="0" max="10" />
                <form:errors path="cgpa" cssClass="field-error" element="span" />
            </div>
            <div class="form-row">
                <label for="skills">Skills</label>
                <form:input path="skills" id="skills" placeholder="e.g. Java, SQL, React" />
            </div>
            <div class="form-row">
                <label for="gradYear">Graduation year</label>
                <form:input path="gradYear" id="gradYear" type="number" />
            </div>
        </fieldset>

        <fieldset id="recruiterFields" style="border:none;padding:0;margin:0;display:none;">
            <div class="form-row">
                <label for="companyName">Company name</label>
                <form:input path="companyName" id="companyName" />
                <form:errors path="companyName" cssClass="field-error" element="span" />
            </div>
            <div class="form-row">
                <label for="website">Company website</label>
                <form:input path="website" id="website" placeholder="https://..." />
            </div>
        </fieldset>

        <button type="submit">Register</button>
        <p class="muted">Already have an account? <a href="${ctx}/login">Login</a>.</p>
    </form:form>
</div>

<script>
    function toggleRoleFields() {
        var role = document.getElementById('role').value;
        document.getElementById('studentFields').style.display = (role === 'STUDENT') ? 'block' : 'none';
        document.getElementById('recruiterFields').style.display = (role === 'RECRUITER') ? 'block' : 'none';
    }
    toggleRoleFields();
</script>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
