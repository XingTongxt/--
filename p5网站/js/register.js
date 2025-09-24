const registerBtn = document.getElementById('register-btn');
registerBtn.addEventListener('click', () => {
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const password2 = document.getElementById('reg-password2').value;
    if (!username || !password || !password2) {
        alert('请完整填写注册信息');
        return;
    }
    if (password !== password2) {
        alert('两次密码不一致');
        return;
    }
    // 调用后端注册接口或保存数据
    alert(`注册成功: ${username}`);
    // 注册成功后跳转到登录
    window.location.href = 'login.html';
});