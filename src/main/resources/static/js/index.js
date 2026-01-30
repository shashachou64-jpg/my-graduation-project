
//===========================
// 初始化
//===========================
document.addEventListener('DOMContentLoaded', () => {
    //初始化登陆功能
    initializeLogin()
})


//===========================
// 登录功能
//===========================

function initializeLogin(){
    const loginBtn = document.getElementById('loginToLoginBtn')
    const username = document.getElementById('loginUsername')
    const password = document.getElementById('loginPassword')
    // 点击事件
    loginBtn.addEventListener('click', () => {
        //登陆函数
        login()
    })
    //回车事件
    username.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            login()
        }
    })

    password.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            login()
        }
    })
}

async function login() {
    const username = document.getElementById('loginUsername').value
    const password = document.getElementById('loginPassword').value

    const response = await getLoginInfo(username, password)
    
    //页面跳转事件
    loadHomePage(response)
}

async function getLoginInfo(username, password) {
    try {
        const response = await axios({
            url: '/user/login',
            method: 'POST',
            data: {
                username: username,
                password: password
            },
            headers: {
                'Content-Type': 'application/json'  
            }
        })
        return response.data
    } catch (error) {
        console.error('登录失败:', error)
    }
}

function loadHomePage(response) {
    if(response.code === 0){
        // 保存用户信息到 localStorage（不要存密码！）
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('username', response.data.username)

        if(response.data.identityName==='学生'){
            window.location.href = '/content/TKKCstudent.html'
        }
        if(response.data.identityName==='教师'){
            window.location.href = '/content/TKKCteacher.html'
        }
        if(response.data.identityName==='管理员'){
            window.location.href = '/content/main.html'
        }
    } else {
        // 登录失败，显示提示
        alert('登录失败：' + response.msg)
    }
}