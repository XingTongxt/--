
async function login() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!username || !password) {
        return alert("请输入用户名和密码");
    }

    try {
        const res = await fetch("http://localhost:8080/admin/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();
        if (data.code === 200) {
            localStorage.setItem("adminToken", data.token); // 保存 token
            window.location.href = "admin-dashboard.html"; // 跳转到后台主页
        } else {
            document.getElementById("msg").innerText = data.msg;
        }

    } catch (err) {
        console.error("请求出错:", err);
        document.getElementById("msg").innerText = "服务器错误";
    }
}