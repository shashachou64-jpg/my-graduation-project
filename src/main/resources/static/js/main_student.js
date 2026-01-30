//==========================================
//全局变量
//==========================================

const addStudentModal = document.getElementById('addStudentModal')
let addStudentModalInstance = null


//==========================================
//逐个添加学生功能
//==========================================

/**
 * 初始化逐个添加学生功能
 */
function initializeAddStudent() {
    const addStudentBtn = document.getElementById('addStudentBtn')
    if (!addStudentBtn) {
        console.error('添加按钮不存在')
        return
    }

    addStudentBtn.addEventListener('click', handleAddStudent)

    //保存按钮点击事件
    const saveStudentBtn = document.getElementById('saveStudentBtn')
    if (!saveStudentBtn) {
        console.error('保存按钮不存在')
        return
    }

    saveStudentBtn.addEventListener('click', handleSaveStudent)

    //回车事件
    addStudentModal.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            handleSaveStudent()
        }
    })
}

function handleAddStudent() {
    //打开模态框函数
    openAddStudentModal()

}


function openAddStudentModal() {
    //清空表单
    clearStuAddForm()

    //加载院系列表
    loadAddStuCollegeList()

    //根据专业和年份加载班级列表
    const stuMajorSelect = document.getElementById('stuMajorSelect')
    const stuYearSelect = document.getElementById('stuYearSelect')
    const stuClassSelect = document.getElementById('stuClassSelect')
    
    stuMajorSelect.addEventListener('change', async () => {
        const majorId = stuMajorSelect.value
        const year = stuYearSelect.value
        const classes = await getClsByMajorId(majorId, year)
        // 填充班级下拉框
        stuClassSelect.innerHTML = '<option value="">请选择班级</option>'
        classes.forEach(cls => {
            const opt = document.createElement('option')
            opt.value = cls.id
            opt.textContent = cls.name
            stuClassSelect.appendChild(opt)
        })
    })
    
    stuYearSelect.addEventListener('change', async () => {
        const year = stuYearSelect.value
        const majorId = stuMajorSelect.value
        const classes = await getClsByMajorId(majorId, year)
        // 填充班级下拉框
        stuClassSelect.innerHTML = '<option value="">请选择班级</option>'
        classes.forEach(cls => {
            const opt = document.createElement('option')
            opt.value = cls.id
            opt.textContent = cls.name
            stuClassSelect.appendChild(opt)
        })
    })

    //加载年份列表
    const YearDom = document.getElementById('stuYearSelect')
    if (YearDom) {
        loadAddYearList(YearDom)
    } else {
        console.error("生成年份表单表单失败")
    }
    //动态加载专业列表
    const stuCollegeSelect = document.getElementById('stuCollegeSelect')
    if (stuCollegeSelect) {
        stuCollegeSelect.addEventListener('change', function () {
            const collegeId = this.value
            if (collegeId) {
                loadAddStuMajorList(collegeId)
            } else {
                const stuMajorSelect = document.getElementById('stuMajorSelect')
                if (stuMajorSelect) {
                    stuMajorSelect.innerHTML = '<option value="">请选择专业</option>'
                }
            }
        })
    }

    //打开显示框
    if (!addStudentModal) {
        addStudentModal = document.getElementById('addStudentModal')
    }
    addStudentModalInstance = new bootstrap.Modal(addStudentModal)
    addStudentModalInstance.show()
}

/**
 * 清空表单
 */
function clearStuAddForm() {
    const stuCollegeSelect = document.getElementById('stuCollegeSelect')
    if (stuCollegeSelect) {
        stuCollegeSelect.value = ''
    }
    const stuMajorSelect = document.getElementById('stuMajorSelect')
    if (stuMajorSelect) {
        stuMajorSelect.value = ''
    }
    const stuYearSelect = document.getElementById('stuYearSelect')
    if (stuYearSelect) {
        stuYearSelect.value = ''
    }
    const stuGenderSelect = document.getElementById('stuGenderSelect')
    if (stuGenderSelect) {
        stuGenderSelect.value = ''
    }
    const stuNameInput = document.getElementById('stuNameInput')
    if (stuNameInput) {
        stuNameInput.value = ''
    }
    const stuClassSelect = document.getElementById('stuClassSelect')
    if (stuClassSelect) {
        stuClassSelect.value = ''
    }
    const stuNumberInput = document.getElementById('stuNumberInput')
    if (stuNumberInput) {
        stuNumberInput.value = ''
    }
}

