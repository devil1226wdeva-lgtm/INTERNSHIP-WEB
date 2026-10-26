/**
 * Student Internship Tracker - Frontend API Client & Dynamic UI Integration
 * Apple-inspired Liquid Glass Dashboard System
 */

const API_BASE = ''; // Same-origin API (Netlify Functions)

// Current page name without extension, e.g. "/dashboard" or "/dashboard.html" -> "dashboard".
// "/" maps to "login" since the login page doubles as the landing page.
function currentPage() {
    const last = window.location.pathname.toLowerCase().replace(/\/+$/, '').split('/').pop() || '';
    return last.replace(/\.html$/, '') || 'login';
}

function isAuthPage() {
    const page = currentPage();
    return page === 'login' || page === 'register';
}

// Utility: Toast notification with glass styling
function showToast(message, type = 'success') {
    let toast = document.getElementById('app-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'app-toast';
        toast.style.position = 'fixed';
        toast.style.top = '24px';
        toast.style.right = '24px';
        toast.style.padding = '14px 24px';
        toast.style.borderRadius = '16px';
        toast.style.color = '#fff';
        toast.style.fontSize = '14px';
        toast.style.fontWeight = '600';
        toast.style.zIndex = '99999';
        toast.style.backdropFilter = 'blur(20px)';
        toast.style.webkitBackdropFilter = 'blur(20px)';
        toast.style.border = '1px solid rgba(255, 255, 255, 0.25)';
        toast.style.boxShadow = '0 12px 30px rgba(0,0,0,0.35)';
        toast.style.transition = 'all 0.3s cubic-bezier(0.16, 1, 0.3, 1)';
        document.body.appendChild(toast);
    }

    if (type === 'success') {
        toast.style.background = 'rgba(16, 185, 129, 0.85)';
    } else if (type === 'error') {
        toast.style.background = 'rgba(239, 68, 68, 0.85)';
    } else {
        toast.style.background = 'rgba(37, 99, 235, 0.85)';
    }

    toast.textContent = message;
    toast.style.opacity = '1';
    toast.style.transform = 'translateY(0) scale(1)';

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-12px) scale(0.95)';
    }, 3500);
}

// Utility: Standard Fetch with session credentials
async function apiRequest(endpoint, options = {}) {
    options.credentials = 'same-origin';
    options.headers = Object.assign({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }, options.headers || {});

    try {
        const response = await fetch(API_BASE + endpoint, options);
        const data = await response.json().catch(() => null);

        if (!response.ok) {
            if (response.status === 401) {
                // If unauthenticated on a protected page, redirect to login
                if (!isAuthPage()) {
                    window.location.href = 'login.html';
                }
            }
            const errorMsg = (data && data.message) ? data.message : `Request failed with status ${response.status}`;
            throw new Error(errorMsg);
        }
        return data;
    } catch (err) {
        throw err;
    }
}

// Navigation & Auth initialization
async function initAuth() {
    const onAuthPage = isAuthPage();

    // Handle logout link clicks
    document.querySelectorAll('.logout-link, a').forEach(link => {
        if (link.textContent.trim().toLowerCase() === 'logout' || link.getAttribute('href') === 'login.html') {
            link.addEventListener('click', async (e) => {
                if (link.textContent.trim().toLowerCase() === 'logout' || link.classList.contains('logout-link')) {
                    e.preventDefault();
                    try {
                        await fetch('/api/auth/logout', { method: 'POST', credentials: 'same-origin' });
                    } catch (err) {}
                    window.location.href = 'login.html';
                }
            });
        }
    });

    try {
        const res = await apiRequest('/api/auth/me');
        if (res && res.success && res.data) {
            const user = res.data;
            const displayName = user.name || user.username || 'Student';

            // If user is already logged in and visits login or register, redirect to dashboard
            if (onAuthPage) {
                window.location.href = 'dashboard.html';
                return;
            }

            // Update top header user meta
            const headerUserName = document.getElementById('headerUserName');
            if (headerUserName) {
                headerUserName.textContent = displayName;
            }

            const headerAvatar = document.getElementById('headerAvatar');
            if (headerAvatar) {
                const parts = displayName.split(' ').filter(Boolean);
                let initials = 'ST';
                if (parts.length >= 2) {
                    initials = (parts[0][0] + parts[1][0]).toUpperCase();
                } else if (parts.length === 1) {
                    initials = parts[0].substring(0, 2).toUpperCase();
                }
                headerAvatar.textContent = initials;
            }

            // Update Dashboard Greeting
            const welcomeGreeting = document.getElementById('welcomeGreeting');
            if (welcomeGreeting) {
                welcomeGreeting.textContent = `Good Morning, ${displayName}! ??`;
            }

            // Fallback for legacy selector
            const welcomeHeader = document.querySelector('.welcome h2');
            if (welcomeHeader) {
                welcomeHeader.textContent = `Welcome, ${displayName}! ??`;
            }
        }
    } catch (err) {
        // Not authenticated
        if (!onAuthPage) {
            window.location.href = 'login.html';
        }
    }
}

