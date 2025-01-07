// Global variables
let currentThreshold = 0;
let currentFilter = 'all';
let statusChart = null;
let signaturesChart = null;

document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
});

// Initialization
async function initializeDashboard() {
    if (!checkAuth()) return;

    setupEventListeners();
    await loadThreshold();
    await loadPetitions();
}

function checkAuth() {
    const token = localStorage.getItem('token');
    const adminEmail = localStorage.getItem('userEmail');

    if (!token) {
        window.location.href = '/login';
        return false;
    }

    if (adminEmail !== 'admin@petition.parliament.sr') {
        window.location.href = '/dashboard';
        return false;
    }

    document.getElementById('adminEmail').textContent = adminEmail;
    return true;
}

// Event Listeners
function setupEventListeners() {
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    document.getElementById('thresholdForm').addEventListener('submit', handleThresholdUpdate);
    document.getElementById('responseForm').addEventListener('submit', handleResponseSubmit);
    document.getElementById('responseForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const petitionId = document.getElementById('petitionId').value;
        const response = document.getElementById('response').value;
        await submitResponse(petitionId, response);
    });
    setupFilterButtons();

}

function setupFilterButtons() {
    const buttons = document.querySelectorAll('.btn-group .btn');
    buttons.forEach(button => {
        button.addEventListener('click', (e) => {
            buttons.forEach(btn => btn.classList.remove('active'));
            e.target.classList.add('active');
            currentFilter = e.target.getAttribute('onclick').match(/'([^']+)'/)[1];
            loadPetitions();
        });
    });
}

// Main Functions
async function loadThreshold() {
    try {
        const response = await fetchWithAuth('/api/admin/threshold');
        if (response.ok) {
            const data = await response.json();
            currentThreshold = data.threshold;
            document.getElementById('threshold').value = currentThreshold;
        }
    } catch (error) {
        handleError('Error loading threshold', error);
    }
}

async function loadPetitions() {
    try {
        const response = await fetchWithAuth('/api/admin/petitions');
        if (response.ok) {
            const petitions = await response.json();
            const filteredPetitions = filterPetitions(petitions);
            displayPetitions(filteredPetitions);
        } else {
            throw new Error(await response.text());
        }
    } catch (error) {
        handleError('Error loading petitions', error);
    }
}

function filterPetitions(petitions) {
    return petitions.filter(petition => {
        switch(currentFilter) {
            case 'open':
                return petition.status === 'OPEN';
            case 'closed':
                return petition.status === 'CLOSED';
            case 'threshold':
                return (petition.signatures || 0) >= currentThreshold;
            default:
                return true;
        }
    });
}

async function submitResponse(petitionId, response) {
    try {
        const responseObj = {
            petitionId,
            response
        };

        const apiResponse = await fetch('/api/admin/response', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify(responseObj)
        });

        if (apiResponse.ok) {
            alert('Response submitted successfully');
            await loadPetitions();  // Optionally reload petitions to reflect the new response
        } else {
            throw new Error('Failed to submit response');
        }
    } catch (error) {
        handleError('Error submitting response', error);
    }
}


function displayPetitions(petitions) {
    const tableBody = document.getElementById('petitionsTableBody');
    tableBody.innerHTML = '';

    if (!petitions.length) {
        tableBody.innerHTML = '<tr><td colspan="5" class="text-center">No petitions found</td></tr>';
        return;
    }

    petitions.forEach(petition => {
        const row = createPetitionRow(petition);
        tableBody.appendChild(row);
    });

    //Update Charts
    updateCharts(petitions);
}

function updateCharts(petitions) {
    try {
        console.log('Updating charts with petitions:', petitions);
        updateStatusChart(petitions);
        updateSignaturesChart(petitions);
    } catch (error) {
        console.error('Error updating charts:', error);
    }
}

function updateStatusChart(petitions) {
    const statusData = {
        open: petitions.filter(p => p.status === 'OPEN').length,
        closed: petitions.filter(p => p.status === 'CLOSED').length
    };

    const ctx = document.getElementById('statusChart').getContext('2d');

    // Destroy existing chart if it exists
    if (statusChart) {
        statusChart.destroy();
    }

    statusChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Open', 'Closed'],
            datasets: [{
                data: [statusData.open, statusData.closed],
                backgroundColor: ['#28a745', '#dc3545'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: true,
                    text: 'Petition Status Distribution'
                }
            }
        }
    });
}

