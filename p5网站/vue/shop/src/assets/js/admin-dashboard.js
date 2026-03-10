// ==================== 加载管理员信息 ====================
export const initAdminDashboard = async (adminInfoElement, router) => {

  const token = localStorage.getItem("token")

  if (!token) {
    alert("请先登录")
    router.push("/admin/login")
    return null
  }

  try {

    const res = await fetch("http://localhost:8080/admin/info", {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token
      }
    })

    const data = await res.json()

    if (res.ok) {

      adminInfoElement.innerText =
        "管理员用户名：" + data.username + " | 角色：" + data.role

      return data

    }

    else if (res.status === 401) {

      alert("登录已过期，请重新登录")
      localStorage.removeItem("token")
      router.push("/admin/login")
      return null

    }

    else {

      adminInfoElement.innerText = data.msg || "获取信息失败"
      return null

    }

  } catch (err) {

    console.error(err)
    adminInfoElement.innerText = "服务器错误"
    return null

  }

}


// ==================== 菜单高亮 ====================
export const highlightMenu = (event) => {

  const menuItems = document.querySelectorAll(".menu-item")

  menuItems.forEach(item => {
    item.style.opacity = 0.6
  })

  if (event) {
    event.currentTarget.style.opacity = 1
  }

}


// ==================== 退出登录 ====================
export const adminLogout = (router) => {

  if (confirm("确定要退出登录吗？")) {

    localStorage.removeItem("token")
    localStorage.removeItem("adminRole")

    router.push("/login")

  }

}