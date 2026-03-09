import { reactive, getCurrentInstance } from 'vue';

export function useAdminLogin() {
  const state = reactive({
    username: "",
    password: "",
    msg: "",
    loading: false
  });

  const internalInstance = getCurrentInstance();
  const router = internalInstance?.proxy.$router;

  async function login() {
    state.msg = "";
    if (!state.username || !state.password) {
      state.msg = "请输入用户名和密码";
      return;
    }

    state.loading = true;
    try {
      const res = await fetch("http://localhost:8080/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: state.username,
          password: state.password
        })
      });

      // 无论成功或失败都解析 JSON
      const data = await res.json();

      if (res.ok && data.token) {
        localStorage.setItem("token", data.token);
        if (router) router.push("/admin/dashboard");
      } else {
        state.msg = data.msg || "用户名或密码错误";
      }
    } catch (err) {
      console.error("请求出错:", err);
      state.msg = "服务器错误";
    } finally {
      state.loading = false;
    }
  }

  return { state, login };
}