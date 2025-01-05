document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = {
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(formData)
                });

                const data = await response.json();
                console.log('Server response:', data);

                if (response.ok) {
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userEmail', data.email);

                    // Add a small delay before redirect
                    setTimeout(() => {
                        if (data.email === 'admin@petition.parliament.sr') {
                            window.location.replace('/committee-dashboard');
                        } else {
                            window.location.replace('/dashboard');
                        }
                    }, 100);
                } else {
                    if (errorMessage) {
                        errorMessage.textContent = data.message || 'Invalid credentials';
                        errorMessage.classList.remove('d-none');
                    }
                }
            } catch (error) {
                console.error('Login error:', error);
                if (errorMessage) {
                    errorMessage.textContent = 'An error occurred during login';
                    errorMessage.classList.remove('d-none');
                }
            }
        });
    }
});