function updateSignaturesChart(petitions) {
    // Sort petitions by signature count
    const sortedPetitions = [...petitions]
        .sort((a, b) => (b.signatures || 0) - (a.signatures || 0))
        .slice(0, 5); // Take top 5

    const ctx = document.getElementById('signaturesChart').getContext('2d');

    // Destroy existing chart if it exists
    if (signaturesChart) {
        signaturesChart.destroy();
    }

    signaturesChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: sortedPetitions.map(p => truncateTitle(p.petitionTitle)),
            datasets: [{
                label: 'Number of Signatures',
                data: sortedPetitions.map(p => p.signatures || 0),
                backgroundColor: '#0d6efd',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Signatures'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Petitions'
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: 'Top 5 Petitions by Signatures'
                }
            }
        }
    });
}






function createPetitionRow(petition) {
    const row = document.createElement('tr');
    const signatureCount = petition.signatures || 0;
    const hasMetThreshold = signatureCount >= currentThreshold;

    row.innerHTML = `
        <td>
            <strong>${escapeHtml(petition.petitionTitle)}</strong>
            <div class="small text-muted">${escapeHtml(petition.petitionText?.substring(0, 100) || '')}...</div>
        </td>
        <td>${escapeHtml(petition.petitioner)}</td>
        <td>
            <div>${signatureCount} signatures</div>
            ${hasMetThreshold ?
        '<span class="badge bg-success">Threshold Met</span>' :
        `<span class="badge bg-warning">Need ${currentThreshold - signatureCount} more</span>`}
        </td>
        <td>
            <span class="badge ${petition.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                ${petition.status}
            </span>
        </td>
        <td>
            ${createActionButtons(petition, hasMetThreshold)}
        </td>
    `;

    return row;
}

// Modal Functions
function showPetitionDetails(petition) {
    try {
        const modalContent = createPetitionDetailsContent(petition);
        showModal('detailsModal', modalContent);
    } catch (error) {
        handleError('Error showing petition details', error);
    }
}

// function showResponseModal(petitionId) {
//     document.getElementById('petitionId').value = petitionId;
//     document.getElementById('response').value = '';
//     new bootstrap.Modal(document.getElementById('responseModal')).show();
// }

function showResponseModal(petitionId) {
    try {
        const modal = document.getElementById('responseModal');
        document.getElementById('petitionId').value = petitionId;
        document.getElementById('response').value = '';
        new bootstrap.Modal(modal).show();
    } catch (error) {
        handleError('Error showing response modal', error);
    }
}

// Utility Functions
async function fetchWithAuth(url, options = {}) {
    return fetch(url, {
        ...options,
        headers: {
            ...options.headers,
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });
}

async function updateThreshold(threshold) {
    try {
        const response = await fetch('/api/admin/threshold', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({ threshold: parseInt(threshold) })
        });

        if (response.ok) {
            currentThreshold = parseInt(threshold);
            alert('Threshold updated successfully');
            await loadPetitions();  // Reload petitions to reflect new threshold
        } else {
            throw new Error('Failed to update threshold');
        }
    } catch (error) {
        handleError('Error updating threshold', error);
    }
}

function handleError(message, error) {
    console.error(message, error);
    alert(`${message}: ${error.message}`);
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Event Handlers
function handleLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    window.location.href = '/login';
}

async function handleThresholdUpdate(e) {
    e.preventDefault();
    const threshold = document.getElementById('threshold').value;
    await updateThreshold(threshold);
    await loadPetitions();
}

async function handleResponseSubmit(e) {
    e.preventDefault();
    const petitionId = document.getElementById('petitionId').value;
    const response = document.getElementById('response').value;
    await submitResponse(petitionId, response);
    bootstrap.Modal.getInstance(document.getElementById('responseModal')).hide();
}

function viewPetitionDetails(petition) {
    try {
        // Sanitize the petition data to prevent XSS
        const sanitizedPetition = {
            petitionTitle: escapeHtml(petition.petitionTitle),
            petitionText: escapeHtml(petition.petitionText),
            petitioner: escapeHtml(petition.petitioner),
            signatures: petition.signatures || 0,
            status: petition.status,
            response: petition.response ? escapeHtml(petition.response) : null
        };

        const modalContent = `
            <div class="modal-header">
                <h5 class="modal-title">Petition Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <h6>Title</h6>
                <p>${sanitizedPetition.petitionTitle}</p>
                
                <h6>Description</h6>
                <p>${sanitizedPetition.petitionText}</p>
                
                <h6>Created By</h6>
                <p>${sanitizedPetition.petitioner}</p>
                
                <h6>Signatures</h6>
                <p>${sanitizedPetition.signatures} total signatures</p>
                
                <h6>Status</h6>
                <p>
                    <span class="badge ${sanitizedPetition.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                        ${sanitizedPetition.status}
                    </span>
                </p>
                
                ${sanitizedPetition.response ? `
                    <h6>Parliamentary Response</h6>
                    <p>${sanitizedPetition.response}</p>
                ` : ''}
            </div>
        `;

        const detailsModal = document.getElementById('detailsModal');
        detailsModal.querySelector('.modal-content').innerHTML = modalContent;
        new bootstrap.Modal(detailsModal).show();
    } catch (error) {
        console.error('Error showing petition details:', error);
        alert('Error showing petition details');
    }
}