/**
 * 加载学院列表
 * @returns 加载学院列表
 */
async function loadAddStuCollegeList() {
    const stuCollegeSelect = document.getElementById('stuCollegeSelect')
    if (stuCollegeSelect.options.length > 1) return //已经加载过了

    // 获取学院列表
    const colleges = await getStuCollegeList()

    // 清空现有选项
    stuCollegeSelect.innerHTML = '<option value="">请选择学院</option>'

    // 添加新选项
    colleges.forEach(college => {
        const opt = document.createElement('option')
        opt.value = college.id
        opt.textContent = college.name
        stuCollegeSelect.appendChild(opt)
    })


}

/**
 * 获取学院列表
 * @returns 学院列表
 */
async function getStuCollegeList() {
    try {
        const response = await axios({
            url: '/college/list',
            method: 'get',
        })

        if (response.data && response.data.data) {
            return response.data.data
        }

        return []
    } catch (error) {
        console.error('获取学院列表失败', error)
        return []
    }

}

function handleStuCollegeChange(collegeId) {
    if (collegeId) {
        loadAddStuMajorList(collegeId)
    } else {
        const stuMajorSelect = document.getElementById('stuMajorSelect')
        if (stuMajorSelect) {
            stuMajorSelect.innerHTML = '<option value="">请选择专业</option>'
        }
    }
}

/**
 * 加载专业列表
 * @param collegeId 学院id
 * @returns 加载专业列表
 */
async function loadAddStuMajorList(collegeId) {
    const stuMajorSelect = document.getElementById('stuMajorSelect')

    stuMajorSelect.innerHTML = '<option value="">请选择专业</option>'

    // 获取专业列表
    const majors = await getStuMajorList(collegeId)

    // 添加新选项
    majors.forEach(major => {
        const opt = document.createElement('option')
        opt.value = major.id
        opt.textContent = major.name
        stuMajorSelect.appendChild(opt)
    })
}

/**
 * 获取专业列表
 * @param collegeId 学院id
 * @returns 专业列表
 */
async function getStuMajorList(collegeId) {
    try {
        const response = await axios({
            url: '/major/majorByCollegeID',
            method: 'get',
            params: {
                collegeId: collegeId,
            },
        })

        if (response.data && response.data.data) {
            return response.data.data
        }

        return []
    } catch (error) {
        console.error('获取专业列表失败', error)
        return []
    }
}

async function getClsByMajorId(majorId, year) {
    // 转换为数字类型
    const majorIdNum = parseInt(majorId)
    const yearNum = parseInt(year)

    // 验证参数
    if (!majorIdNum || !yearNum) {
        console.warn('专业ID或年份为空，跳过请求')
        return []
    }

    try {
        const response = await axios({
            url: '/class/getClsByMajorId',
            method: 'get',
            params: {
                majorId: majorIdNum,
                year: yearNum,
            },
        })
        if (response.data && response.data.data) {
            return response.data.data
        }
        return []
    } catch (error) {
        console.error('获取班级列表失败', error)
        return []
    }
}