// -------------------------------------------------------------
// Page: Login
// -------------------------------------------------------------
function initLoginPage() {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');

        const btn = form.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.textContent = 'Logging in...';
        btn.disabled = true;

        try {
            const res = await apiRequest('/api/auth/login', {
                method: 'POST',
                body: JSON.stringify({
                    username: usernameInput.value.trim(),
                    password: passwordInput.value
                })
            });
            if (res && res.success) {
                showToast('Login successful! Welcome back.', 'success');
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 400);
            }
        } catch (err) {
            showToast(err.message || 'Invalid username or password', 'error');
            btn.textContent = originalText;
            btn.disabled = false;
        }
    });
}

// -------------------------------------------------------------
// Page: Register
// -------------------------------------------------------------
function initRegisterPage() {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const name = document.getElementById('name') ? document.getElementById('name').value.trim() : '';
        const studentId = document.getElementById('studentId') ? document.getElementById('studentId').value.trim() : '';

        const btn = form.querySelector('button[type="submit"]');
        btn.textContent = 'Creating Account...';
        btn.disabled = true;

        try {
            const res = await apiRequest('/api/auth/register', {
                method: 'POST',
                body: JSON.stringify({ username, email, password, name, studentId })
            });
            if (res && res.success) {
                showToast('Registration successful! Redirecting to login...', 'success');
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1000);
            }
        } catch (err) {
            showToast(err.message || 'Registration failed', 'error');
            btn.textContent = 'Register Account';
            btn.disabled = false;
        }
    });
}

