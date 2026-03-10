export async function fetchLogs(token, renderCallback) {
  if (!token) {
    alert("请先登录管理员账户");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/admin/logs", {
      headers: { "Authorization": "Bearer " + token }
    });

    if (!res.ok) {
      const text = await res.text();
      alert("请求失败: " + text);
      return;
    }

    const logs = await res.json();
    renderCallback(logs);
  } catch (err) {
    console.error(err);
    alert("网络或服务器错误");
  }
}

// 筛选函数
export function filterLogs(logs, type, username) {
  type = type.trim().toUpperCase();
  username = username.trim().toLowerCase();

  return logs.filter(log => {
    const rowType = log.type.toUpperCase();
    const rowUser = log.username.toLowerCase();
    const typeMatch = !type || rowType === type;
    const userMatch = !username || rowUser.includes(username);
    return typeMatch && userMatch;
  });
}