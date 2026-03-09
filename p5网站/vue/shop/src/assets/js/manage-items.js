export async function loadItems(token, renderCallback) {
    if (!token) return alert('未登录或权限不足');

    try {
        const res = await fetch('http://localhost:8080/admin/items', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('获取物品失败');
        const items = await res.json();
        renderCallback(items);
    } catch (err) {
        console.error(err);
        alert(err.message || '加载物品列表失败');
    }
}

export async function addItem(token, itemData, reloadCallback) {
    try {
        const res = await fetch('http://localhost:8080/admin/items', {
            method: 'POST',
            headers: { 
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(itemData)
        });
        if (!res.ok) throw new Error('新增失败');
        alert('新增成功');
        reloadCallback();
    } catch (err) {
        console.error(err);
        alert(err.message || '新增物品失败');
    }
}

export async function editItem(token, id, updatedData, reloadCallback) {
    try {
        const res = await fetch(`http://localhost:8080/admin/items/${id}`, {
            method: 'PUT',
            headers: { 
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        });
        if (!res.ok) throw new Error('修改失败');
        alert('修改成功');
        reloadCallback();
    } catch (err) {
        console.error(err);
        alert(err.message || '修改物品失败');
    }
}

export async function deleteItem(token, id, reloadCallback) {
    if (!confirm('确定删除该物品吗？')) return;
    try {
        const res = await fetch(`http://localhost:8080/admin/items/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!res.ok) throw new Error('删除失败');
        alert('删除成功');
        reloadCallback();
    } catch (err) {
        console.error(err);
        alert(err.message || '删除物品失败');
    }
}