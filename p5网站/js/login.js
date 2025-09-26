document.getElementById('login-btn').addEventListener('click', async () => {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!username || !password) return alert('请输入用户名和密码');

    const res = await fetch('http://localhost:8080/api/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
        credentials: 'include'
    });

    const data = await res.json();   // ✅ 用 json() 获取
    if (res.ok) {
        localStorage.setItem('token', data.token);
        // sessionStorage.setItem('username', data.username);
        alert(data.message);
        window.location.href = 'user.html';
    } else {
        alert(data.error);
    }
});