async function handleSaveStudent() {
    //获取值
    const number = document.getElementById('stuNumberInput').value
    const name = document.getElementById('stuNameInput').value
    const collegeId = document.getElementById('stuCollegeSelect').value
    const majorId = document.getElementById('stuMajorSelect').value
    const year = document.getElementById('stuYearSelect').value
    const classId = document.getElementById('stuClassSelect').value
    const gender = document.getElementById('stuGenderSelect').value
    if (!number || !name || !collegeId || !majorId || !year || !classId || !gender) {
        showAlert('请填写完整信息')
        return
    }
    //创建学生DTO
    const studentDTO = {
        number: number,
        name: name,
        collegeId: collegeId,
        majorId: majorId,
        year: year,
        classId: classId,
        gender: gender,
    }
    //添加学生
    const result = await axios({
        url: '/student/addStuInfo',
        method: 'put',
        data: studentDTO,
    })
    if (result.data && result.data.code === 0) {
        showSuccessAlert('添加成功')
        //清空表单
        clearStuAddForm()
        //关闭模态框
        const modal = bootstrap.Modal.getInstance(addStudentModal) || addStudentModalInstance
        modal.hide()
        //刷新学生列表
        renderStudentList()
    } else {
        showAlert(result.data.message)
    }

}


//==========================================
//学生列表功能
//==========================================

/**
 * 初始化学生列表功能
 */
function initializeStudentList() {
    const studentForm = document.getElementById('studentForm')
    if (!studentForm) {
        console.error('学生列表不存在')
        return
    }
    const studentList = document.getElementById('studentList')
    if (!studentList) {
        console.error('学生列表不存在')
        return
    }

    //刷新学生列表
    renderStudentList()
}

/**
 * 渲染学生列表
 */
async function renderStudentList() {
    const studentList = document.getElementById('studentList')
    if (!studentList) {
        console.error('学生列表不存在')
        return
    }

    //获取学生列表
    const students = await getStudentList()
    if (!students || students.length === 0) {
        studentList.innerHTML = '<tr><td colspan="8" style="text-align:center;">暂无学生数据</td></tr>'
        return
    }
    let html = ''
    students.forEach((student, index) => {
        // 构建带额外字段的学生数据对象
        const studentData = {
            ...student,
            // 由于后端返回的数据可能没有这些字段，我们从其他属性构建
            id: student.id || student.number,
            collegeId: student.collegeId || '',
            majorId: student.majorId || '',
            classId: student.classId || '',
            year: student.year || '',
            gender: student.gender || ''
        }
        // 将学生数据存储为 JSON 字符串
        const studentJson = JSON.stringify(studentData).replace(/"/g, '&quot;')
        html += `<tr class="student-form-item" data-id="${studentData.id}" data-student="${studentJson}">
            <td>${index + 1}</td>
            <td>${student.number}</td>
            <td>${student.name}</td>
            <td>${student.collegeName}</td>
            <td>${student.majorName}</td>
            <td>${student.className}</td>
            <td>${student.year}</td>
            <td>${student.gender}</td>
            <td>
                <button type="button" class="btn btn-primary stu-list-edit-btn">修改</button>
                <button type="button" class="btn btn-danger stu-list-delete-btn">删除</button>
            </td>
        </tr>`
    })
    studentList.innerHTML = html
}

/**
 * 获取学生列表
 * @returns 学生列表
 */
async function getStudentList() {
    try {
        const response = await axios({
            url: '/student/list',
            method: 'get',
        })
        if (response.data && response.data.data) {
            return response.data.data
        }
        
        return []
    } catch (error) {
        console.error('获取学生列表失败', error)
        return []
    }

}

//==========================================
// 学生批量导入功能
//==========================================

/**
 * 初始化学生导入功能
 */
function initializeStudentImport() {
    const importBtn = document.getElementById('studentImportBtn')
    const fileInput = document.getElementById('studentImportInput')
    
    if (!importBtn || !fileInput) {
        console.error('导入按钮或文件输入框不存在')
        return
    }
    
    importBtn.addEventListener('click', () => {
        fileInput.click()
    })
    
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0]
        if (!file) return
        
        handleStudentImport(file)
        fileInput.value = '' // 清空文件选择
    })
}

/**
 * 处理学生导入
 * @param {File} file - Excel 文件
 */
