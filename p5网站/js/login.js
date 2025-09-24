document.getElementById('login-btn').addEventListener('click', async () => {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !password) return alert('请输入用户名和密码');

    try {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            alert('用户名或密码错误');
            return;
        }

        const user = await res.json(); // 解析成 JSON
        alert(`登录成功: ${user.username}`);
        localStorage.setItem('currentUser', JSON.stringify(user)); // 保存用户信息
        window.location.href = 'shop.html';
    } catch (err) {
        console.error(err);
        alert('登录失败，请检查后端服务');
    }
});
