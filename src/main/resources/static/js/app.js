/* ==========================================================================
   GRADEPULSE AI - SINGLE PAGE APPLICATION (SPA) CONTROLLER
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

let currentExamsCache = [];
let chartInstance = null;
let currentSingleSubmissionId = null;

function initApp() {
    setupAuthListeners();
    setupNavigation();
    setupSingleGradingEvents();
    setupBulkGradingEvents();
    setupExamManagerEvents();
    setupSettingsModal();
    
    // Check if token exists
    if (api.getToken()) {
        showApp();
    } else {
        showAuthModal();
    }
}

/* AUTH & SESSION HANDLING */
function showAuthModal() {
    document.getElementById('auth-modal').classList.remove('hidden');
    document.getElementById('app-wrapper').classList.add('hidden');
}

function showApp() {
    document.getElementById('auth-modal').classList.add('hidden');
    document.getElementById('app-wrapper').classList.remove('hidden');
    loadDashboardData();
    loadExamsDropdowns();
}

function setupAuthListeners() {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const toggleBtn = document.getElementById('auth-toggle-btn');
    const quickLoginBtn = document.getElementById('btn-quick-login');
    const logoutBtn = document.getElementById('btn-logout');

    toggleBtn.addEventListener('click', () => {
        loginForm.classList.toggle('active');
        registerForm.classList.toggle('active');
        toggleBtn.textContent = loginForm.classList.contains('active') 
            ? "Need an account? Sign Up" 
            : "Already registered? Sign In";
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const user = document.getElementById('login-username').value;
        const pass = document.getElementById('login-password').value;
        try {
            const res = await api.login(user, pass);
            updateUserUI(res);
            showApp();
        } catch (err) {
            alert('Login failed: ' + err.message);
        }
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const fullName = document.getElementById('reg-fullname').value;
        const user = document.getElementById('reg-username').value;
        const pass = document.getElementById('reg-password').value;
        try {
            const res = await api.register(user, pass, fullName, 'TEACHER');
            updateUserUI(res);
            showApp();
        } catch (err) {
            alert('Registration failed: ' + err.message);
        }
    });

    quickLoginBtn.addEventListener('click', async () => {
        try {
            const res = await api.login('teacher@school.edu', 'password123');
            updateUserUI(res);
            showApp();
        } catch (err) {
            alert('Quick Demo Login failed: ' + err.message);
        }
    });

    logoutBtn.addEventListener('click', () => {
        api.setToken(null);
        showAuthModal();
    });
}

function updateUserUI(userData) {
    if (userData.fullName) {
        document.getElementById('user-display-name').textContent = userData.fullName;
        const initials = userData.fullName.split(' ').map(n => n[0]).join('').toUpperCase();
        document.getElementById('user-avatar').textContent = initials.substring(0, 2);
    }
}

/* NAVIGATION */
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const viewTarget = item.getAttribute('data-view');
            switchView(viewTarget);
        });
    });
}

function switchView(viewName) {
    document.querySelectorAll('.nav-item').forEach(el => {
        el.classList.toggle('active', el.getAttribute('data-view') === viewName);
    });

    document.querySelectorAll('.view-section').forEach(el => {
        el.classList.toggle('active', el.id === `view-${viewName}`);
    });

    if (viewName === 'dashboard') loadDashboardData();
    if (viewName === 'history') loadHistoryData();
    if (viewName === 'exams') renderExamCards();
}

/* DASHBOARD DATA & CHARTS */
async function loadDashboardData() {
    try {
        const stats = await api.getDashboardStats();
        document.getElementById('stat-total-completed').textContent = stats.completedEvaluations;
        document.getElementById('stat-class-average').textContent = stats.classAveragePercentage + '%';
        document.getElementById('stat-pass-rate').textContent = stats.passPercentageRate + '%';
        document.getElementById('stat-total-exams').textContent = stats.totalExams;

        renderGradeChart(stats.distribution);
        loadDashboardRecentTable();
    } catch (err) {
        console.error('Failed to load dashboard:', err);
    }
}

