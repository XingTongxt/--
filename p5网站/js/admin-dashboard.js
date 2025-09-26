async function loadAdminInfo() {
    const token = localStorage.getItem("adminToken");
    if (!token) {
        alert("请先登录");
        window.location.href = "admin.html";
        return;
    }

    try {
        const res = await fetch("http://localhost:8080/admin/info", {
            method: "GET",
            headers: { "Authorization": "Bearer " + token }
        });

        const data = await res.json();
        if (res.ok) {
            document.getElementById("admin-info").innerText =
                "管理员用户名：" + data.username;
        } else {
            document.getElementById("admin-info").innerText = data.msg || "获取信息失败";
        }
    } catch (err) {
        console.error("请求出错:", err);
        document.getElementById("admin-info").innerText = "服务器错误";
    }
}

function logout() {
    localStorage.removeItem("adminToken");
    window.location.href = "admin.html";
}

function goTo(page) {
    window.location.href = page;
}

// 页面加载时获取管理员信息
loadAdminInfo();

function goTo(page) {
    // 简单动画效果：闪烁菜单高亮
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(item => item.style.opacity = '0.6');
    
    // 点击的菜单恢复高亮
    event.currentTarget.style.opacity = '1';
    
    setTimeout(() => {
        window.location.href = page;
    }, 200); // 0.2秒动画后跳转
}

function logout() {
    if(confirm("确定要退出登录吗？")) {
        // 可以清除 token 或 cookie
        window.location.href = "login.html";
    }
}
