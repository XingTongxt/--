export async function registerUser(username, password, password2, router) {

    if (!username || !password || !password2) {
        alert("请填写完整信息")
        return
    }

    if (password !== password2) {
        alert("两次密码不一致")
        return
    }

    try {

        const res = await fetch("http://localhost:8080/api/user/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username,
                password
            })
        })

        const text = await res.text()

        if (res.ok) {

            alert("注册成功")

            if (router) {
                router.push("/login")
            }

        } else {
            alert(text)
        }

    } catch (err) {
        console.error(err)
        alert("注册失败，请检查后端")
    }

}