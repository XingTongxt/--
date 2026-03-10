export async function loadUsers(token, renderUsersCallback) {
    if (!token) return alert('未登录或权限不足');

    try {
        const res = await fetch('http://localhost:8080/admin/users', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('获取用户失败');
        const users = await res.json();
        renderUsersCallback(users); 
    } catch (err) {
        console.error(err);
        alert('加载用户列表失败');
    }
}

export async function changeRole(token, username, newRole, reloadCallback) {
    try {
        const res = await fetch(`http://localhost:8080/admin/users/${username}/role`, {
            method: 'PUT',
            headers: { 
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ role: newRole })
        });
        if (!res.ok) throw new Error('修改失败');
        alert('角色修改成功');
        reloadCallback();
    } catch (err) {
        console.error(err);
        alert('修改角色失败');
    }
}

export async function deleteUser(token, username, reloadCallback) {
    if (!confirm(`确定删除用户 ${username} 吗？`)) return;
    try {
        const res = await fetch(`http://localhost:8080/admin/users/${username}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('删除失败');
        alert('删除成功');
        reloadCallback();
    } catch (err) {
        console.error(err);
        alert('删除用户失败');
    }
}