async function handleStudentImport(file) {
    // 验证文件
    const validation = validateStudentExcelFile(file)
    if (!validation.isValid) {
        showAlert('请选择有效的Excel文件(.xlsx或.xls)')
        return
    }
    
    try {
        // 读取Excel文件
        const studentData = await parseStudentExcelFile(file)
        
        if (studentData.length === 0) {
            showAlert('Excel文件中没有学生数据')
            return
        }
        
        // 验证数据格式
        const validationResult = validateStudentData(studentData)
        if (!validationResult.isValid) {
            showAlert('数据格式不正确：' + validationResult.errors.join('; '))
            return
        }
        
        // 确认导入
        if (!confirm(`确定要导入 ${studentData.length} 名学生吗？`)) {
            return
        }
        
        // 发送到后端
        showSuccessAlert('正在导入学生...', 'info')
        const result = await batchImportStudents(studentData)

        
        
        if (result.code === 0) {
            showSuccessAlert(`成功导入 ${studentData.length} 名学生`)
            renderStudentList() // 刷新学生列表
        } else {
            const message = result.message || '导入失败'
            showBatchImportModal(message)
        }
    } catch (error) {
        console.error('导入学生失败:', error)
        showAlert('导入失败：' + error.message)
    }
}

/**
 * 验证学生Excel文件
 * @param {File} file - 文件对象
 * @returns {Object} 验证结果
 */
function validateStudentExcelFile(file) {
    const validMimeTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel'
    ]
    const validExtensions = ['.xlsx', '.xls']
    
    const fileExtension = file.name.toLowerCase().substring(file.name.lastIndexOf('.'))
    const isValidMimeType = validMimeTypes.includes(file.type)
    const isValidExtension = validExtensions.includes(fileExtension)
    
    return {
        isValid: isValidMimeType || isValidExtension,
        fileName: file.name
    }
}

/**
 * 解析学生Excel文件
 * @param {File} file - Excel 文件
 * @returns {Promise<Array>} 学生数据数组
 */
async function parseStudentExcelFile(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        
        reader.onload = function(event) {
            try {
                const data = new Uint8Array(event.target.result)
                const workbook = XLSX.read(data, { type: 'array' })
                const firstSheetName = workbook.SheetNames[0]
                const worksheet = workbook.Sheets[firstSheetName]
                
                // 转换为JSON，识别多种表头格式
                const jsonData = XLSX.utils.sheet_to_json(worksheet)
                
                const students = jsonData.map(row => ({
                    number: row.学号 || row.number || row.studentNumber || '',
                    name: row.姓名 || row.name || row.studentName || '',
                    gender: row.性别 || row.gender || row.sex || '',
                    year: row.入学年份 || row.year || row.grade || row.开学年份||'',
                    collegeName: row.学院名称 || row.collegeName || row.学院 || row.院系 ||'',
                    majorName: row.专业名称 || row.majorName || row.专业 || row.专业名称 || '',
                    className: row.班级名称 || row.className || row.班级 || row.班级名称 || ''
                }))
                
                resolve(students)
            } catch (error) {
                reject(error)
            }
        }
        
        reader.onerror = function(error) {
            reject(error)
        }
        
        reader.readAsArrayBuffer(file)
    })
}

/**
 * 验证学生数据
 * @param {Array} students - 学生数据数组
 * @returns {Object} 验证结果
 */
function validateStudentData(students) {
    const errors = []
    
    students.forEach((student, index) => {
        if (!student.number) {
            errors.push(`第${index + 1}行：学号不能为空`)
        }
        if (!student.name) {
            errors.push(`第${index + 1}行：姓名不能为空`)
        }
        if (!student.gender) {
            errors.push(`第${index + 1}行：性别不能为空`)
        }
        if (!student.year) {
            errors.push(`第${index + 1}行：入学年份不能为空`)
        }
    })
    
    return {
        isValid: errors.length === 0,
        errors: errors
    }
}

/**
 * 批量导入学生
 * @param {Array} students - 学生数据数组
 * @returns {Promise<Object>} 导入结果
 */
async function batchImportStudents(students) {
    try {
        const response = await axios({
            url: '/student/batchAddStuInfo',
            method: 'post',
            data: students,
        })
        return response.data
    } catch (error) {
        console.error('批量导入学生失败:', error)
        throw error
    }
}

//==========================================
// 学生搜索功能
//==========================================

/**
 * 初始化学生搜索功能
 */
