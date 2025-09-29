async function fetchLogs() {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("请先登录管理员账户");
        return;
    }

    try {
        const res = await fetch("http://localhost:8080/admin/logs", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!res.ok) {
            const text = await res.text();
            alert("请求失败: " + text);
            return;
        }

        const logs = await res.json();
        renderLogs(logs);
    } catch (err) {
        console.error(err);
        alert("网络或服务器错误");
    }
}

function renderLogs(logs) {
    const tbody = document.getElementById("logTableBody");
    tbody.innerHTML = "";

    logs.forEach(log => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${log.id}</td>
            <td>${log.username}</td>
            <td>${log.role}</td>
            <td>${log.action}</td>
            <td>${log.type}</td>
            <td>${log.time}</td>
        `;
        tbody.appendChild(tr);
    });
}

// 筛选功能
document.getElementById("filterBtn").addEventListener("click", () => {
    const type = document.getElementById("typeFilter").value.trim().toUpperCase();
    const username = document.getElementById("userFilter").value.trim().toLowerCase();

    const rows = Array.from(document.querySelectorAll("#logTableBody tr"));
    rows.forEach(row => {
        const rowType = row.children[4].textContent.trim().toUpperCase();
        const rowUser = row.children[1].textContent.trim().toLowerCase();
        const typeMatch = !type || rowType === type;
        const userMatch = !username || rowUser.includes(username);
        row.style.display = typeMatch && userMatch ? "" : "none";
    });
});

// 刷新按钮
document.getElementById("refreshBtn").addEventListener("click", fetchLogs);

// 页面加载时获取日志
window.addEventListener("DOMContentLoaded", fetchLogs);