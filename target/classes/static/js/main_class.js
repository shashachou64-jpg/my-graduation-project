const classCollegeSelect = document.getElementById('classCollegeAddSelect')
const addModal = document.getElementById('courseModal')
const editModal = document.getElementById('courseEditModal')




// 导入文件函数
// ==========================================
// Excel文件验证相关函数
// ==========================================

/**
 * 验证Excel文件
 * @param {File} file - 文件对象
 * @returns {Object} 验证结果
 */
function validateExcelFile(file) {
    // 支持的MIME类型
    const validMimeTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
        'application/vnd.ms-excel' // .xls
    ];

    // 支持的文件扩展名
    const validExtensions = ['.xlsx', '.xls'];

    // 获取文件扩展名
    const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'));

    // 验证MIME类型和扩展名
    const isValidMimeType = validMimeTypes.includes(file.type);
    const isValidExtension = validExtensions.includes(fileExtension);

    // 文件有效性检查
    const isValid = isValidMimeType || isValidExtension;

    return {
        isValid: isValid,
        mimeType: file.type,
        extension: fileExtension,
        fileName: file.name,
        fileSize: file.size,
        sizeInKB: (file.size / 1024).toFixed(2)
    };
}

/**
 * 显示文件验证结果
 * @param {Object} validation - 验证结果
 */
function showFileValidationResult(validation) {
    const validationMsg = document.getElementById('file-validation-msg');
    const fileInfo = document.getElementById('file-info');
    const submitBtn = document.getElementById('class-add-btn');

    // 隐藏之前的消息
    validationMsg.style.display = 'none';
    fileInfo.style.display = 'none';

    if (validation.isValid) {
        // 文件有效 - 显示文件信息
        fileInfo.innerHTML = `
            <strong>✓ 文件验证成功</strong><br>
            <small>
                名称: ${validation.fileName}<br>
                大小: ${validation.sizeInKB} KB<br>
                类型: ${validation.mimeType || '未知'}
            </small>
        `;
        fileInfo.style.display = 'block';

        // 启用提交按钮
        submitBtn.disabled = false;
        submitBtn.innerHTML = '完成';

    } else {
        // 文件无效 - 显示错误信息
        validationMsg.innerHTML = `
            <strong>✗ 文件格式错误</strong><br>
            <small>
                请上传有效的Excel文件 (.xlsx 或 .xls)<br>
                当前文件: ${validation.fileName}<br>
                检测类型: ${validation.extension}
            </small>
        `;
        validationMsg.style.display = 'block';

        // 禁用提交按钮
        submitBtn.disabled = true;
        submitBtn.innerHTML = '请先选择有效文件';
    }
}

/**
 * 文件大小格式化
 * @param {number} bytes 
 * @returns {string}
 */
function formatFileSize(bytes) {
    if (bytes === 0) {
        return '0 B';
    }
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}



/**
 * 初始化文件验证功能
 */
function initializeFileValidation() {
    const fileInput = document.getElementById('classStuListInput')

    if (!fileInput) {
        console.error('文件输入框不存在')
        return
    }

    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0]

        //没有选择文件,重置状态
        if (!file) {
            resetFileValidation()
            return
        }

        //验证文件
        const validation = validateExcelFile(file)
        showFileValidationResult(validation)

        // 如果文件有效，使用XLSX读取Excel内容
        if (validation.isValid) {
            const reader = new FileReader()

            reader.onload = function (event) {
                try {
                    const data = new Uint8Array(event.target.result)
                    const workbook = XLSX.read(data, { type: 'array' })
                    const firstSheetName = workbook.SheetNames[0]
                    const worksheet = workbook.Sheets[firstSheetName]
                    // 转换成json格式
                    const stuData = XLSX.utils.sheet_to_json(worksheet)

                    console.log('Excel读取成功:', stuData)
                    console.log('共读取 ' + stuData.length + ' 条学生记录')

                    // 将学生数据存储到全局变量
                    window.stuList = stuData
                    window.stuNum = stuData.length

                    // 可以在这里将数据发送到后端或做其他处理

                } catch (error) {
                    console.error('读取Excel文件失败:', error)
                    alert('读取Excel文件失败: ' + error.message)
                }
            }

            reader.onerror = function (error) {
                console.error('FileReader错误:', error)
            }

            reader.readAsArrayBuffer(file)
        }
    })


}

