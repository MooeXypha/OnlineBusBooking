// Make togglePassword global so HTML can call it
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    input.type = input.type === 'password' ? 'text' : 'password';
}

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Collect form values
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const phone = document.getElementById('phone').value;
            const nrc = document.getElementById('nrc').value;
            const gender = document.getElementById('gender').value;
            const dob = document.getElementById('dob').value;
            const citizenship = document.getElementById('citizenship').value;

            // Simple form validation
            if (!username || !email || !password) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Please fill in all required fields'
                });
                return;
            }

            // Build payload
            const payload = {
                username,
                gmail: email, // or change backend to accept "email" instead of "gmail"
                password,
                phoneNumber: phone,
                nrc,
                gender,
                dob,
                citizenship
            };

            try {
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // Some backends return token directly, some return JSON
                let token;
                try {
                    const data = await response.json();
                    token = data.token || data.accessToken;
                } catch {
                    token = await response.text(); // fallback if backend returns plain string
                }

                if (!response.ok) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Registration Failed',
                        text: token || 'Check your input or backend'
                    });
                    return;
                }

                if (token) {
                    localStorage.setItem('token', token);
                }

                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: 'Registration successful! Redirecting to login...',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = './login.html';
                });

            } catch (error) {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while registering'
                });
            }
        });
    }
});
