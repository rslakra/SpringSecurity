<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ page session="false" %>

            <jsp:include page="fragments/header.jsp">
                <jsp:param name="pageTitle" value="Employee List" />
            </jsp:include>

            <jsp:include page="fragments/nav.jsp">
                <jsp:param name="activePage" value="demo" />
            </jsp:include>

            <!-- Main Content -->
            <div class="container">
                <!-- Header -->
                <div class="page-header">
                    <h1>👥 Employee List</h1>
                    <p>Retrieved from protected resource</p>
                </div>

                <!-- Status Badge -->
                <div class="text-center mb-4">
                    <c:choose>
                        <c:when test="${not empty param.code}">
                            <span class="badge badge-success">✓ Authenticated via OAuth</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-warning">Demo Mode - Sample Data</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Employee Table Card -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-icon" style="background: linear-gradient(135deg, var(--secondary), #047857);">
                            📊</div>
                        <div>
                            <div class="card-title">Employees</div>
                            <div class="card-subtitle">
                                <c:choose>
                                    <c:when test="${not empty employees}">
                                        ${employees.size()} employee(s) found
                                    </c:when>
                                    <c:otherwise>
                                        No employees found
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty employees}">
                                <div class="table-container">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Employee ID</th>
                                                <th>Name</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${employees}" var="employee" varStatus="status">
                                                <tr>
                                                    <td>${status.index + 1}</td>
                                                    <td><code>${employee.empId}</code></td>
                                                    <td>${employee.empName}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-warning">
                                    <span>⚠️</span>
                                    <span>No employees to display. Try the OAuth flow to fetch real data.</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Actions -->
                <div class="flex flex-center gap-2 mt-4">
                    <a href="<c:url value=" /getEmployees" />" class="btn btn-primary">
                    🔐 Try OAuth Flow
                    </a>
                    <a href="<c:url value=" /showEmployees" />" class="btn btn-secondary">
                    🔄 Refresh Demo Data
                    </a>
                    <a href="<c:url value=" /" />" class="btn btn-outline">
                    ← Back to Home
                    </a>
                </div>

                <!-- API Response Info -->
                <div class="card mt-4">
                    <div class="card-header">
                        <div class="card-icon" style="background: linear-gradient(135deg, var(--accent), #d97706);">📡
                        </div>
                        <div>
                            <div class="card-title">API Response</div>
                            <div class="card-subtitle">JSON representation</div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="code-block">
                            <pre>[
<c:forEach items="${employees}" var="employee" varStatus="status">
  {
    "empId": "${employee.empId}",
    "empName": "${employee.empName}"
  }<c:if test="${!status.last}">,</c:if>
</c:forEach>
]</pre>
                        </div>
                    </div>
                </div>
            </div>

            <jsp:include page="fragments/footer.jsp" />