//==========================================
//模态框相关
//==========================================

addModal.addEventListener('show.bs.modal', () => {
    //加载院系列表
    loadCollegeList()

    //重置文件验证状态
    resetFileValidation()

    //清空表单
    clearForm()
})

/**
 * 加载院系列表 
 */
function loadCollegeList() {
    if (classCollegeSelect.options.length > 1) return //已经加载过了

    axios({
        url: '/college/list',
        method: 'get',
    }).then((result) => {
        // console.log(result)

        if (result.data && result.data.data) {
            const colleges = result.data.data
            // 清空现有选项
            while (classCollegeSelect.options.length > 1) {
                classCollegeSelect.remove(1)
            }
            // 添加新选项
            colleges.forEach(college => {
                const opt = document.createElement('option')
                opt.value = college.id
                opt.textContent = college.name
                classCollegeSelect.appendChild(opt)

            });

        }
    }).catch((err) => {
        console.error('获取院系列表失败', err);
        alert('获取院系列表失败，请刷新页面重试');
    });
}

/**
 * 清空表单
 */
function clearForm() {
    document.getElementById('classNumberEditInput').value = ''
    document.getElementById('classCollegeAddSelect').value = ''
    document.getElementById('courseEditInput').value = ''
    document.getElementById('classGenderEditInput').value = ''
    document.getElementById('classStuListInput').value = ''
}

//==========================================
//课程添加功能
//==========================================

/**
 * 初始化课程添加功能
 */
function initializeCourseAdd() {
    const addBtn = document.getElementById('class-add-btn')
    if (!addBtn) {
        console.error('添加按钮不存在')
        return
    }

    addBtn.addEventListener('click', handleAddCourse)
}

/**
 *处理添加课程 
 */
async function handleAddCourse() {
    try {
        //获取表单数据
        const courseData = getFormData()
        console.log(courseData)
        if (!courseData) {
            return
        }

        //显示加载状态
        const submitBtn = document.getElementById('class-add-btn')
        const originalText = submitBtn.innerHTML
        submitBtn.disabled = true
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> 上传中...'

        const result = await axios({
            url: '/course/addCourseWithStudents',
            method: 'post',
            data: courseData,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })

        if (result.data && result.data.code === 0) {
            alert('课程添加成功！');
            console.log(result)
            // 关闭模态框
            const modal = bootstrap.Modal.getInstance(addModal);
            modal.hide();
            // 刷新课程列表
            renderCourseList()
        } else {
            console.log(result.data)
            alert(result.data.message || '添加失败');
        }
    } catch (error) {
        console.log(error)
        console.error('添加课程失败:', error);
        alert('添加课程失败，请重试');
    } finally {
        // 恢复按钮状态
        const submitBtn = document.getElementById('class-add-btn');
        submitBtn.disabled = false;
        submitBtn.innerHTML = '完成';
    }
}

/**
 * 获取表单数据
 * @returns {FormData|null} 表单数据
 */
function getFormData() {
    const courseName = document.getElementById('classNumberEditInput').value.trim();
    const collegeId = document.getElementById('classCollegeAddSelect').value;
    const credit = document.getElementById('courseEditInput').value;
    const maxNum = document.getElementById('classGenderEditInput').value;
    const fileInput = document.getElementById('classStuListInput');

    // 验证必填字段
    if (!courseName) {
        alert('请输入课程名称');
        return null;
    }

    if (!collegeId) {
        alert('请选择院系');
        return null;
    }

    if (!credit || credit <= 0) {
        alert('请输入有效的学分');
        return null;
    }

    if (!maxNum || maxNum <= 0) {
        alert('请输入有效的班级容量');
        return null;
    }

    // 验证文件
    if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
        alert('请选择学生名单文件');
        return null;
    }

    const file = fileInput.files[0];
    const validation = validateExcelFile(file);

    if (!validation.isValid) {
        alert('请选择有效的Excel文件');
        return null;
    }

    //学院名称
    const collegeName = document.getElementById('classCollegeAddSelect').options[document.getElementById('classCollegeAddSelect').selectedIndex].text;

    // 从全局变量获取学生数据
    let studentNumbers = [];

    if (window.stuList && Array.isArray(window.stuList)) {
        // 假设Excel中有number或学号字段
        studentNumbers = window.stuList.map(student => {
            return student.number || student.学号 || student.studentNumber || student.学号 || '';
        }).filter(num => num); // 过滤空值
    }
    //学生人数
    const studentNum = studentNumbers.length

    // 构建课程对象
    const course = {
        courseName: courseName,
        credit: parseInt(credit),
        maxNum: parseInt(maxNum),
        collegeId: parseInt(collegeId),
        currentNum: studentNum
    };

    return {
        course: course,
        collegeName: collegeName,
        studentsNumberList: studentNumbers
    }
}

