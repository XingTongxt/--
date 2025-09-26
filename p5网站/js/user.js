let token = localStorage.getItem('token');

// 获取用户信息
async function loadUserInfo() {
    if (!token) {
        alert('未登录');
        window.location.href = 'login.html';
        return;
    }

    try {
        const res = await fetch('http://localhost:8080/api/user/info', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        const data = await res.json();
        if (!res.ok) {
            alert(data.error || '获取用户信息失败');
            return;
        }

        document.getElementById('username').innerText = data.username;
        document.getElementById('email').innerText = data.email || '未绑定';
    } catch (err) {
        console.error(err);
        alert('获取用户信息失败');
    }
}

// 修改密码
document.getElementById('change-password-btn').addEventListener('click', async () => {
    const newPwd = prompt('请输入新密码');
    if (!newPwd) return;

    try {
        const res = await fetch('http://localhost:8080/api/user/change-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ newPassword: newPwd })
        });

        const text = await res.text();
        alert(text);
    } catch (err) {
        console.error(err);
        alert('修改密码失败');
    }
});

// 退出登录
document.getElementById('logout-btn').addEventListener('click', async () => {
    localStorage.removeItem('token'); // 删除 token
    alert('已退出登录');
    window.location.href = 'login.html';
});

// 页面加载时获取用户信息
loadUserInfo();