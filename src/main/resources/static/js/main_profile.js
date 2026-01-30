//==============================
// 全局变量
//==============================
const editProfileBtn = document.getElementById('editProfileBtn')
const editProfileModal = document.getElementById('editProfileModal')
let profileModalInstance = null  // 保存模态框实例

const editPasswordModal = document.getElementById('editPasswordModal')
let passwordModalInstance = null  // 保存模态框实例

const token = localStorage.getItem('token')

//==============================
// 页面初始化
//==============================

document.addEventListener('DOMContentLoaded', () => {

})



//==============================
// 获取个人信息
//==============================

/**
 * 初始化个人信息页面
 * @returns
 */
async function initializeProfile() {
    const profileContainer = document.getElementById('profileContainer')
    if (!profileContainer) {
        console.error('获取个人信息失败')
        return
    }
    //获取个人信息数据
    const response = await getProfileInfo()
    if (response) {
        //个人信息数据渲染
        renderProfileInfo(response.data)
    } else {
        console.error('获取个人信息失败')
    }

}

/**
 * 获取个人信息
 * @returns {Promise<Object>} 个人信息数据
 * @returns
 */
async function getProfileInfo() {
    try {
        const response = await axios({
            url: '/user/profile',
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('token')
            }
        })
        return response.data
    } catch (error) {
        console.error('获取个人信息失败:', error)
        return null
    }
}

/**
 * 渲染个人信息
 * @param {Object} data 个人信息数据
 * @returns {void}
 */
function renderProfileInfo(data) {
    const username = document.getElementById('profileUserName')
    const name = document.getElementById('profileName')
    const sex = document.getElementById('profileSex')
    const identityName = document.getElementById('profileRole')
    const phone = document.getElementById('profilePhone')
    const email = document.getElementById('profileEmail')
    const address = document.getElementById('profileAddress')
    const userMessage = document.getElementById('userMessage')



    username.textContent = data.username
    name.textContent = data.name
    sex.textContent = data.sex
    identityName.textContent = data.identityName
    phone.textContent = data.phone
    email.textContent = data.email
    address.textContent = data.address

    userMessage.textContent = '你好，' + data.name
}


//==============================
// 修改个人信息
//==============================

/**
 * 初始化
 * @returns {void}
 */
function initializeEditProfile() {
    if (!editProfileBtn) {
        console.error('修改个人信息按钮不存在')
        return
    }
    if (!editProfileModal) {
        console.error('修改个人信息模态框不存在')
        return
    }

    // 只创建一次模态框实例
    profileModalInstance = new bootstrap.Modal(editProfileModal)

    // 保存按钮事件（只添加一次）
    const saveBtn = document.getElementById('saveProfileBtn')
    saveBtn.onclick = async () => {
        const response = await submitProfileChange()
        if (response && response.code === 0) {
            // 关闭模态框
            profileModalInstance.hide()
            // 刷新数据列表
            initializeProfile()
        }
    }

    editProfileBtn.addEventListener('click', () => {
        // 填充数据
        fillEditForm()
        // 模态框显示
        profileModalInstance.show()
    })
}

/**
 * 填充修改个人信息表单
 * @returns {Promise<void>}
 */
async function fillEditForm() {
    //获取个人信息数据
    const response = await getProfileInfo()
    if (response) {
        //个人信息数据渲染
        renderEditForm(response.data)
    } else {
        console.error('获取个人信息失败')
    }
}

/**
 * 渲染修改个人信息表单
 * @param {Object} data 个人信息数据
 * @returns {void}
 */
function renderEditForm(data) {
    const username = document.getElementById('editUsername')
    const name = document.getElementById('editName')
    const sex = document.getElementById('editSex')
    const phone = document.getElementById('editPhone')
    const email = document.getElementById('editEmail')
    const address = document.getElementById('editAddress')

    username.value = data.username
    name.value = data.name
    sex.value = data.sex
    phone.value = data.phone
    email.value = data.email
    address.value = data.address

    if (data.username === '未设置') {
        username.value = ''
    }
    if (data.sex === '未设置') {
        sex.value = ''
    }
    sex.value = data.sex
    if (data.sex === '未设置') {
        sex.value = ''
    }
    if (data.phone === '未设置') {
        phone.value = ''
    }
    if (data.email === '未设置') {
        email.value = ''
    }
    if (data.address === '未设置') {
        address.value = ''
    }
}

/**
 * 提交修改个人信息
 * @returns {Promise<void>}
 */