/**
 * 重置文件验证状态
 */
function resetFileValidation() {
    const validationMsg = document.getElementById('file-validation-msg');
    const fileInfo = document.getElementById('file-info');
    const submitBtn = document.getElementById('class-add-btn');

    // 隐藏消息
    validationMsg.style.display = 'none';
    fileInfo.style.display = 'none';

    // 禁用提交按钮
    submitBtn.disabled = true;
    submitBtn.innerHTML = '请先选择文件';
}
// 课程详情跳转
function goCourseDetail(courseId) {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('身份已过期，请重新登录');
        window.location.href = 'index.html';
        return;
    }
    // 直接跳转，不要指望这一步能传 token 给后端拦截器
    location.href = `class_detail.html?courseId=${courseId}`;
}

//==========================================
//课程列表功能
//==========================================

/**
 * 获取课程列表
 */
async function getCourseList(params = {}) {
    try {
        const response = await axios({
            url: '/course/ListCourse',
            method: 'get',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })

        if (response.data && response.data.code === 0) {
            return response.data.data || [];
        }

    } catch (error) {
        console.error('获取课程列表失败:', error);
        return [];
    }
}

/**
 * 渲染课程列表课程
 */
async function renderCourseList() {
    const tbody = document.getElementById('class-form-body');
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }

    //清空表格
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">加载中...</td></tr>'

    try {
        const courseList = await getCourseList()

        if (!courseList || courseList.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">暂无课程数据</td></tr>';
            return;
        }

        let html = ''
        courseList.forEach((course, index) => {
            let studentsHtml = '-'
            if (course.studentInfoList && course.studentInfoList.length > 0) {
                const studentNames = course.studentInfoList.map(s => s.name).join('、')
                studentsHtml = `<a href="/class_detail.html?courseId=${course.id}" title="${studentNames}">${course.studentInfoList.length}</a>`
            }

            html += `
            <tr class="class-form-item" data-id="${course.id}">
                <td>${index + 1}</td>
                <td>${course.courseName}</td>
                <td>${course.collegeName}</td>
                <td>${course.credit}</td>
                <td>${course.maxNum}</td>
                <td>${studentsHtml}</td>
                <td>
                    <button type="button" class="btn btn-primary class-edit-btn">修改</button>
                    <button type="button" class="btn btn-danger class-delete-btn">删除</button>
                </td>
            </tr>
            `
        })
        tbody.innerHTML = html
    } catch (error) {
        console.error('渲染课程列表失败:', error);
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">加载失败，请刷新页面重试</td></tr>'
        return
    }
}

//==========================================
//课程搜索相关功能
//==========================================

/**
 * 初始化课程搜索功能
 */
function initializeCourseSearch() {
    const searchBtn = document.getElementById('classSearchBtn')
    const searchInput = document.getElementById('classSearchInput')
    const resetBtn = document.getElementById('classResetBtn')
    if (!resetBtn) {
        console.error('重置按钮不存在')
        return
    }
    if (!searchInput) {
        console.error('搜索输入框不存在')
        return
    }
    if (!searchBtn) {
        console.error('搜索按钮不存在')
        return
    }
    searchInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            handleSearchCourse()
        }
    })

    searchInput.addEventListener('input', () => {
        if (searchInput.value.trim() === '') {
            resetCourseSearch()
        }
        handleSearchCourse()
    })
    resetBtn.addEventListener('click', resetCourseSearch)
    searchBtn.addEventListener('click', handleSearchCourse)
}

/**
 * 搜索课程
 * 按学院名称或课程名称搜索
 * 如果学院名称和课程名称都为空，则搜索所有课程
 */