// function createActionButtons(petition, hasMetThreshold) {
//     return `
//         <div class="btn-group">
//             <button class="btn btn-outline-primary btn-sm"
//                     onclick='viewPetitionDetails(${JSON.stringify(petition)})'>
//                 View Details
//             </button>
//             ${petition.status === 'OPEN' && hasMetThreshold ? `
//                 <button class="btn btn-primary btn-sm ms-1"
//                         onclick="showResponseModal('${petition.petitionId}')">
//                     Respond
//                 </button>
//             ` : ''}
//             ${petition.response ? `
//                 <button class="btn btn-info btn-sm ms-1 view-response-btn"
//                         data-response="${encodeURIComponent(petition.response)}">
//                     View Response
//                 </button>
//             ` : ''}
//         </div>
//     `;
// }

function createActionButtons(petition, hasMetThreshold) {
    const sanitizedPetition = JSON.stringify(petition).replace(/'/g, '&#39;');
    return `
        <div class="btn-group">
            <button class="btn btn-outline-primary btn-sm" 
                    onclick='viewPetitionDetails(${sanitizedPetition})'>
                View Details
            </button>
            ${petition.status === 'OPEN' && hasMetThreshold ? `
                <button class="btn btn-primary btn-sm ms-1" 
                        onclick="showResponseModal('${petition.petitionId}')">
                    Respond
                </button>
            ` : ''}
            ${petition.response ? `
                <button class="btn btn-info btn-sm ms-1 view-response-btn" 
                        onclick="showResponse('${encodeURIComponent(petition.response)}')">
                    View Response
                </button>
            ` : ''}
        </div>
    `;
}

function showResponse(response) {
    const modalContent = `
        <div class="modal-header">
            <h5 class="modal-title">Parliamentary Response</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <p>${decodeURIComponent(response)}</p>
        </div>
    `;

    const responseViewModal = document.getElementById('responseViewModal');
    responseViewModal.querySelector('.modal-content').innerHTML = modalContent;
    new bootstrap.Modal(responseViewModal).show();
}

function handleError(message, error) {
    console.error(message, error);
    alert(`${message}: ${error.message}`);
}

function createPetitionDetailsContent(petition) {
    return `
        <div class="modal-header">
            <h5 class="modal-title">Petition Details</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <h6>Title</h6>
            <p>${escapeHtml(petition.petitionTitle)}</p>
            
            <h6>Description</h6>
            <p>${escapeHtml(petition.petitionText)}</p>
            
            <h6>Created By</h6>
            <p>${escapeHtml(petition.petitioner)}</p>
            
            <h6>Signatures</h6>
            <p>${petition.signatures || 0} total signatures</p>
            
            <h6>Status</h6>
            <p>
                <span class="badge ${petition.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                    ${petition.status}
                </span>
            </p>
            
            ${petition.response ? `
                <h6>Parliamentary Response</h6>
                <p>${escapeHtml(petition.response)}</p>
            ` : ''}
        </div>
    `;
}

function createPetitionDetailsContent(petition) {
    return `
        <div class="modal-header">
            <h5 class="modal-title">Petition Details</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
            <h6>Title</h6>
            <p>${escapeHtml(petition.petitionTitle)}</p>
            
            <h6>Description</h6>
            <p>${escapeHtml(petition.petitionText)}</p>
            
            <h6>Created By</h6>
            <p>${escapeHtml(petition.petitioner)}</p>
            
            <h6>Signatures</h6>
            <p>${petition.signatures || 0} total signatures</p>
            
            <h6>Status</h6>
            <p>
                <span class="badge ${petition.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                    ${petition.status}
                </span>
            </p>
            
            ${petition.response ? `
                <h6>Parliamentary Response</h6>
                <p>${escapeHtml(petition.response)}</p>
            ` : ''}
        </div>
    `;
}

// Add this function for submitting the response
async function submitResponse(petitionId, response) {
    try {
        const res = await fetch(`/api/admin/petitions/${petitionId}/respond`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({ response })
        });

        if (res.ok) {
            alert('Response submitted successfully. Petition has been closed.');
            // Hide the modal
            const responseModal = bootstrap.Modal.getInstance(document.getElementById('responseModal'));
            responseModal.hide();
            // Reload petitions to show updated status
            await loadPetitions();
        } else {
            const error = await res.json();
            throw new Error(error.message || 'Failed to submit response');
        }
    } catch (error) {
        handleError('Error submitting response', error);
    }
}