function initializeStudentSearch(){
    const studentSearchSelect=document.getElementById('studentSearchSelect')
    if (!studentSearchSelect) {
        console.error('学生搜索选择框不存在')
        return
    }
    
    const studentSearchInput=document.getElementById('studentSearchInput')
    if (!studentSearchInput) {
        console.error('学生搜索输入框不存在')
        return
    }
    
    const studentSearchBtn=document.getElementById('studentSearchBtn')
    if (!studentSearchBtn) {
        console.error('学生搜索按钮不存在')
        return
    }
    const studentResetBtn=document.getElementById('studentResetBtn')
    if (!studentResetBtn) {
        console.error('学生重置按钮不存在')
        return
    }

    studentSearchSelect.addEventListener('change', ()=>{
        //清空输入框
        studentSearchInput.value = ''
        //搜索
        handleStudentSearch()
    })
    studentSearchInput.addEventListener('input', handleStudentSearch)
    studentSearchInput.addEventListener('change', handleStudentSearch)
    studentSearchInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            handleStudentSearch()
        }
    })

    studentSearchBtn.addEventListener('click', handleStudentSearch)
    studentResetBtn.addEventListener('click', handleStudentReset)
}

/**
 * 搜索学生
 */

async function handleStudentSearch(){
    const searchType=document.getElementById('studentSearchSelect').value
    const keyword=document.getElementById('studentSearchInput').value
    const studentList=await searchStudent(searchType,keyword)

    if (!studentList || studentList.length === 0) {
        const tbody=document.getElementById('studentList')
        if (!tbody) {
            console.error('表格主体不存在')
            return
        }
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">暂无学生数据</td></tr>'
        return
    }else{
        renderStudentListBySearch(studentList)
    }
}

/**
 * 搜索学生
 * @param {string} searchType 搜索类型
 * @param {string} keyword 搜索关键词
 * @returns {Promise<Array>} 学生列表
 */
async function searchStudent(searchType,keyword){
    try {
        const response=await axios({
            url: '/student/search',
            method: 'get',
            params: {
                searchType: searchType,
                keyword: keyword,
            },
        })
        return response.data.data
    } catch (error) {
        console.error('搜索学生失败:', error)
        return []
    }

}

function renderStudentListBySearch(studentList){
    
    const tbody = document.getElementById('studentList')
    if (!tbody) {
        console.error('表格主体不存在')
        return
    }
    let html = ''
    studentList.forEach((student, index) => {
        html += `<tr class="student-form-item" data-id="${student.id}">
            <td>${index + 1}</td>
            <td>${student.number}</td>
            <td>${student.name}</td>
            <td>${student.collegeName}</td>
            <td>${student.majorName}</td>
            <td>${student.className}</td>
            <td>${student.year}</td>
            <td>${student.gender}</td>
            <td>
                <button type="button" class="btn btn-primary stu-list-edit-btn" id="studentEditBtn">修改</button>
                <button type="button" class="btn btn-danger stu-list-delete-btn" id="studentDeleteBtn">删除</button>
            </td>
        </tr>`
    })
    tbody.innerHTML = html
}

/**
 * 重置学生搜索
 */
async function handleStudentReset(){
    console.log('123')
    const studentSearchSelect=document.getElementById('studentSearchSelect')
    if (!studentSearchSelect) {
        console.error('学生搜索选择框不存在')
        return
    }
    
    const studentSearchInput=document.getElementById('studentSearchInput')
    if (!studentSearchInput) {
        console.error('学生搜索输入框不存在')
        return
    }
    studentSearchInput.value = ''
    renderStudentList();
}

//==========================================
// 学生修改功能
//==========================================

let studentEditModalInstance = null
let currentEditStudent = null  // 当前编辑的学生

/**
 * 初始化学生修改功能 - 使用事件委托
 */