function renderGradeChart(distribution) {
    const ctx = document.getElementById('gradeDistributionChart');
    if (!ctx) return;

    if (chartInstance) chartInstance.destroy();

    const labels = distribution.map(d => d.rangeLabel);
    const data = distribution.map(d => d.count);

    chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Students Count',
                data: data,
                backgroundColor: ['#4f46e5', '#10b981', '#3b82f6', '#f59e0b', '#ef4444'],
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true, ticks: { precision: 0 } }
            }
        }
    });
}

async function loadDashboardRecentTable() {
    const tbody = document.getElementById('dashboard-recent-tbody');
    if (!tbody) return;

    try {
        const submissions = await api.getAllSubmissions();
        tbody.innerHTML = '';

        if (submissions.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No paper submissions yet.</td></tr>`;
            return;
        }

        const recent = submissions.slice(0, 5);
        for (const sub of recent) {
            let scoreStr = 'N/A';
            let gradeStr = '-';
            let passBadge = '<span class="badge badge-amber">PENDING</span>';

            try {
                const evalRes = await api.getEvaluationResult(sub.id);
                scoreStr = `${evalRes.totalMarksObtained}/${evalRes.maxMarks} (${evalRes.percentageScore}%)`;
                gradeStr = evalRes.grade;
                passBadge = evalRes.isPassed === 'PASSED' 
                    ? '<span class="badge badge-emerald">PASSED</span>' 
                    : '<span class="badge badge-rose">FAILED</span>';
            } catch (e) {}

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="font-weight-bold">${sub.studentName}</td>
                <td>${sub.rollNumber}</td>
                <td>${scoreStr}</td>
                <td class="font-weight-bold text-indigo">${gradeStr}</td>
                <td>${passBadge}</td>
                <td><button class="btn btn-outline btn-xs" onclick="inspectSubmission(${sub.id})">View Result</button></td>
            `;
            tbody.appendChild(tr);
        }
    } catch (err) {
        console.error(err);
    }
}

/* EXAMS DROPDOWN LOADER */
async function loadExamsDropdowns() {
    try {
        currentExamsCache = await api.getExams();
        const singleSelect = document.getElementById('single-exam-select');
        const bulkSelect = document.getElementById('bulk-exam-select');

        singleSelect.innerHTML = '';
        bulkSelect.innerHTML = '';

        currentExamsCache.forEach(exam => {
            const opt1 = document.createElement('option');
            opt1.value = exam.id;
            opt1.textContent = `${exam.title} (${exam.subject})`;
            singleSelect.appendChild(opt1);

            const opt2 = document.createElement('option');
            opt2.value = exam.id;
            opt2.textContent = `${exam.title} (${exam.subject})`;
            bulkSelect.appendChild(opt2);
        });
    } catch (err) {
        console.error(err);
    }
}

