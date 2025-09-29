const token = localStorage.getItem('token');
        const itemTableBody = document.querySelector('#item-table tbody');
        const form = document.getElementById('add-item-form');

        async function loadItems() {
            try {
                const res = await fetch('http://localhost:8080/admin/items', {
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!res.ok) throw new Error('获取物品失败');
                const items = await res.json();
                renderItems(items);
            } catch (err) {
                alert(err.message);
                console.error(err);
            }
        }

        function renderItems(items) {
            itemTableBody.innerHTML = '';
            items.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${item.id}</td>
                    <td>${item.name}</td>
                    <td>${item.price}</td>
                    <td>${item.category}</td>
                    <td><img src="${item.img}" alt="${item.name}" width="50"></td>
                    <td>
                        <button onclick="editItem(${item.id})">修改</button>
                        <button onclick="deleteItem(${item.id})">删除</button>
                    </td>
                `;
                itemTableBody.appendChild(tr);
            });
        }

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value.trim();
            const price = parseFloat(document.getElementById('price').value);
            const category = document.getElementById('category').value.trim();
            const img = document.getElementById('img').value.trim();

            if (!name || !price || !category || !img) return alert('请完整填写信息');

            try {
                const res = await fetch('http://localhost:8080/admin/items', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({ name, price, category, img })
                });
                if (!res.ok) throw new Error('新增失败');
                alert('新增成功');
                form.reset();
                loadItems();
            } catch (err) {
                alert(err.message);
            }
        });

        async function editItem(id) {
            const name = prompt('请输入新名称:');
            if (name === null) return;
            const price = parseFloat(prompt('请输入新价格:'));
            if (isNaN(price)) return alert('价格无效');
            const category = prompt('请输入新分类:');
            if (category === null) return;
            const img = prompt('请输入新图片URL:');
            if (img === null) return;

            try {
                const res = await fetch(`http://localhost:8080/admin/items/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({ name, price, category, img })
                });
                if (!res.ok) throw new Error('修改失败');
                alert('修改成功');
                loadItems();
            } catch (err) {
                alert(err.message);
            }
        }

        async function deleteItem(id) {
            if (!confirm('确定删除该物品吗？')) return;
            try {
                const res = await fetch(`http://localhost:8080/admin/items/${id}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': 'Bearer ' + token }
                });
                if (!res.ok) throw new Error('删除失败');
                alert('删除成功');
                loadItems();
            } catch (err) {
                alert(err.message);
            }
        }

        loadItems();