function initializeStudentEdit() {
    const studentList = document.getElementById('studentList')
    if (!studentList) {
        console.error('学生列表不存在')
        return
    }
    
    // 使用事件委托监听修改按钮点击
    studentList.addEventListener('click', function(event) {
        if (event.target.classList.contains('stu-list-edit-btn')) {
            // 获取点击按钮所在行的学生信息
            const row = event.target.closest('tr')
            const studentData = row.dataset.student
            
            if (studentData) {
                try {
                    currentEditStudent = JSON.parse(studentData)
                } catch (e) {
                    console.error('解析学生数据失败', e)
                    return
                }
            }
            
            handleStudentEdit()
        }
    })
}

/**
 * 修改学生
 */
function handleStudentEdit() {
    // 获取模态框元素
    const studentEditModal = document.getElementById('studentEditModal')
    
    // 设置下拉框联动事件
    setupEditDropdownsEvent()
    
    // 填充数据
    importStuEditMsg()

    // 打开模态框
    if (!studentEditModal) {
        console.error('学生修改模态框不存在')
        return
    }
    studentEditModalInstance = new bootstrap.Modal(studentEditModal)
    if (!studentEditModalInstance) {
        console.error('学生修改模态框实例不存在')
        return
    }
    studentEditModalInstance.show()

    // 完成按钮点击事件
    const studentEditSaveBtn = document.getElementById('studentEditSaveBtn')
    if (!studentEditSaveBtn) {
        console.error('学生修改完成按钮不存在')
        return
    }
    // 移除旧的事件监听器，避免重复绑定
    const newSaveBtn = studentEditSaveBtn.cloneNode(true)
    studentEditSaveBtn.parentNode.replaceChild(newSaveBtn, studentEditSaveBtn)
    newSaveBtn.addEventListener('click', handleStudentEditSave)
}

/**
 * 填充数据
 */
function importStuEditMsg() {
    if (!currentEditStudent) {
        console.error('没有选中的学生')
        return
    }
    
    const studentEditNameInput = document.getElementById('stuNameEditInput')
    if (!studentEditNameInput) {
        console.error('学生修改姓名输入框不存在')
        return
    }
    studentEditNameInput.value = currentEditStudent.name || ''
    
    const studentEditNumberInput = document.getElementById('stuNumberEditInput')
    if (studentEditNumberInput) {
        studentEditNumberInput.value = currentEditStudent.number || ''
    }
    
    // 填充下拉框
    fillEditDropdowns()
}

/**
 * 填充修改模态框的下拉框
 */
async function fillEditDropdowns() {
    // 加载学院列表
    await loadEditCollegeList()
    
    // 通过学院名称匹配学院ID
    const collegeEditSelect = document.getElementById('collegeEditSelect')
    const collegeName = currentEditStudent.collegeName
    let collegeId = currentEditStudent.collegeId
    
    // 如果没有collegeId，尝试通过名称匹配
    if (!collegeId && collegeName) {
        for (let i = 0; i < collegeEditSelect.options.length; i++) {
            if (collegeEditSelect.options[i].textContent === collegeName) {
                collegeId = collegeEditSelect.options[i].value
                break
            }
        }
    }
    
    if (collegeId) {
        collegeEditSelect.value = collegeId
        await loadEditMajorList(collegeId)
        
        // 通过专业名称匹配专业ID
        const majorEditSelect = document.getElementById('majorEditSelect')
        const majorName = currentEditStudent.majorName
        let majorId = currentEditStudent.majorId
        
        if (!majorId && majorName) {
            for (let i = 0; i < majorEditSelect.options.length; i++) {
                if (majorEditSelect.options[i].textContent === majorName) {
                    majorId = majorEditSelect.options[i].value
                    break
                }
            }
        }
        
        if (majorId) {
            majorEditSelect.value = majorId
            
            // 加载年份列表
            await loadEditYearList()
            
            // 设置年份选中值
            const yearEditSelect = document.getElementById('yearEditSelect')
            const year = currentEditStudent.year
            if (year) {
                yearEditSelect.value = year
            }
            
            // 加载班级列表
            await loadEditClassList(majorId, year)
            
            // 设置班级选中值
            const classEditSelect = document.getElementById('classEditSelect')
            const className = currentEditStudent.className
            if (className) {
                for (let i = 0; i < classEditSelect.options.length; i++) {
                    if (classEditSelect.options[i].textContent === className) {
                        classEditSelect.value = classEditSelect.options[i].value
                        break
                    }
                }
            }
        }
    }
    
    // 设置性别选中值
    const genderEditSelect = document.getElementById('genderEditSelect')
    const gender = currentEditStudent.gender
    if (gender && genderEditSelect) {
        genderEditSelect.value = gender
    }
}

