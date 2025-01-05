const AuthGuard = {
    checkAuth: () => {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login';
            return false;
        }
        return true;
    },

    isCommittee: () => {
        const userEmail = localStorage.getItem('userEmail');
        return userEmail === 'admin@petition.parliament.sr';
    },

    setupAuthHeaders: () => {
        const token = localStorage.getItem('token');
        return {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };
    }
};