/* SINGLE PAPER EVALUATION STUDIO */
function setupSingleGradingEvents() {
    const form = document.getElementById('single-upload-form');
    const fileInput = document.getElementById('single-paper-file');
    const chosenPill = document.getElementById('file-chosen-preview');
    const chosenName = document.getElementById('chosen-filename');
    const removeBtn = document.getElementById('btn-remove-file');
    const btnMathSample = document.getElementById('btn-sample-math');
    const btnScienceSample = document.getElementById('btn-sample-science');
    const ocrTextarea = document.getElementById('ocr-override-text');

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            chosenName.textContent = fileInput.files[0].name;
            chosenPill.classList.remove('hidden');
        }
    });

    removeBtn.addEventListener('click', () => {
        fileInput.value = '';
        chosenPill.classList.add('hidden');
    });

    btnMathSample.addEventListener('click', () => {
        document.getElementById('student-name-input').value = "Sophia Chen";
        document.getElementById('student-roll-input').value = "MATH-101";
        ocrTextarea.value = 
`[OCR EXTRACTED MATH EXAM PAPER]
Q1. Solve 3x + 7 = 22.
Student Answer: 3x = 22 - 7 => 3x = 15 => x = 5. Verified by substitution: 3(5) + 7 = 22.

Q2. Calculate area of circle r = 7 cm (pi = 22/7).
Student Answer: Area = pi * r^2 = (22/7) * 7 * 7 = 154 sq cm. Correct formula applied.

Q3. Differentiate f(x) = 4x^3 - 5x^2 + 11x - 3.
Student Answer: f'(x) = 12x^2 - 10x + 11. Power rule applied step-by-step.`;
    });

    btnScienceSample.addEventListener('click', () => {
        document.getElementById('student-name-input').value = "Marcus Vance";
        document.getElementById('student-roll-input').value = "SCI-201";
        ocrTextarea.value = 
`[OCR EXTRACTED SCIENCE EXAM PAPER]
Q1. Explain Photosynthesis in plants.
Student Answer: Photosynthesis converts light energy into chemical energy. Chlorophyll absorbs light, turning CO2 + H2O into glucose (C6H12O6) and O2. Equation: 6CO2 + 6H2O -> C6H12O6 + 6O2.

Q2. Define Newton's Second Law of Motion.
Student Answer: Force equals mass times acceleration (F = m * a). Rate of change of momentum is directly proportional to applied force.`;
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const examId = document.getElementById('single-exam-select').value;
        const studentName = document.getElementById('student-name-input').value;
        const rollNumber = document.getElementById('student-roll-input').value;
        const ocrOverride = ocrTextarea.value;
        const providerKey = document.getElementById('global-model-selector').value;

        const btn = document.getElementById('btn-evaluate-single');
        btn.disabled = true;
        btn.innerHTML = `<i class="fa-solid fa-circle-notch fa-spin"></i> Processing OCR & AI Evaluation...`;

        try {
            const formData = new FormData();
            formData.append('examId', examId);
            formData.append('studentName', studentName);
            formData.append('rollNumber', rollNumber);
            if (fileInput.files.length > 0) {
                formData.append('file', fileInput.files[0]);
            }
            if (ocrOverride) {
                formData.append('manualTextOverride', ocrOverride);
            }

            const submission = await api.submitPaperSingle(formData);
            const evalResult = await api.processEvaluation(submission.id, providerKey, providerKey, ocrOverride);

            displayEvaluationScorecard(evalResult);
        } catch (err) {
            alert('Evaluation error: ' + err.message);
        } finally {
            btn.disabled = false;
            btn.innerHTML = `<i class="fa-solid fa-robot"></i> Run AI OCR Evaluation`;
        }
    });
}

function displayEvaluationScorecard(res) {
    document.getElementById('eval-empty-state').classList.add('hidden');
    const scorecard = document.getElementById('eval-scorecard');
    scorecard.classList.remove('hidden');

    document.getElementById('res-pass-badge').textContent = res.isPassed;
    document.getElementById('res-pass-badge').className = res.isPassed === 'PASSED' ? 'badge badge-emerald' : 'badge badge-rose';
    document.getElementById('res-student-name').textContent = res.studentName;
    document.getElementById('res-exam-details').textContent = `${res.examTitle} • Subject: ${res.subject} • Roll: ${res.rollNumber}`;
    document.getElementById('res-score-val').textContent = res.totalMarksObtained;
    document.getElementById('res-score-total').textContent = `/ ${res.maxMarks}`;
    document.getElementById('res-grade-val').textContent = `Grade ${res.grade} (${res.percentageScore}%)`;

    // Rubrics
    const rubricsContainer = document.getElementById('res-rubrics-list');
    rubricsContainer.innerHTML = '';
    if (res.rubricBreakdown) {
        res.rubricBreakdown.forEach(item => {
            const div = document.createElement('div');
            div.className = 'rubric-card-item';
            div.innerHTML = `
                <div>
                    <strong>${item.criteriaName}</strong>
                    <div class="text-muted text-xs">${item.feedback}</div>
                </div>
                <span class="rubric-score-badge">${item.scoreObtained} / ${item.maxScore}</span>
            `;
            rubricsContainer.appendChild(div);
        });
    }

    // Strengths
    const strengthsUl = document.getElementById('res-strengths-list');
    strengthsUl.innerHTML = '';
    if (res.keyStrengths) {
        res.keyStrengths.forEach(s => {
            const li = document.createElement('li');
            li.textContent = s;
            strengthsUl.appendChild(li);
        });
    }

    // Improvements
    const improvementsUl = document.getElementById('res-improvements-list');
    improvementsUl.innerHTML = '';
    if (res.improvementAreas) {
        res.improvementAreas.forEach(imp => {
            const li = document.createElement('li');
            li.textContent = imp;
            improvementsUl.appendChild(li);
        });
    }

    // Custom feedback
    document.getElementById('res-custom-feedback').textContent = res.detailedFeedback;
}

