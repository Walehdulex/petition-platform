const ErrorHandler = {
    handleError: async (response) => {
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'An error occurred');
        }
        return response;
    },

    showError: (message) => {
        // You can replace this with a more sophisticated error display
        const errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger alert-dismissible fade show';
        errorDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.insertBefore(errorDiv, document.body.firstChild);

        // Auto dismiss after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }
};