async function handleSearchCourse() {
    const SearchType = document.getElementById('classSearchSelect').value
    const searchInput = document.getElementById('classSearchInput')
    const keyword = searchInput ? searchInput.value.trim() : ''
    const tbody = document.getElementById('class-form-body')
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }

    if (!SearchType) {
        alert('请选择搜索类型和输入搜索内容')
        return
    }

    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">搜索中...</td></tr>'
    //按照搜索的属性进行搜索
    const courseList = await searchCourse(SearchType, keyword)

    //渲染课程列表
    renderCourseListBySearch(courseList)

}

/**
 * 重置搜索结果
 * @returns {Promise} 重置搜索结果
 * @returns {null} 重置失败
 */
async function resetCourseSearch() {
    const tbody = document.getElementById('class-form-body');
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">加载中...</td></tr>'
    const searchInput = document.getElementById('classSearchInput')
    if(searchInput.value.trim() != ''){
        searchInput.value = ''
    }
    renderCourseList()
}

/**
 * 得到搜索课程列表
 * @param {string} SearchType 搜索类型
 * @param {string} keyword 搜索关键词
 * @returns {Array} 课程列表
 * @returns {null} 搜索失败
 */
async function searchCourse(SearchType, keyword) {
    try {
        const response = await axios({
            url: '/course/search',
            method: 'get',
            params: {
                SearchType: SearchType,
                keyword: keyword
            }
        })
        if (response.data && response.data.code === 0) {
            return response.data.data || [];
        }
        return [];
    } catch (error) {
        console.error('搜索课程失败:', error);
        return [];
    }
}

/**
 * 渲染搜索课程列表
 * @param {Array} courseList 课程列表
 * @returns {null} 渲染失败
 * @returns 
 */
function renderCourseListBySearch(courseList) {
    const tbody = document.getElementById('class-form-body');
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }

    //清空表格
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">加载中...</td></tr>'

    let html = ''
    courseList.forEach((course, index) => {
        let studentsHtml = '-'
        if (course.studentInfoList && course.studentInfoList.length > 0) {
            const studentNames = course.studentInfoList.map(s => s.name).join('、')
            studentsHtml = `<a href="/course_detail.html?courseId=${course.id}" title="${studentNames}">${course.studentInfoList.length}</a>`
        }

        html += `
        <tr class="class-form-item" data-id="${course.id}">
            <td>${index + 1}</td>
            <td>${course.courseName}</td>
            <td>${course.collegeName}</td>
            <td>${course.credit}</td>
            <td>${course.maxNum}</td>
            <td>${studentsHtml}</td>
            <td>
                <button type="button" class="btn btn-primary class-edit-btn">修改</button>
                <button type="button" class="btn btn-danger class-delete-btn">删除</button>
            </td>
        </tr>
    `
    })
    tbody.innerHTML = html
}

//==========================================
//课程删除功能
//==========================================

/**
 * 初始化课程删除功能
 */
function initializeCourseDelete() {
    const tbody = document.getElementById('class-form-body');
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }
    tbody.addEventListener('click', (event) => {
        if (event.target.classList.contains('class-delete-btn')) {
            const row = event.target.closest('tr');
            const courseId = row.dataset.id;  // 从data-id获取ID
            const courseName = row.querySelector('td:nth-child(2)').textContent;
            console.log(courseId, courseName);
            handleCourseDelete(courseId, courseName);
        }
    })
}

/**
 * 删除课程
 * @param {number} courseId 课程ID
 * @param {string} courseName 课程名称
 * @returns {Promise} 删除结果
 * @returns {null} 删除失败
 */
async function handleCourseDelete(courseId, courseName) {
    if (!confirm(`确定要删除课程"${courseName}"吗？`)) {
        return;
    }
    try {
        const response = await axios({
            url: '/course/delete',
            method: 'delete',
            params: {
                courseId: courseId
            },
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })
        if (response.data && response.data.code === 0) {
            alert('课程删除成功！');
            renderCourseList()
        } else {
            alert(response.data.message || '删除失败');
        }
    } catch (error) {
        console.error('删除课程失败:', error);
        alert('删除课程失败，请重试');
    }
}

//==========================================
//课程修改功能
//==========================================

/**
 * 初始化课程修改功能
 * @returns {null} 初始化失败
 * @returns {null} 初始化成功
 */