// -------------------------------------------------------------
// Page: Dashboard
// -------------------------------------------------------------
async function initDashboardPage() {
    try {
        // 1. Load Stats
        const statsRes = await apiRequest('/api/dashboard/stats');
        if (statsRes && statsRes.data) {
            const s = statsRes.data;

            // Liquid Glass stat card bindings
            const elTotal = document.getElementById('statTotal');
            if (elTotal) elTotal.textContent = s.total ?? 0;

            const elInterviews = document.getElementById('statInterviews');
            if (elInterviews) elInterviews.textContent = s.interviews ?? 0;

            const elSelected = document.getElementById('statSelected');
            if (elSelected) elSelected.textContent = s.selected ?? 0;

            const elRejected = document.getElementById('statRejected');
            if (elRejected) elRejected.textContent = s.rejected ?? 0;

            // Donut chart counts
            const donutTotal = document.getElementById('donutTotalCount');
            if (donutTotal) donutTotal.textContent = s.total ?? 0;

            const legApplied = document.getElementById('legendApplied');
            if (legApplied) legApplied.textContent = s.applications ?? 0;

            const legInterview = document.getElementById('legendInterview');
            if (legInterview) legInterview.textContent = s.interviews ?? 0;

            const legSelected = document.getElementById('legendSelected');
            if (legSelected) legSelected.textContent = s.selected ?? 0;

            const legRejected = document.getElementById('legendRejected');
            if (legRejected) legRejected.textContent = s.rejected ?? 0;

            const legCompleted = document.getElementById('legendCompleted');
            if (legCompleted) legCompleted.textContent = s.completed ?? 0;

            // Legacy card elements support
            const numberCards = document.querySelectorAll('.card .number');
            if (numberCards.length >= 4) {
                numberCards[0].textContent = s.total || 0;
                numberCards[1].textContent = s.applications || 0;
                numberCards[2].textContent = s.interviews || 0;
                numberCards[3].textContent = s.selected || 0;
            }
        }

        // 2. Load Recent Internships
        const recentRes = await apiRequest('/api/dashboard/recent');
        const tbody = document.getElementById('recentInternshipsBody') || document.querySelector('table tbody');

        if (tbody && recentRes && recentRes.data) {
            tbody.innerHTML = '';
            if (recentRes.data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; padding: 32px; color: var(--text-muted); font-size: 13.5px;">No internships tracked yet. Click "+ Add Internship" above to start!</td></tr>`;
            } else {
                recentRes.data.forEach(item => {
                    const statusClass = (item.status || 'Applied').toLowerCase();
                    const dateStr = item.appliedDate ? formatDateShort(item.appliedDate) : 'Recently';
                    const compLogoText = (item.company || 'Co').substring(0, 4).toLowerCase();

                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>
                            <div class="company-cell">
                                <div class="company-logo">${escapeHtml(compLogoText)}</div>
                                <span>${escapeHtml(item.company)}</span>
                            </div>
                        </td>
                        <td>${escapeHtml(item.role)}</td>
                        <td><span class="type-pill type-remote">${escapeHtml(item.location || 'Remote')}</span></td>
                        <td><span class="status-pill status-${statusClass}">${escapeHtml(item.status)}</span></td>
                        <td>${dateStr}</td>
                        <td style="text-align: right;">
                            <a href="add-internship.html?id=${item.id}" style="color: var(--accent-blue); text-decoration: none; font-size: 12.5px; font-weight: 600;">Edit</a>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        }
    } catch (err) {
        console.error('Failed to load dashboard data:', err);
    }
}

// -------------------------------------------------------------
// Page: Internships List
// -------------------------------------------------------------
async function initInternshipsPage() {
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');

    if (searchInput) {
        searchInput.addEventListener('input', debounce(loadInternships, 300));
    }
    if (statusFilter) {
        statusFilter.addEventListener('change', loadInternships);
    }

    await loadInternships();
}

async function loadInternships() {
    const tbody = document.getElementById('internshipsTableBody') || document.querySelector('table tbody');
    if (!tbody) return;

    const search = document.getElementById('searchInput') ? document.getElementById('searchInput').value.trim() : '';
    const status = document.getElementById('statusFilter') ? document.getElementById('statusFilter').value : 'ALL';

    let url = `/api/internships?`;
    if (search) url += `search=${encodeURIComponent(search)}&`;
    if (status && status !== 'ALL') url += `status=${encodeURIComponent(status)}&`;

    try {
        const res = await apiRequest(url);
        tbody.innerHTML = '';
        if (!res || !res.data || res.data.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; padding: 40px; color: var(--text-muted); font-size: 14px;">No internships found matching your criteria.</td></tr>`;
            return;
        }

        res.data.forEach(item => {
            const statusClass = (item.status || 'Applied').toLowerCase();
            const dateStr = item.appliedDate ? formatDate(item.appliedDate) : '-';
            const compLogo = (item.company || 'Co').substring(0, 4).toLowerCase();

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div class="company-cell">
                        <div class="company-logo">${escapeHtml(compLogo)}</div>
                        <span style="font-weight: 600; color: #fff;">${escapeHtml(item.company)}</span>
                    </div>
                </td>
                <td>${escapeHtml(item.role)}</td>
                <td>${escapeHtml(item.location || '-')}</td>
                <td><span class="status-pill status-${statusClass}">${escapeHtml(item.status)}</span></td>
                <td>${dateStr}</td>
                <td style="text-align: right;">
                    <a href="add-internship.html?id=${item.id}" style="color: #60a5fa; margin-right: 14px; text-decoration: none; font-weight: 600; font-size: 13px;">Edit</a>
                    <a href="javascript:void(0)" onclick="deleteInternshipItem(${item.id})" style="color: #f87171; text-decoration: none; font-weight: 600; font-size: 13px;">Delete</a>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showToast(err.message || 'Failed to load internships', 'error');
    }
}

async function deleteInternshipItem(id) {
    if (!confirm('Are you sure you want to delete this internship entry?')) return;
    try {
        await apiRequest(`/api/internships/${id}`, { method: 'DELETE' });
        showToast('Internship deleted successfully', 'success');
        loadInternships();
    } catch (err) {
        showToast(err.message || 'Failed to delete internship', 'error');
    }
}

// -------------------------------------------------------------
// Page: Add / Edit Internship
// -------------------------------------------------------------
async function initAddInternshipPage() {
    const form = document.querySelector('form');
    if (!form) return;

    const urlParams = new URLSearchParams(window.location.search);
    const internshipId = urlParams.get('id');

    const heading = document.getElementById('pageFormTitle') || document.querySelector('.page-header h2');
    const submitBtn = form.querySelector('button[type="submit"]');

    if (internshipId) {
        if (heading) heading.textContent = 'Edit Internship';
        if (submitBtn) submitBtn.textContent = 'Update Internship';

        // Fetch existing data
        try {
            const res = await apiRequest(`/api/internships/${internshipId}`);
            if (res && res.data) {
                const item = res.data;
                if (document.getElementById('company')) document.getElementById('company').value = item.company || '';
                if (document.getElementById('role')) document.getElementById('role').value = item.role || '';
                if (document.getElementById('location')) document.getElementById('location').value = item.location || '';
                if (item.appliedDate && document.getElementById('date')) {
                    document.getElementById('date').value = item.appliedDate;
                }
                if (item.status && document.getElementById('status')) {
                    document.getElementById('status').value = item.status;
                }
                if (document.getElementById('description')) {
                    document.getElementById('description').value = item.description || '';
                }
            }
        } catch (err) {
            showToast('Failed to load internship details', 'error');
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = {
            company: document.getElementById('company').value.trim(),
            role: document.getElementById('role').value.trim(),
            location: document.getElementById('location').value.trim(),
            appliedDate: document.getElementById('date').value,
            status: document.getElementById('status').value,
            description: document.getElementById('description') ? document.getElementById('description').value.trim() : ''
        };

        const originalText = submitBtn.textContent;
        submitBtn.textContent = internshipId ? 'Updating...' : 'Adding...';
        submitBtn.disabled = true;

        try {
            const method = internshipId ? 'PUT' : 'POST';
            const endpoint = internshipId ? `/api/internships/${internshipId}` : '/api/internships';

            await apiRequest(endpoint, {
                method: method,
                body: JSON.stringify(payload)
            });

            showToast(internshipId ? 'Internship updated successfully!' : 'Internship added successfully!', 'success');
            setTimeout(() => {
                window.location.href = 'internships.html';
            }, 600);
        } catch (err) {
            showToast(err.message || 'Operation failed', 'error');
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    });
}

// -------------------------------------------------------------
// Page: Profile
// -------------------------------------------------------------
async function initProfilePage() {
    const form = document.querySelector('form');
    if (!form) return;

    try {
        const res = await apiRequest('/api/profile');
        if (res && res.data) {
            const p = res.data;
            if (document.getElementById('name')) document.getElementById('name').value = p.name || '';
            if (document.getElementById('student-id')) document.getElementById('student-id').value = p.studentId || '';
            if (document.getElementById('department')) document.getElementById('department').value = p.department || '';
            if (document.getElementById('year')) document.getElementById('year').value = p.year || '';
            if (document.getElementById('email')) document.getElementById('email').value = p.email || '';
            if (document.getElementById('college')) document.getElementById('college').value = p.college || '';
            if (document.getElementById('skills')) document.getElementById('skills').value = p.skills || '';

            const headerName = document.querySelector('.profile-header h3') || document.querySelector('.profile-header-text h2');
            if (headerName && p.name) headerName.textContent = p.name;
        }
    } catch (err) {
        showToast('Failed to load profile data', 'error');
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = {
            name: document.getElementById('name').value.trim(),
            studentId: document.getElementById('student-id').value.trim(),
            department: document.getElementById('department').value.trim(),
            year: document.getElementById('year').value,
            email: document.getElementById('email').value.trim(),
            college: document.getElementById('college').value.trim(),
            skills: document.getElementById('skills') ? document.getElementById('skills').value.trim() : ''
        };

        const btn = form.querySelector('button[type="submit"]');
        const originalText = btn.textContent;
        btn.textContent = 'Saving Profile...';
        btn.disabled = true;

        try {
            await apiRequest('/api/profile', {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
            showToast('Profile updated successfully!', 'success');
            const headerName = document.querySelector('.profile-header h3') || document.querySelector('.profile-header-text h2');
            if (headerName) headerName.textContent = payload.name;
        } catch (err) {
            showToast(err.message || 'Failed to update profile', 'error');
        } finally {
            btn.textContent = originalText;
            btn.disabled = false;
        }
    });
}

// -------------------------------------------------------------
// Page: Settings
// -------------------------------------------------------------
async function initSettingsPage() {
    try {
        const res = await apiRequest('/api/settings');
        if (res && res.data) {
            const s = res.data;
            if (document.getElementById('username')) document.getElementById('username').value = s.username || '';
            if (document.getElementById('email')) document.getElementById('email').value = s.email || '';
            if (document.getElementById('theme')) document.getElementById('theme').value = s.theme || 'dark';
            if (document.getElementById('email-updates')) document.getElementById('email-updates').checked = !!s.emailUpdates;
            if (document.getElementById('application-reminders')) document.getElementById('application-reminders').checked = !!s.applicationReminders;
        }
    } catch (err) {
        showToast('Failed to load settings', 'error');
    }

    const saveBtn = document.querySelector('.save-btn');
    if (saveBtn) {
        saveBtn.addEventListener('click', async () => {
            const originalText = saveBtn.textContent;
            saveBtn.textContent = 'Saving Settings...';
            saveBtn.disabled = true;

            try {
                // 1. Account Settings
                const username = document.getElementById('username').value.trim();
                const email = document.getElementById('email').value.trim();
                if (username && email) {
                    await apiRequest('/api/settings/account', {
                        method: 'PUT',
                        body: JSON.stringify({ username, email })
                    });
                }

                // 2. Preferences
                const theme = document.getElementById('theme') ? document.getElementById('theme').value : 'dark';
                const emailUpdates = document.getElementById('email-updates') ? document.getElementById('email-updates').checked : false;
                const applicationReminders = document.getElementById('application-reminders') ? document.getElementById('application-reminders').checked : false;
                await apiRequest('/api/settings/preferences', {
                    method: 'PUT',
                    body: JSON.stringify({ theme, emailUpdates, applicationReminders })
                });

                // 3. Password (if entered)
                const currentPassword = document.getElementById('current-password').value;
                const newPassword = document.getElementById('new-password').value;
                const confirmPassword = document.getElementById('confirm-password').value;

                if (currentPassword || newPassword || confirmPassword) {
                    if (!currentPassword || !newPassword || !confirmPassword) {
                        throw new Error('To change password, please fill in all 3 password fields.');
                    }
                    if (newPassword !== confirmPassword) {
                        throw new Error('New password and confirm password do not match.');
                    }
                    await apiRequest('/api/settings/password', {
                        method: 'PUT',
                        body: JSON.stringify({ currentPassword, newPassword, confirmPassword })
                    });
                    document.getElementById('current-password').value = '';
                    document.getElementById('new-password').value = '';
                    document.getElementById('confirm-password').value = '';
                }

                showToast('Settings saved successfully!', 'success');
            } catch (err) {
                showToast(err.message || 'Failed to save settings', 'error');
            } finally {
                saveBtn.textContent = originalText;
                saveBtn.disabled = false;
            }
        });
    }
}

// Helpers
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr + 'T00:00:00');
    if (isNaN(d.getTime())) return dateStr;
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[d.getMonth()]} ${d.getDate()}, ${d.getFullYear()}`;
}

function formatDateShort(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr + 'T00:00:00');
    if (isNaN(d.getTime())) return dateStr;
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[d.getMonth()]} ${d.getDate()}, ${d.getFullYear()}`;
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Auto-run on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    initAuth();

    const pageInits = {
        'login': initLoginPage,
        'register': initRegisterPage,
        'dashboard': initDashboardPage,
        'internships': initInternshipsPage,
        'add-internship': initAddInternshipPage,
        'profile': initProfilePage,
        'settings': initSettingsPage
    };
    const init = pageInits[currentPage()];
    if (init) init();
});
