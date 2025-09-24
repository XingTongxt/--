document.addEventListener('DOMContentLoaded', () => {
    const registerBtn = document.getElementById('register-btn');

    registerBtn.addEventListener('click', async () => {
        const username = document.getElementById('reg-username').value.trim();
        const password = document.getElementById('reg-password').value.trim();
        const password2 = document.getElementById('reg-password2').value.trim();

        if (!username || !password || !password2) {
            alert('请填写完整信息');
            return;
        }

        if (password !== password2) {
            alert('两次密码不一致');
            return;
        }

        const data = { username, password };

        try {
            const response = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const result = await response.text();
            if (result === 'success') {
                alert('注册成功');
                window.location.href = 'login.html';
            } else {
                alert(result);
            }

        } catch (error) {
            console.error(error);
            alert('注册失败，请检查后端服务');
        }
    });
});