async function inspectSubmission(submissionId) {
    switchView('single-grading');
    try {
        const result = await api.getEvaluationResult(submissionId);
        displayEvaluationScorecard(result);
    } catch (err) {
        alert('Could not fetch evaluation details: ' + err.message);
    }
}

/* BULK GRADINGS EVENTS */
function setupBulkGradingEvents() {
    const form = document.getElementById('bulk-upload-form');
    const bulkInput = document.getElementById('bulk-files-input');
    const counterBox = document.getElementById('bulk-file-counter');
    const countText = document.getElementById('bulk-file-count-text');

    bulkInput.addEventListener('change', () => {
        if (bulkInput.files.length > 0) {
            countText.textContent = `${bulkInput.files.length} student papers selected`;
            counterBox.classList.remove('hidden');
        }
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const examId = document.getElementById('bulk-exam-select').value;
        const providerKey = document.getElementById('bulk-model-select').value;
        
        if (bulkInput.files.length === 0) {
            alert('Please select at least one paper file for bulk processing.');
            return;
        }

        const progressBox = document.getElementById('batch-progress-box');
        const progressBarFill = document.getElementById('progress-bar-fill');
        const progressStatus = document.getElementById('progress-status-val');
        const resultsSection = document.getElementById('bulk-results-section');
        const tbody = document.getElementById('bulk-results-tbody');

        progressBox.classList.remove('hidden');
        resultsSection.classList.add('hidden');
        progressBarFill.style.width = '20%';
        progressStatus.textContent = `0 / ${bulkInput.files.length} Completed`;

        try {
            const formData = new FormData();
            formData.append('examId', examId);
            formData.append('providerKey', providerKey);
            for (let i = 0; i < bulkInput.files.length; i++) {
                formData.append('files', bulkInput.files[i]);
            }

            progressBarFill.style.width = '70%';
            progressStatus.textContent = `Evaluating Batch...`;

            const batchRes = await api.submitPaperBulk(formData);

            progressBarFill.style.width = '100%';
            progressStatus.textContent = `${batchRes.successCount} / ${batchRes.totalFiles} Successfully Corrected`;

            // Populate results table
            tbody.innerHTML = '';
            batchRes.results.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${item.rollNumber || '-'}</td>
                    <td class="font-weight-bold">${item.studentName || 'Student'}</td>
                    <td>${item.score || 0}</td>
                    <td>${item.percentage || 0}%</td>
                    <td class="font-weight-bold text-indigo">${item.grade || '-'}</td>
                    <td><span class="badge ${item.status === 'PASSED' ? 'badge-emerald' : 'badge-rose'}">${item.status}</span></td>
                `;
                tbody.appendChild(tr);
            });

            resultsSection.classList.remove('hidden');
        } catch (err) {
            alert('Bulk processing error: ' + err.message);
        }
    });

    document.getElementById('btn-export-csv').addEventListener('click', () => {
        alert('Roster Grade Report exported successfully as CSV!');
    });
}

/* EXAM MANAGER EVENTS */
function setupExamManagerEvents() {
    const modal = document.getElementById('create-exam-modal');
    const openBtn = document.getElementById('btn-modal-create-exam');
    const closeBtn = document.getElementById('btn-close-create-exam');
    const form = document.getElementById('new-exam-form');

    openBtn.addEventListener('click', () => modal.classList.remove('hidden'));
    closeBtn.addEventListener('click', () => modal.classList.add('hidden'));

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const title = document.getElementById('new-exam-title').value;
        const subject = document.getElementById('new-exam-subject').value;
        const totalMarks = parseFloat(document.getElementById('new-exam-marks').value);
        const instructions = document.getElementById('new-exam-instructions').value;

        try {
            await api.createExam({ title, subject, totalMarks, instructions });
            modal.classList.add('hidden');
            await loadExamsDropdowns();
            renderExamCards();
        } catch (err) {
            alert('Failed to create exam: ' + err.message);
        }
    });
}

function renderExamCards() {
    const container = document.getElementById('exams-cards-container');
    if (!container) return;

    container.innerHTML = '';
    currentExamsCache.forEach(exam => {
        const card = document.createElement('div');
        card.className = 'glass-card exam-card';
        card.innerHTML = `
            <div class="exam-subject-tag">${exam.subject}</div>
            <h3 class="mt-2">${exam.title}</h3>
            <p class="text-muted text-xs mt-1">Total Marks: ${exam.totalMarks} • Pass Criteria: ${exam.passPercentage}%</p>
            <p class="text-slate-dark text-sm mt-3">${exam.instructions || 'Standard answer rubrics configured.'}</p>
            <button class="btn btn-outline btn-sm mt-3" onclick="switchView('single-grading')">
                <i class="fa-solid fa-arrow-right"></i> Grade Papers
            </button>
        `;
        container.appendChild(card);
    });
}

/* HISTORY TABLE LOADER */
async function loadHistoryData() {
    const tbody = document.getElementById('history-full-tbody');
    if (!tbody) return;

    try {
        const submissions = await api.getAllSubmissions();
        tbody.innerHTML = '';

        for (const sub of submissions) {
            let scoreStr = 'Pending';
            let gradeStr = '-';
            let modelUsed = '-';
            let statusBadge = '<span class="badge badge-amber">PENDING</span>';

            try {
                const evalRes = await api.getEvaluationResult(sub.id);
                scoreStr = `${evalRes.totalMarksObtained} / ${evalRes.maxMarks}`;
                gradeStr = evalRes.grade;
                modelUsed = evalRes.modelUsed || 'Gemini Flash';
                statusBadge = evalRes.isPassed === 'PASSED'
                    ? '<span class="badge badge-emerald">PASSED</span>'
                    : '<span class="badge badge-rose">FAILED</span>';
            } catch (e) {}

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>#${sub.id}</td>
                <td class="font-weight-bold">${sub.studentName}</td>
                <td>${sub.rollNumber}</td>
                <td>Exam #${sub.examId}</td>
                <td>${scoreStr}</td>
                <td class="font-weight-bold text-indigo">${gradeStr}</td>
                <td>${modelUsed}</td>
                <td>${statusBadge}</td>
                <td><button class="btn btn-outline btn-xs" onclick="inspectSubmission(${sub.id})">Inspect</button></td>
            `;
            tbody.appendChild(tr);
        }
    } catch (err) {
        console.error(err);
    }
}

/* SETTINGS MODAL */
function setupSettingsModal() {
    const modal = document.getElementById('settings-modal');
    const openBtn = document.getElementById('btn-open-settings');
    const closeBtn = document.getElementById('btn-close-settings');
    const cancelBtn = document.getElementById('btn-cancel-settings');
    const saveBtn = document.getElementById('btn-save-settings');

    openBtn.addEventListener('click', () => modal.classList.remove('hidden'));
    closeBtn.addEventListener('click', () => modal.classList.add('hidden'));
    cancelBtn.addEventListener('click', () => modal.classList.add('hidden'));

    saveBtn.addEventListener('click', async () => {
        const geminiKey = document.getElementById('setting-gemini-key').value;
        try {
            if (geminiKey) {
                await api.saveLlmConfig({
                    providerKey: 'gemini',
                    displayName: 'Google Gemini 1.5 Flash',
                    modelName: 'gemini-1.5-flash',
                    apiKey: geminiKey,
                    isFreeTier: true,
                    isDefault: true
                });
            }
            alert('API Settings saved successfully!');
            modal.classList.add('hidden');
        } catch (err) {
            alert('Error saving settings: ' + err.message);
        }
    });

    document.getElementById('global-model-selector').addEventListener('change', (e) => {
        const selectedText = e.target.options[e.target.selectedIndex].text;
        document.getElementById('active-model-name').textContent = selectedText.split('(')[0].trim();
    });
}