/**
 * 加载年份列表（修改模态框）
 */
async function loadEditYearList() {
    const yearEditSelect = document.getElementById('yearEditSelect')
    if (!yearEditSelect) return
    
    if (yearEditSelect.options.length > 1) return // 已经加载过了
    
    const currentYear = new Date().getFullYear()
    yearEditSelect.innerHTML = '<option value="">请选择入学年份</option>'
    
    // 生成近5年的年份选项
    for (let year = currentYear; year >= currentYear - 5; year--) {
        const opt = document.createElement('option')
        opt.value = year
        opt.textContent = year + '年'
        yearEditSelect.appendChild(opt)
    }
}

/**
 * 加载学院列表（修改模态框）
 */
async function loadEditCollegeList() {
    const collegeEditSelect = document.getElementById('collegeEditSelect')
    if (!collegeEditSelect) return
    
    if (collegeEditSelect.options.length > 1) return // 已经加载过了
    
    const colleges = await getStuCollegeList()
    collegeEditSelect.innerHTML = '<option value="">请选择学院</option>'
    
    colleges.forEach(college => {
        const opt = document.createElement('option')
        opt.value = college.id
        opt.textContent = college.name
        collegeEditSelect.appendChild(opt)
    })
}

/**
 * 加载专业列表（修改模态框）
 * @param {number} collegeId 学院id
 */
async function loadEditMajorList(collegeId) {
    const majorEditSelect = document.getElementById('majorEditSelect')
    if (!majorEditSelect) return
    
    majorEditSelect.innerHTML = '<option value="">请选择专业</option>'
    
    if (!collegeId) return
    
    const majors = await getStuMajorList(collegeId)
    majors.forEach(major => {
        const opt = document.createElement('option')
        opt.value = major.id
        opt.textContent = major.name
        majorEditSelect.appendChild(opt)
    })
}

/**
 * 加载班级列表（修改模态框）
 * @param {number} majorId 专业id
 * @param {number} year 年份
 */
async function loadEditClassList(majorId, year) {
    const classEditSelect = document.getElementById('classEditSelect')
    if (!classEditSelect) return
    
    classEditSelect.innerHTML = '<option value="">请选择班级</option>'
    
    if (!majorId || !year) return
    
    const classes = await getClsByMajorId(majorId, year)
    classes.forEach(cls => {
        const opt = document.createElement('option')
        opt.value = cls.id
        opt.textContent = cls.name
        classEditSelect.appendChild(opt)
    })
}

/**
 * 设置修改模态框下拉框的联动事件
 */
function setupEditDropdownsEvent() {
    const collegeEditSelect = document.getElementById('collegeEditSelect')
    const majorEditSelect = document.getElementById('majorEditSelect')
    const classEditSelect = document.getElementById('classEditSelect')
    const yearEditSelect = document.getElementById('yearEditSelect')
    
    // 学院变更事件
    if (collegeEditSelect) {
        collegeEditSelect.addEventListener('change', async function() {
            const collegeId = this.value
            majorEditSelect.innerHTML = '<option value="">请选择专业</option>'
            classEditSelect.innerHTML = '<option value="">请选择班级</option>'
            if (collegeId) {
                await loadEditMajorList(collegeId)
            }
        })
    }
    
    // 专业变更事件
    if (majorEditSelect) {
        majorEditSelect.addEventListener('change', async function() {
            const majorId = this.value
            const year = yearEditSelect.value
            classEditSelect.innerHTML = '<option value="">请选择班级</option>'
            if (majorId && year) {
                await loadEditClassList(majorId, year)
            }
        })
    }
    
    // 年份变更事件
    if (yearEditSelect) {
        yearEditSelect.addEventListener('change', async function() {
            const year = this.value
            const majorId = majorEditSelect.value
            classEditSelect.innerHTML = '<option value="">请选择班级</option>'
            if (majorId && year) {
                await loadEditClassList(majorId, year)
            }
        })
    }
}

