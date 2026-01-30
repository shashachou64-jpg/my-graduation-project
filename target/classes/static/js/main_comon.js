//==========================================
//页面初始化
//==========================================
document.addEventListener('DOMContentLoaded', () => {
    /**
     * 初始化课程
     */
    //初始化文件验证功能
    initializeFileValidation()

    //初始化课程添加功能
    initializeCourseAdd()

    //初始化课程列表
    renderCourseList()

    //初始化课程搜索功能
    initializeCourseSearch()

    //初始化课程删除功能
    initializeCourseDelete()

    //初始化课程修改功能
    initializeCourseEdit()

    /**
     * 初始化个人信息
     */

    
    initializeProfile()

    initializeEditProfile()

    initializeEditPassword()

    /**
     * 初始化学生
     */

    initializeAddStudent()

    initializeStudentList()

    initializeStudentSearch()

    initializeStudentEdit()

    initializeStudentDelete()

    //初始化导入功能
    initializeStudentImport()
})


//==============================
// 显示提示框
//==============================
/**
 * 显示危险提示框
 * @param {string} message 提示内容
 * @returns {void}
 */
function showAlert(message) {
    const alertEl = document.getElementById('alert-DangerB');
    if (!alertEl) return;

    alertEl.textContent = message;

    // 1. 先设为 block，此时 opacity 仍为 0
    alertEl.style.display = 'block';

    // 2. 稍微延迟一点点，让浏览器渲染完 display:block 后再改 opacity，触发动画
    setTimeout(() => {
        alertEl.style.opacity = '1';
    }, 10);

    // 2秒后开始隐藏逻辑
    setTimeout(() => {
        alertEl.style.opacity = '0';

        // 等透明度动画结束后，彻底隐藏
        setTimeout(() => {
            alertEl.style.display = 'none';
        }, 300);
    }, 4000);
}

/**
 * 显示成功提示框
 * @param {string} message 提示内容
 * @returns {void}
 */
function showSuccessAlert(message) {
    const alertEl = document.getElementById('alert-SuccessMessage');
    if (!alertEl) return;

    alertEl.textContent = message;

    // 1. 先设为 block，此时 opacity 仍为 0
    alertEl.style.display = 'block';

    // 2. 稍微延迟一点点，让浏览器渲染完 display:block 后再改 opacity，触发动画
    setTimeout(() => {
        alertEl.style.opacity = '1';
    }, 10);

    // 2秒后开始隐藏逻辑
    setTimeout(() => {
        alertEl.style.opacity = '0';

        // 等透明度动画结束后，彻底隐藏
        setTimeout(() => {
            alertEl.style.display = 'none';
        }, 300);
    }, 4000);
}


/**
 * 加载年份列表
 */
function loadAddYearList(YearDom) {
    if (YearDom.options.length > 1) return // 已经加载过了

    const currentYear = new Date().getFullYear()
    
    // 生成从2010年到今年的选项
    for (let year = currentYear-1; year >= 2022; year--) {
        const opt = document.createElement('option')
        opt.value = year
        opt.textContent = year
        YearDom.appendChild(opt)
    }
}

/**
 * 显示错误信息
 * @param {string} message 错误信息
 * @returns {void}
 */
const batchImportModal = document.getElementById('batchImportModal')
let batchImportModalInstance = null

/**
 * 显示批量导入模态框
 * @returns {void}
 */
function showBatchImportModal(message) {
    const batchImportDetail = document.getElementById('batchImportDetail')
    batchImportDetail.textContent = message
    batchImportModalInstance = new bootstrap.Modal(batchImportModal)
    batchImportModalInstance.show()

    document.getElementById('batchImportBtn').addEventListener('click', () => {
        batchImportModalInstance.hide()
    })
}