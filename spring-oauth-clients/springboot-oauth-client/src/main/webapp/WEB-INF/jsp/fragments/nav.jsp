<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!-- Navigation -->
        <nav class="nav-bar">
            <div class="nav-content">
                <a href="<c:url value=" /" />" class="nav-brand">
                🔐 OAuth Client
                </a>
                <ul class="nav-links">
                    <li><a href="<c:url value=" /" />" class="${param.activePage == 'home' ? 'active' : ''}">Home</a>
                    </li>
                    <li><a href="<c:url value=" /getEmployees" />" class="${param.activePage == 'oauth' ? 'active' :
                        ''}">OAuth Flow</a></li>
                    <li><a href="<c:url value=" /showEmployees" />" class="${param.activePage == 'demo' ? 'active' :
                        ''}">Demo Data</a></li>
                </ul>
            </div>
        </nav>