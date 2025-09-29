// 加载管理员信息
async function loadAdminInfo() {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("请先登录");
        window.location.href = "admin.html";
        return;
    }

    try {
        const res = await fetch("http://localhost:8080/admin/info", {
            method: "GET",
            headers: { 
                "Authorization": "Bearer " + token 
            }
        });

        // 如果返回不是 JSON，先获取文本
        let data;
        try {
            data = await res.json();
        } catch {
            const text = await res.text();
            throw new Error(text || "返回格式错误");
        }

        if (res.ok) {
            document.getElementById("admin-info").innerText =
                "管理员用户名：" + data.username;
        } else if (res.status === 401) {
            alert("登录已过期，请重新登录");
            localStorage.removeItem("token");
            window.location.href = "admin.html";
        } else {
            document.getElementById("admin-info").innerText = data.msg || "获取信息失败";
        }
    } catch (err) {
        console.error("请求出错:", err);
        document.getElementById("admin-info").innerText = "服务器错误";
    }
}

// 退出登录
function logout() {
    if (confirm("确定要退出登录吗？")) {
        localStorage.removeItem("token");
        window.location.href = "admin.html";
    }
}

// 菜单跳转
function goTo(page, event) {
    // 简单动画效果：闪烁菜单高亮
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(item => item.style.opacity = '0.6');

    if(event) {
        event.currentTarget.style.opacity = '1';
    }

    setTimeout(() => {
        window.location.href = page;
    }, 200); // 0.2秒动画后跳转
}

// 页面加载时获取管理员信息
loadAdminInfo();
