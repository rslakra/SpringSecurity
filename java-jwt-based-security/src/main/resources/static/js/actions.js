/**
 * JWT API Explorer - Actions
 * Interactive API testing functionality
 */

// Base URL will be set from the template
let baseUrl = window.location.origin;

// Store response data for copy functionality
const responseDataStore = new Map();

/**
 * Initialize the base URL from server-side value
 * @param {string} url - The base URL from the server
 */
function initBaseUrl(url) {
    baseUrl = url;
}

/**
 * Toggle card visibility (currently always shows)
 * @param {HTMLElement} header - The card header element
 */
function toggleCard(header) {
    const body = header.nextElementSibling;
    body.style.display = body.style.display === 'none' ? 'block' : 'block';
}

/**
 * Try an API endpoint and display the response
 * @param {string} endpoint - The API endpoint path
 * @param {string} method - HTTP method (GET, POST)
 * @param {string|null} bodyId - ID of textarea containing request body
 * @param {HTMLElement} btn - The button element
 */
async function tryEndpoint(endpoint, method, bodyId, btn) {
    const responseArea = btn.parentElement.querySelector('.response-area');

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>Loading...';

    try {
        const options = {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        };

        if (bodyId) {
            const textarea = document.getElementById(bodyId);
            options.body = textarea.value;
        }

        const response = await fetch(baseUrl + endpoint, options);
        const data = await response.json();

        displayResponse(responseArea, response.ok, response.status, data);
    } catch (error) {
        displayResponse(responseArea, false, 'Error', { error: error.message });
    } finally {
        btn.disabled = false;
        btn.innerHTML = '▶ Try It';
    }
}

/**
 * Parse a JWT token
 * @param {string} endpoint - The parser endpoint
 * @param {string} inputId - ID of input containing JWT token
 * @param {HTMLElement} btn - The button element
 */
async function parseJwt(endpoint, inputId, btn) {
    const jwt = document.getElementById(inputId).value;
    const responseArea = btn.parentElement.querySelector('.response-area');

    if (!jwt) {
        displayResponse(responseArea, false, 'Error', { error: 'Please enter a JWT token' });
        return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>Parsing...';

    try {
        const response = await fetch(baseUrl + endpoint + '?jwt=' + encodeURIComponent(jwt));
        const data = await response.json();

        displayResponse(responseArea, response.ok, response.status, data);
    } catch (error) {
        displayResponse(responseArea, false, 'Error', { error: error.message });
    } finally {
        btn.disabled = false;
        btn.innerHTML = '▶ Parse Token';
    }
}

/**
 * Generate unique ID for response storage
 */
let responseIdCounter = 0;
function generateResponseId() {
    return 'response-' + (++responseIdCounter);
}

/**
 * Display API response in the response area
 * @param {HTMLElement} container - The response area container
 * @param {boolean} success - Whether the request was successful
 * @param {number|string} status - HTTP status code
 * @param {Object} data - Response data
 */
function displayResponse(container, success, status, data) {
    const statusClass = success ? 'status-success' : 'status-error';
    const borderClass = success ? 'response-success' : 'response-error';
    const jsonString = JSON.stringify(data, null, 2);

    // Store the JSON string for copy functionality
    const responseId = generateResponseId();
    responseDataStore.set(responseId, jsonString);

    container.innerHTML = `
        <div class="code-block ${borderClass} fade-in" style="margin-top: 1rem;">
            <div class="code-block-header">
                <span class="response-status ${statusClass}">Status: ${status}</span>
                <button class="copy-btn" data-response-id="${responseId}" onclick="copyResponseJson(this)">Copy</button>
            </div>
            <pre>${syntaxHighlight(jsonString)}</pre>
        </div>
    `;
}

/**
 * Copy response JSON from stored data
 * @param {HTMLElement} btn - The copy button
 */
function copyResponseJson(btn) {
    const responseId = btn.getAttribute('data-response-id');
    const jsonText = responseDataStore.get(responseId);

    if (jsonText) {
        navigator.clipboard.writeText(jsonText).then(() => {
            showCopySuccess(btn);
        }).catch(err => {
            console.error('Failed to copy:', err);
            showCopyError(btn);
        });
    } else {
        showCopyError(btn);
    }
}

/**
 * Show copy success feedback
 * @param {HTMLElement} btn - The copy button
 */
function showCopySuccess(btn) {
    const original = btn.innerText;
    btn.innerText = 'Copied!';
    btn.style.color = 'var(--accent-green)';
    btn.style.borderColor = 'var(--accent-green)';
    setTimeout(() => {
        btn.innerText = original;
        btn.style.color = '';
        btn.style.borderColor = '';
    }, 1500);
}

/**
 * Show copy error feedback
 * @param {HTMLElement} btn - The copy button
 */
function showCopyError(btn) {
    btn.innerText = 'Failed';
    btn.style.color = 'var(--accent-red)';
    btn.style.borderColor = 'var(--accent-red)';
    setTimeout(() => {
        btn.innerText = 'Copy';
        btn.style.color = '';
        btn.style.borderColor = '';
    }, 1500);
}

/**
 * Syntax highlight JSON string
 * @param {string} json - JSON string to highlight
 * @returns {string} HTML with syntax highlighting
 */
function syntaxHighlight(json) {
    // Escape HTML first
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

    return json.replace(
        /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
        function (match) {
            let cls = 'json-number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'json-key';
                } else {
                    cls = 'json-string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'json-boolean';
            } else if (/null/.test(match)) {
                cls = 'json-null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        }
    );
}

/**
 * Copy text to clipboard (legacy function for backward compatibility)
 * @param {HTMLElement} btn - The copy button
 * @param {string} text - Text to copy
 */
function copyToClipboard(btn, text) {
    navigator.clipboard.writeText(text).then(() => {
        showCopySuccess(btn);
    }).catch(err => {
        console.error('Failed to copy:', err);
        showCopyError(btn);
    });
}

/**
 * Copy token from CSRF result page
 */
function copyToken() {
    const token = document.getElementById('csrf-token').innerText;
    navigator.clipboard.writeText(token).then(() => {
        const btn = event.target;
        showCopySuccess(btn);
    }).catch(err => {
        console.error('Failed to copy:', err);
    });
}