function initializeCourseEdit() {
    const tbody = document.getElementById('class-form-body')
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }

    tbody.addEventListener('click', (e) => {
        if (e.target.classList.contains('class-edit-btn')) {
            const row = e.target.closest('tr')
            const courseId = row.dataset.id  // 从data-id获取ID

            //打开修改模态框
            openEditModal(courseId)
        }

        // 保存按钮绑定事件
        const saveBtn = document.getElementById('courseEditSaveBtn')
        if (saveBtn) {
            saveBtn.addEventListener('click', handleSaveCourseEdit)

            //绑定回车事件
            saveBtn.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    handleSaveCourseEdit()
                }
            })
        }
    })
}

/**
 * 打开修改模态框
 * @param {number} courseId 课程ID
 * @returns {Promise} 打开模态框结果
 * @returns {null} 打开模态框失败
 */
async function openEditModal(courseId) {
    try {
        const response = await axios({
            url: '/course/getCourseById',
            params: {
                courseId
            },
            headers: {
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })

        //填充模态框数据
        if (response.data && response.data.code === 0) {
            const course = response.data.data
            document.getElementById('courseEditNameInput').value = course.courseName
            document.getElementById('courseEditId').value = course.id
            document.getElementById('courseEditCreditInput').value = course.credit
            document.getElementById('courseEditMaxNumInput').value = course.maxNum
            document.getElementById('courseEditCollegeId').value = course.collegeId
            //加载院系列表并设置选中项
            await loadCollegeListForEdit(course.collegeId)

            //显示模态框
            const modal = new bootstrap.Modal(editModal)
            modal.show()
        } else {
            alert(response.data.message || '获取课程详情失败')
        }
    } catch (error) {
        console.error('获取课程详情失败:', error);
        alert('获取课程详情失败，请重试')
    }
}

/**
 * 加载院系列表（用于修改模态框）
 * @param {number} selectedCollegeId - 当前选中的院系ID
 */

async function loadCollegeListForEdit(selectedCollegeId) {
    const select = document.getElementById('courseEditCollegeSelect')

    try {
        const response = await axios({
            url: '/college/list',
            method: 'get',
            headers: {
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })

        if (response.data && response.data.code === 0) {
            const colleges = response.data.data;

            // 清空现有选项（保留第一个）
            while (select.options.length > 1) {
                select.remove(1);
            }

            // 添加院系选项
            colleges.forEach(college => {
                const option = document.createElement('option');
                option.value = college.id;
                option.textContent = college.name;

                if (college.id === selectedCollegeId) {
                    option.selected = true;
                }

                select.appendChild(option);
            })
        }
    } catch (error) {
        console.error('获取院系列表失败:', error);
    }
}   

/**
 * 保存课程修改信息
 * @returns {Promise} 保存课程修改信息结果
 * @returns {null} 保存课程修改信息失败
 */
async function handleSaveCourseEdit() {

    //获取参数
    const id = document.getElementById('courseEditId').value
    const courseName = document.getElementById('courseEditNameInput').value
    const collegeId = document.getElementById('courseEditCollegeId').value
    const credit = document.getElementById('courseEditCreditInput').value
    const maxNum = document.getElementById('courseEditMaxNumInput').value
    
    if(!courseName){
        console.error('课程名称不能为空')
        return
    }
    if(!collegeId){
        console.error('学院不能为空')
        return
    }
    if(!credit){
        console.error('学分不能为空')
        return
    }
    if(!maxNum){
        console.error('最大人数不能为空')
        return
    }

    const course = {
        id: id,
        courseName: courseName,
        collegeId: collegeId,
        credit: credit,
        maxNum: maxNum
    }

    try {
        const response = await updatingCourseInfo(course)
        if(response && response.code === 0){
            renderCourseList()
            //关闭模态框
            const modal = bootstrap.Modal.getInstance(editModal);
            modal.hide();
        } else if(response && response.code === 1){
            alert(response.message || '修改失败')
        } else {
            alert('修改失败')
        }
    } catch (error) {
        console.error('更新课程信息失败:', error);
    }
    
}

/**
 * 更新课程信息
 * @param {Object} course 课程信息
 * @returns {Object} 更新课程信息结果
 * @returns {null} 更新课程信息失败
 */
async function updatingCourseInfo(course) {
    try {
        const response = await axios({
            url: '/course/updatingCourseInfo',
            method: 'post',
            data: course,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + (localStorage.getItem('token') || '')
            }
        })
        return response.data
        
    } catch (error) {
        console.error('更新课程信息失败:', error);
        return null
    }
}