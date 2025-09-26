let token = localStorage.getItem('token');

// 获取用户/管理员信息
async function loadUserInfo() {
    if (!token) {
        alert('未登录');
        window.location.href = 'login.html';
        return;
    }

    let data;
    try {
        let res = await fetch('http://localhost:8080/api/user/info', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            // 尝试管理员接口
            res = await fetch('http://localhost:8080/admin/info', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
        }

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            alert(errData.error || '获取用户信息失败');
            return;
        }

        data = await res.json();

        // 填充页面信息
        document.getElementById('username').innerText = data.username;
        document.getElementById('email').innerText = data.email || '未绑定';
        document.getElementById('role').innerText = data.role || 'user';

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
        // 普通用户接口
        let res = await fetch('http://localhost:8080/api/user/change-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ newPassword: newPwd })
        });

        if (!res.ok) {
            // 管理员接口
            res = await fetch('http://localhost:8080/admin/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ newPassword: newPwd })
            });
        }

        const text = await res.text();
        alert(text);
    } catch (err) {
        console.error(err);
        alert('修改密码失败');
    }
});

// 退出登录
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('token');
    alert('已退出登录');
    window.location.href = 'login.html';
});

// 页面加载时获取信息
loadUserInfo();
