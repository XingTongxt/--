export function initLogin(router) {
    const loginBtn = document.getElementById('login-btn')
    if (!loginBtn) return

    loginBtn.addEventListener('click', async () => {
        const username = document.getElementById('username').value.trim()
        const password = document.getElementById('password').value.trim()

        if (!username || !password) return alert('请输入用户名和密码')

        try {
            const res = await fetch('http://localhost:8080/api/user/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
                credentials: 'include'
            })
            const data = await res.json()
            if (res.ok) {
                localStorage.setItem('token', data.data.token)
                localStorage.setItem('username', data.data.username)
                if (router) {
                    router.push('/shop')
                } else {
                    window.location.href = '/shop'
                }
            } else {
                alert(data.error)
            }
        } catch (err) {
            console.error(err)
            alert('登录失败，请检查后端')
        }
    })
}