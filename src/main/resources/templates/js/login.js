document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            if (!username || !password) {
                Swal.fire({ icon: 'error', title: 'Error', text: 'Please fill in all fields' });
                return;
            }

            const payload = { username, password };

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                // Read JSON only once
                const data = await response.json();

                if (!response.ok) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Login Failed',
                        text: data.message || "Invalid username or password"
                    });
                    return;
                }

                if (!data.token) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Login Success but no token found',
                        text: 'Check backend response'
                    });
                    return;
                }

                localStorage.setItem('token', data.token);

                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: 'Login successful! Redirecting...',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = './index.html';
                });

            } catch (error) {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'An error occurred while logging in'
                });
            }
        });
    }
});
