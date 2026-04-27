let vueRouter = null;

// 保存用户信息
export const userState = {
  username: '加载中...',
  email: '加载中...',
  role: ''
};

// 获取最新 token
function getToken() {
  return localStorage.getItem('token');
}

// 初始化 JS，注入 Vue Router
export function initUserJS(router) {
  vueRouter = router;
  loadUserInfo();
}

// 获取用户/管理员信息
export async function loadUserInfo() {
  const token = getToken();
  if (!token) {
    alert('未登录');
    if (vueRouter) vueRouter.push('/login');
    else window.location.href = 'login.html';
    return;
  }

  try {
    let res = await fetch('http://localhost:8080/api/user/info', {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
      res = await fetch('http://localhost:8080/admin/info', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
    }

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      alert(errData.error || '获取用户信息失败');
      return;
    }

    const data = await res.json();
    userState.username = data.data.username;
    userState.email = data.data.email || '未绑定';
    userState.role = data.data.role || 'user';

    // 更新 DOM
    const usernameEl = document.getElementById('username');
    const emailEl = document.getElementById('email');
    const roleEl = document.getElementById('role');

    if (usernameEl) usernameEl.innerText = userState.username;
    if (emailEl) emailEl.innerText = userState.email;
    if (roleEl) roleEl.innerText = userState.role;

  } catch (err) {
    console.error(err);
    alert('获取用户信息失败');
  }
}

// 修改密码
export async function changePassword() {
  const token = getToken();
  if (!token) return alert('未登录');

  const newPwd = prompt('请输入新密码');
  if (!newPwd) return;

  try {
    let res = await fetch('http://localhost:8080/api/user/change-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ newPassword: newPwd })
    });

    if (!res.ok) {
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
}

// 退出登录
export function logout() {
  localStorage.removeItem('token');
  alert('已退出登录');
  if (vueRouter) vueRouter.push('/login');
  else window.location.href = 'login.html';
}