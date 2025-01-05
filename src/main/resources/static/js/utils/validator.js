const Validator = {
    validateEmail: (email) => {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    },

    validateBioId: (bioId) => {
        return bioId.length === 10 && /^[A-Z0-9]+$/.test(bioId);
    },

    validatePassword: (password) => {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 number
        const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
        return re.test(password);
    },

    validateForm: (formData) => {
        const errors = {};

        if (!formData.email || !Validator.validateEmail(formData.email)) {
            errors.email = 'Invalid email address';
        }

        if (!formData.password || !Validator.validatePassword(formData.password)) {
            errors.password = 'Password must be at least 8 characters with 1 uppercase, 1 lowercase, and 1 number';
        }

        if (formData.bioId && !Validator.validateBioId(formData.bioId)) {
            errors.bioId = 'Invalid BioID format';
        }

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    }
};