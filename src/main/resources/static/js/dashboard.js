document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Display user email
    const userEmail = localStorage.getItem('userEmail');
    document.getElementById('userEmail').textContent = userEmail;

    // Load petitions
    loadPetitions();

    // Logout handler
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userEmail');
        window.location.href = '/login';
    });

    // Create petition form handler
    document.getElementById('createPetitionForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const petitionData = {
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        };

        try {
            const response = await fetch('/api/petitions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(petitionData)
            });

            if (response.ok) {
                document.getElementById('createPetitionForm').reset();
                loadPetitions();
            } else {
                alert('Failed to create petition');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while creating the petition');
        }
    });
});

async function loadPetitions() {
    const token = localStorage.getItem('token');
    try {
        const response = await fetch('/api/petitions', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const petitions = await response.json();
            displayPetitions(petitions);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function displayPetitions(petitions) {
    const petitionsList = document.getElementById('petitionsList');
    petitionsList.innerHTML = '';

    petitions.forEach(petition => {
        const card = document.createElement('div');
        card.className = 'card mb-3';
        card.innerHTML = `
            <div class="card-body">
                <h5 class="card-title">${petition.title}</h5>
                <p class="card-text">${petition.content}</p>
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="badge ${petition.status === 'OPEN' ? 'bg-success' : 'bg-secondary'}">
                            ${petition.status}
                        </span>
                        <small class="text-muted ms-2">Signatures: ${petition.signatures.length}</small>
                    </div>
                    ${petition.status === 'OPEN' ? `
                        <button class="btn btn-primary btn-sm sign-petition" 
                                data-id="${petition.id}">
                            Sign Petition
                        </button>
                    ` : `
                        <div class="response-text">
                            <small class="text-muted">Response: ${petition.response || 'No response yet'}</small>
                        </div>
                    `}
                </div>
            </div>
        `;
        petitionsList.appendChild(card);
    });

    // Add event listeners for sign buttons
    document.querySelectorAll('.sign-petition').forEach(button => {
        button.addEventListener('click', async (e) => {
            const petitionId = e.target.dataset.id;
            await signPetition(petitionId);
        });
    });
}

async function signPetition(petitionId) {
    const token = localStorage.getItem('token');
    try {
        const response = await fetch(`/api/petitions/${petitionId}/sign`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            loadPetitions();
        } else {
            const error = await response.json();
            alert(error.message || 'Failed to sign petition');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred while signing the petition');
    }
}