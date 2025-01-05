document.addEventListener('DOMContentLoaded', function () {
    const registerForm = document.getElementById('registerForm');
    const errorMessage = document.getElementById('errorMessage');
    const scanButton = document.getElementById('scanQR');
    const qrReader = document.getElementById('qrReader');
    let html5QrcodeScanner = null;

    // QR Code Scanner
    if (scanButton) {
        scanButton.addEventListener('click', () => {
            if (qrReader.classList.contains('d-none')) {
                qrReader.classList.remove('d-none');
                html5QrcodeScanner = new Html5QrcodeScanner(
                    "qrScanner",
                    { fps: 10, qrbox: { width: 250, height: 250 } }
                );

                html5QrcodeScanner.render(
                    (decodedText) => {
                        console.log('QR Code detected:', decodedText);
                        document.getElementById('bioId').value = decodedText;

                        if (html5QrcodeScanner) {
                            html5QrcodeScanner.clear();
                            html5QrcodeScanner = null;
                        }
                        qrReader.classList.add('d-none');
                    },
                    // (error) => {
                    //     console.warn(`QR Code scan error: ${error}`);
                    //     alert("Error scanning QR Code. Please try again.");
                    // }
                );
            } else {
                if (html5QrcodeScanner) {
                    html5QrcodeScanner.clear();
                    html5QrcodeScanner = null;
                }
                qrReader.classList.add('d-none');
            }
        });
    }

    const VALID_BIOIDS = [
        "K1YL8VA2HG", "7DMPYAZAP2", "D05HPPQNJ4", "2WYIM3QCK9", "DHKFIYHMAZ",
        "LZK7P0X0LQ", "H5C98XCENC", "6X6I6TSUFG", "QTLCWUS8NB", "Y4FC3F9ZGS",
        "V30EPKZQI2", "O3WJFGR5WE", "SEIQTS1H16", "X16V7LFHR2", "TLFDFY7RDG",
        "PGPVG5RF42", "FPALKDEL5T", "2BIB99Z54V", "ABQYUQCQS2", "9JSXWO4LGH",
        "QJXQOUPTH9", "GOYWJVDA8A", "6EBQ28A62V", "30MY51J1CJ", "FH6260T08H",
        "JHDCXB62SA", "O0V55ENOT0", "F3ATSRR5DQ", "1K3JTWHA05", "FINNMWJY0G",
        "CET8NUAE09", "VQKBGSE3EA", "E7D6YUPQ6J", "BPX8O0YB5L", "AT66BX2FXM",
        "1PUQV970LA", "CCU1D7QXDT", "TTK74SYYAN", "4HTOAI9YKO", "PD6XPNB80J",
        "BZW5WWDMUY", "340B1EOCMG", "CG1I9SABLL", "49YFTUA96K", "V2JX0IC633",
        "C7IFP4VWIL", "RYU8VSS4N5", "S22A588D75", "88V3GKIVSF", "8OLYIE2FRC"
    ];

    function validateBioId(bioId) {
        return VALID_BIOIDS.includes(bioId);
    }

    // Form Submission Handler
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Clear previous error message
            errorMessage.textContent = '';
            errorMessage.classList.add('d-none');

            const bioId = document.getElementById('bioId').value;

            if (!validateBioId(bioId)) {
                errorMessage.textContent = "Invalid BioID";
                errorMessage.classList.remove('d-none');
                return;
            }

            const formData = {
                email: document.getElementById('email').value,
                fullName: document.getElementById('fullName').value,
                dateOfBirth: document.getElementById('dateOfBirth').value,
                password: document.getElementById('password').value,
                bioId: bioId
            };

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });

                if (response.ok) {
                    alert('Registration successful! Please login.');
                    window.location.href = '/login';
                } else {
                    const error = await response.json();
                    errorMessage.textContent = error.message || 'Registration failed';
                    errorMessage.classList.remove('d-none');
                }
            } catch (error) {
                console.error('Error:', error);
                errorMessage.textContent = 'An error occurred during registration';
                errorMessage.classList.remove('d-none');
            }
        });
    }
});
