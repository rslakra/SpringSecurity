<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <jsp:include page="fragments/header.jsp">
            <jsp:param name="pageTitle" value="API Explorer" />
        </jsp:include>

        <jsp:include page="fragments/nav.jsp">
            <jsp:param name="activePage" value="home" />
        </jsp:include>

        <!-- Main Content -->
        <div class="container">
            <!-- Header -->
            <div class="page-header">
                <h1>Spring OAuth Client</h1>
                <p>OAuth 2.0 Authorization Code Flow Demo with Spring Boot 3.5</p>
            </div>

            <!-- OAuth Flow Overview -->
            <div class="card mb-4">
                <div class="card-header">
                    <div class="card-icon">🔄</div>
                    <div>
                        <div class="card-title">OAuth 2.0 Authorization Code Flow</div>
                        <div class="card-subtitle">Secure access to protected resources</div>
                    </div>
                </div>
                <div class="card-body">
                    <div class="flow-diagram">
                        <div class="flow-step">
                            <div class="flow-step-number">1</div>
                            <div>User Request</div>
                        </div>
                        <div class="flow-arrow">→</div>
                        <div class="flow-step">
                            <div class="flow-step-number">2</div>
                            <div>OAuth Server</div>
                        </div>
                        <div class="flow-arrow">→</div>
                        <div class="flow-step">
                            <div class="flow-step-number">3</div>
                            <div>Auth Code</div>
                        </div>
                        <div class="flow-arrow">→</div>
                        <div class="flow-step">
                            <div class="flow-step-number">4</div>
                            <div>Access Token</div>
                        </div>
                        <div class="flow-arrow">→</div>
                        <div class="flow-step">
                            <div class="flow-step-number">5</div>
                            <div>Resources</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- API Endpoints -->
            <h2 class="mb-3" style="color: var(--text-primary);">📡 API Endpoints</h2>

            <div class="grid grid-3">
                <!-- Home Endpoint -->
                <div class="endpoint-card">
                    <span class="endpoint-method method-get">GET</span>
                    <div class="endpoint-path">/</div>
                    <div class="endpoint-desc">API Explorer home page with all available endpoints</div>
                    <a href="<c:url value=" /" />" class="btn btn-primary btn-sm">Visit</a>
                </div>

                <!-- Get Employees (OAuth Form) -->
                <div class="endpoint-card">
                    <span class="endpoint-method method-get">GET</span>
                    <div class="endpoint-path">/getEmployees</div>
                    <div class="endpoint-desc">Initiate OAuth 2.0 authorization flow to access employee data</div>
                    <a href="<c:url value=" /getEmployees" />" class="btn btn-primary btn-sm">Try OAuth Flow</a>
                </div>

                <!-- Show Employees -->
                <div class="endpoint-card">
                    <span class="endpoint-method method-get">GET</span>
                    <div class="endpoint-path">/showEmployees</div>
                    <div class="endpoint-desc">Display employee list (demo data without OAuth)</div>
                    <a href="<c:url value=" /showEmployees" />" class="btn btn-secondary btn-sm">View Demo</a>
                </div>
            </div>

            <!-- Configuration Section -->
            <h2 class="mt-4 mb-3" style="color: var(--text-primary);">⚙️ Configuration</h2>

            <div class="grid grid-2">
                <!-- OAuth Settings -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-icon" style="background: linear-gradient(135deg, var(--secondary), #047857);">
                            🔑</div>
                        <div>
                            <div class="card-title">OAuth Settings</div>
                            <div class="card-subtitle">Authorization server configuration</div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="code-block">
                            <pre>Client ID: ${oauthClientId}
Redirect URI: ${oauthRedirectUri}
Scope: ${oauthScope}
Grant Type: authorization_code</pre>
                        </div>
                    </div>
                </div>

                <!-- Server Info -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-icon" style="background: linear-gradient(135deg, var(--accent), #d97706);">🖥️
                        </div>
                        <div>
                            <div class="card-title">Server Info</div>
                            <div class="card-subtitle">Application settings</div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="code-block">
                            <pre>Client URL: ${clientBaseUrl}
Auth Server: ${authServiceBaseUrl}
Framework: Spring Boot 3.5.7
Java: 21</pre>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Start -->
            <h2 class="mt-4 mb-3" style="color: var(--text-primary);">🚀 Quick Start</h2>

            <div class="card">
                <div class="card-body">
                    <div class="alert alert-info mb-3">
                        <span>💡</span>
                        <span>Without an OAuth server, use the <strong>Demo Data</strong> endpoint to see sample
                            employees.</span>
                    </div>

                    <h4 style="color: var(--text-primary); margin-bottom: 1rem;">cURL Examples</h4>

                    <div class="code-block mb-2">
                        <code># View demo employees (no OAuth required)</code><br>
                        curl ${clientBaseUrl}/showEmployees
                    </div>

                    <div class="code-block">
                        <code># With OAuth authorization code</code><br>
                        curl "${clientBaseUrl}/showEmployees?code=YOUR_AUTH_CODE"
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="fragments/footer.jsp" />