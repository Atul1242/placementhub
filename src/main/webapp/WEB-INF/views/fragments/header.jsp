<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.currentUser}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PlacementHub</title>
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="${ctx}/">Placement<span>Hub</span></a>
    <nav class="nav">
        <c:choose>
            <c:when test="${empty user}">
                <a href="${ctx}/login">Login</a>
                <a class="btn-sm" href="${ctx}/register">Register</a>
            </c:when>
            <c:otherwise>
                <c:if test="${user.role == 'STUDENT'}">
                    <a href="${ctx}/student/dashboard">Dashboard</a>
                    <a href="${ctx}/student/jobs">Browse Jobs</a>
                    <a href="${ctx}/student/applications">My Applications</a>
                    <a href="${ctx}/student/profile">Profile</a>
                </c:if>
                <c:if test="${user.role == 'RECRUITER'}">
                    <a href="${ctx}/recruiter/dashboard">Dashboard</a>
                    <a href="${ctx}/recruiter/jobs/new">Post Job</a>
                </c:if>
                <c:if test="${user.role == 'ADMIN'}">
                    <a href="${ctx}/admin/dashboard">Admin</a>
                </c:if>
                <span class="who">${user.name} &middot; ${user.role}</span>
                <a class="btn-sm" href="${ctx}/logout">Logout</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main class="container">
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>
    <c:if test="${not empty info}">
        <div class="alert alert-info">${info}</div>
    </c:if>
