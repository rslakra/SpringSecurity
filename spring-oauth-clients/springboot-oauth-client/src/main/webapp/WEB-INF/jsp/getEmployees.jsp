<%@ page contentType="text/html;charset=UTF-8" language="java" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

			<jsp:include page="fragments/header.jsp">
				<jsp:param name="pageTitle" value="OAuth Authorization" />
			</jsp:include>

			<jsp:include page="fragments/nav.jsp">
				<jsp:param name="activePage" value="oauth" />
			</jsp:include>

			<!-- Main Content -->
			<div class="container">
				<!-- Header -->
				<div class="page-header">
					<h1>🔑 OAuth Authorization</h1>
					<p>Initiate OAuth 2.0 Authorization Code Flow</p>
				</div>

				<!-- OAuth Form Card -->
				<div class="card" style="max-width: 600px; margin: 0 auto;">
					<div class="card-header">
						<div class="card-icon">🚀</div>
						<div>
							<div class="card-title">Request Employee Data</div>
							<div class="card-subtitle">Authorize access to protected resources</div>
						</div>
					</div>
					<div class="card-body">
						<div class="alert alert-info mb-3">
							<span>ℹ️</span>
							<span>This will redirect you to the OAuth authorization server for authentication.</span>
						</div>

						<form:form action="${authServiceBaseUrl}/oauth/authorize" method="post"
							modelAttribute="employee">
							<div class="form-group">
								<label class="form-label">Response Type</label>
								<input type="text" name="response_type" value="code" class="form-input" readonly>
								<div class="form-hint">Authorization code grant type</div>
							</div>

							<div class="form-group">
								<label class="form-label">Client ID</label>
								<input type="text" name="client_id" value="${oauthClientId}" class="form-input"
									readonly>
								<div class="form-hint">OAuth client identifier</div>
							</div>

							<div class="form-group">
								<label class="form-label">Redirect URI</label>
								<input type="text" name="redirect_uri" value="${oauthRedirectUri}" class="form-input"
									readonly>
								<div class="form-hint">Callback URL after authorization</div>
							</div>

							<div class="form-group">
								<label class="form-label">Scope</label>
								<input type="text" name="scope" value="${oauthScope}" class="form-input" readonly>
								<div class="form-hint">Requested permissions</div>
							</div>

							<div class="flex gap-2 mt-3">
								<button type="submit" class="btn btn-primary btn-lg">
									🔐 Authorize & Get Employees
								</button>
								<a href="<c:url value=" /showEmployees" />" class="btn btn-outline">
								View Demo Data
								</a>
							</div>
						</form:form>
					</div>
				</div>

				<!-- Flow Info -->
				<div class="card mt-4" style="max-width: 600px; margin: 2rem auto 0;">
					<div class="card-header">
						<div class="card-icon" style="background: linear-gradient(135deg, var(--secondary), #047857);">
							📋</div>
						<div>
							<div class="card-title">What Happens Next?</div>
							<div class="card-subtitle">OAuth flow steps</div>
						</div>
					</div>
					<div class="card-body">
						<ol style="color: var(--text-secondary); padding-left: 1.5rem;">
							<li class="mb-2">You'll be redirected to the OAuth authorization server at
								<code>${authServiceBaseUrl}</code>
							</li>
							<li class="mb-2">Login with your credentials (if required)</li>
							<li class="mb-2">Grant permission for this app to access employee data</li>
							<li class="mb-2">You'll be redirected back to <code>${oauthRedirectUri}</code></li>
							<li>The app exchanges the code for an access token and fetches employees</li>
						</ol>
					</div>
				</div>
			</div>

			<jsp:include page="fragments/footer.jsp" />