/**
 * 保存学生修改
 */
async function handleStudentEditSave(){
    //获取值
    const stuNameEditInput = document.getElementById('stuNameEditInput').value
    const stuNumberEditInput = document.getElementById('stuNumberEditInput').value
    const collegeEditSelect = document.getElementById('collegeEditSelect').value
    const majorEditSelect = document.getElementById('majorEditSelect').value
    const yearEditSelect = document.getElementById('yearEditSelect').value
    const classEditSelect = document.getElementById('classEditSelect').value
    const genderEditSelect = document.getElementById('genderEditSelect').value

    console.log(stuNameEditInput, stuNumberEditInput, collegeEditSelect, majorEditSelect, yearEditSelect, classEditSelect, genderEditSelect)
    
    if(!stuNameEditInput || !stuNumberEditInput || !collegeEditSelect || !majorEditSelect || !yearEditSelect || !classEditSelect || !genderEditSelect){
        showAlert('请填写完整信息')
        return
    }
    //创建学生DTO
    const studentDTO = {
        id: currentEditStudent.id,
        name: stuNameEditInput,
        number: stuNumberEditInput,
        collegeId: collegeEditSelect,
        majorId: majorEditSelect,
        year: yearEditSelect,
        classId: classEditSelect,
        gender: genderEditSelect
    }
    //修改学生
    const result = await updateStuInfo(studentDTO)
    if (result.code === 0) {
        showSuccessAlert('修改成功')
        //关闭模态框
        const modal = bootstrap.Modal.getInstance(studentEditModal) || studentEditModalInstance
        modal.hide()
        //刷新学生列表
        renderStudentList()
    }
    else {
        showAlert(result.message)
    }
}

/**
 * 修改学生信息
 * @param {Object} studentDTO 学生信息
 * @returns {Promise} 修改学生信息结果
 */
async function updateStuInfo(studentDTO) {
    const result = await axios({
        url: '/student/updateStuInfo',
        method: 'put',
        data: studentDTO,
    })
    return result.data
}

//==========================================
// 学生删除功能
//==========================================

/**
 * 初始化学生删除功能
 */
function initializeStudentDelete() {
    const studentList = document.getElementById('studentList')
    if (!studentList) {
        console.error('学生列表不存在')
        return
    }
    studentList.addEventListener('click', function(event) {
        if (event.target.classList.contains('stu-list-delete-btn')) {
            handleStudentDelete(event)
        }
    })
}

/**
 * 删除学生
 */
async function handleStudentDelete(event) {
    //获取点击删除按钮所在行的学生信息
    const row = event.target.closest('tr')
    const studentData = row.dataset.student

    if (!studentData) {
        showAlert('获取学生信息失败')
        return
    }

    let currentDeleteStudent = null
    try {
        currentDeleteStudent = JSON.parse(studentData)
    } catch (e) {
        console.error('解析学生数据失败', e)
        showAlert('解析学生信息失败')
        return
    }

    //确认删除
    try {
        const result = await deleteStudent(currentDeleteStudent.id)
        if (result.code === 0) {
            showSuccessAlert('删除成功')
            //刷新学生列表
            renderStudentList()
        } else {
            showAlert(result.message || '删除失败')
        }
    } catch (error) {
        console.error('删除学生失败:', error)
        showAlert('删除学生失败：' + error.message)
    }
}

/**
 * 删除学生信息
 * @param {number} studentId 学生ID
 * @returns {Promise<Object>} 删除结果
 */
async function deleteStudent(studentId) {
    
    try {
        const response = await axios({
            url: '/student/delete',
            method: 'delete',
            params: {
                number: studentId,
            },
        })
        return response.data
    } catch (error) {
        console.error('删除学生请求失败:', error)
        throw error
    }
}