async function submitProfileChange() {
    //获取修改个人信息数据
    const username = document.getElementById('editUsername').value
    const name = document.getElementById('editName').value
    const sex = document.getElementById('editSex').value
    const phone = document.getElementById('editPhone').value
    const email = document.getElementById('editEmail').value
    const address = document.getElementById('editAddress').value

    //获取token
    const token = localStorage.getItem('token')
    const data = {
        username: username,
        name: name,
        sex: sex,
        phone: phone,
        email: email,
        address: address
    }

    //获取更新信息
    const response = await getUpdatePersonalInfo(token, data)

    if (response && response.code === 0) {
        return response
    } else {
        showAlert(response?.message || '更新失败')
        return null
    }
}

async function getUpdatePersonalInfo(token, data) {
    try {
        const response = await axios({
            url: `/personal/updateProfile/${token}`,
            method: 'PUT',
            data: data,
            headers: {
                'Authorization': token
            }
        })
        return response.data
    } catch (error) {
        console.error('更新个人信息失败:', error)
        return null
    }
}

//==============================
// 修改密码
//==============================

/**
 * 初始化修改密码
 * @returns {void}
 */
function initializeEditPassword() {
    const editPasswordBtn = document.getElementById('editPasswordBtn')
    if (!editPasswordBtn) {
        console.error('修改密码按钮不存在')
        return
    }


    editPasswordBtn.addEventListener('click', handleEditPassword)
}

/**
 * 修改密码按钮点击事件
 * @returns {void}
 */
function handleEditPassword() {
    try {
        //显示模态框
        passwordModalInstance = new bootstrap.Modal(editPasswordModal)
        passwordModalInstance.show()

        const savePasswordBtn = document.getElementById('savePasswordBtn')
        if (!savePasswordBtn) {
            console.error('更改密码按钮错误')
        }

        const confirmPassword = document.getElementById('confirmPassword')
        if (!confirmPassword) {
            console.error('确认新密码框获取失败')
        }
        const newPassword = document.getElementById('newPassword')
        if (!newPassword) {
            console.error('新密码框获取失败')
        }

        newPassword.addEventListener('input', confirmNewPasswordFormat)
        confirmPassword.addEventListener('input', confirmNewPassword)

        savePasswordBtn.addEventListener('click', handleSavePassword)


    } catch (error) {
        console.error('修改密码失败:', error)
        return null
    }
}

/**
 * 保存密码
 * @returns {Promise<void>}
 */
async function handleSavePassword() {
    try {
        const oldPassword = document.getElementById('oldPassword').value
        const newPassword = document.getElementById('newPassword').value
        const confirmPassword = document.getElementById('confirmPassword').value


        //逻辑判断
        if (oldPassword === '') {
            showAlert('请输入原本的密码')
            return
        }
        if (newPassword === '') {
            showAlert('新密码不能为空')
            return
        }
        if (confirmPassword === '') {
            showAlert('请再次确认密码')
            return
        }

        const response = await getEditPassword(oldPassword, newPassword, confirmPassword)


        /**
         * 判断修改密码是否成功
         * 如果成功，消除token，返回登录页，提示修改密码成功
         * 如果失败，提示修改密码失败
         */
        if (response && response.code === 0) {
            await showSuccessAlert(response.data + '，请返回登陆页面重新登录')
            localStorage.removeItem('token')
            window.location.href = '/index.html'
        }else{
            showAlert(response?.message || '修改密码失败，请重试')
        }
    } catch (error) {
        console.error('更改密码失败', error);
    }
}

/**
 * 确认新密码
 * @returns {void}
 */
function confirmNewPassword() {
    const newPassword = document.getElementById('newPassword').value
    const confirmPassword = document.getElementById('confirmPassword').value
    const passwordMismatchTip = document.getElementById('passwordMismatchTip')

    if (newPassword !== confirmPassword) {
        passwordMismatchTip.style.display = 'block'
    } else {
        passwordMismatchTip.style.display = 'none'
    }


}

/**
 * 确认新密码格式
 * @returns {void}
 */
function confirmNewPasswordFormat() {
    const newPassword = document.getElementById('newPassword').value
    const newPasswordFormat = document.getElementById('newPasswordFormat')
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/
    if (!passwordRegex.test(newPassword)) {
        newPasswordFormat.style.display = 'block'
    } else {
        newPasswordFormat.style.display = 'none'
    }
}

/**
 * 修改密码
 * @param {string} oldPassword 旧密码
 * @param {string} newPassword 新密码
 * @param {string} confirmPassword 确认密码
 * @returns {Promise<Object>} 修改密码结果
 */
async function getEditPassword(oldPassword, newPassword, confirmPassword) {
    const data = {
        oldPassword: oldPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    }
    try {
        const response = await axios({
            url: `/user/updatePassword/${token}`,
            method: 'put',
            data: data,
            headers: {
                'Authorization': token
            }
        })
        return response.data

    } catch (error) {
        console.error('修改密码接口调用失败', error)
        return null
    }
}