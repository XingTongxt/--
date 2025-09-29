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

        if (!res.ok) { // 处理 401/500 等错误
            const text = await res.text(); // 直接读取文本，避免 JSON 解析错误
            document.getElementById("msg").innerText = text || "登录失败";
            return;
        }

        const data = await res.json(); // 仅在 200 OK 时解析 JSON
        localStorage.setItem("token", data.token); // 保存 token
        window.location.href = "shop.html"; // 跳转到商店

    } catch (err) {
        console.error("请求出错:", err);
        document.getElementById("msg").innerText = "服务器错误";
    }
}
