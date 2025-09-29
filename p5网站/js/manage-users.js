const token = localStorage.getItem('token');

        async function loadUsers() {
            if (!token) return alert('未登录或权限不足');

            try {
                const res = await fetch('http://localhost:8080/admin/users', {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!res.ok) throw new Error('获取用户失败');
                const users = await res.json();
                renderUsers(users);
            } catch (err) {
                console.error(err);
                alert('加载用户列表失败');
            }
        }

        function renderUsers(users) {
            const tbody = document.querySelector('#user-table tbody');
            tbody.innerHTML = '';
            users.forEach(user => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${user.username}</td>
                    <td>${user.email || '未绑定'}</td>
                    <td>
                        <select data-username="${user.username}">
                            <option value="USER" ${user.role === 'USER' ? 'selected' : ''}>USER</option>
                            <option value="ADMIN" ${user.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                        </select>
                    </td>
                    <td>
                        <button onclick="changeRole('${user.username}')">修改角色</button>
                        <button onclick="deleteUser('${user.username}')">删除</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }

        async function changeRole(username) {
            const select = document.querySelector(`select[data-username="${username}"]`);
            const newRole = select.value;
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
                loadUsers();
            } catch (err) {
                console.error(err);
                alert('修改角色失败');
            }
        }

        async function deleteUser(username) {
            if (!confirm(`确定删除用户 ${username} 吗？`)) return;
            try {
                const res = await fetch(`http://localhost:8080/admin/users/${username}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!res.ok) throw new Error('删除失败');
                alert('删除成功');
                loadUsers();
            } catch (err) {
                console.error(err);
                alert('删除用户失败');
            }
        }

        // 页面加载时获取用户列表
        loadUsers();