// login.js
document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    loginBtn.addEventListener('click', () => {
        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        if (!username || !password) {
            alert('请输入用户名和密码');
            return;
        }

        // 模拟登录逻辑，可替换为后端接口
        const users = JSON.parse(localStorage.getItem('users')) || [];
        const user = users.find(u => u.username === username && u.password === password);

        if (user) {
            alert(`登录成功: ${username}`);
            localStorage.setItem('currentUser', JSON.stringify(user)); // 保存当前登录用户
            window.location.href = 'shop.html'; // 登录成功跳转商店页面
        } else {
            alert('用户名或密码错误');
        }
    });

    // 可选：回车登录
    [usernameInput, passwordInput].forEach(input => {
        input.addEventListener('keypress', e => {
            if (e.key === 'Enter') loginBtn.click();
        });
    });
});
