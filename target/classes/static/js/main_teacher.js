//渲染教师职位
const teaPositionSelect = document.getElementById('teaPositionSelect')
teaPositionSelect.innerHTML = '<option value="">请选择职位</option>'
axios({
    url: '/position/list',
}).then(result => {
    const positions = result.data.data
    positions.forEach(major => {
        const option = document.createElement('option');
        option.value = major.id
        option.textContent = major.name
        teaPositionSelect.appendChild(option)
    });
})

//添加教师按钮点击事件
const teacherModal = document.getElementById('teacherModal');
teacherModal.addEventListener('shown.bs.modal', function () {
    // 可选：自动聚焦到姓名输入框，提升体验
    document.getElementById('teaNameInput').focus();

    // 绑定 Enter 键提交（只在模态框打开时有效）
    teacherModal.addEventListener('keydown', teacherAddHandleEnterKey);
});

// 可选：模态框关闭后移除事件监听（防止内存泄漏或重复绑定）
teacherModal.addEventListener('hidden.bs.modal', function () {
    teacherModal.removeEventListener('keydown', teacherAddHandleEnterKey);
});

// 统一的 Enter 键处理函数
function teacherAddHandleEnterKey(e) {
    // 避免在 textarea 或 shift+enter 等情况下触发
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();  // 阻止默认行为（如表单提交）

        // 触发添加按钮的点击事件
        document.getElementById('tea-add-btn').click();
    }
}

const addTeacherBtn = document.getElementById('tea-add-btn')
addTeacherBtn.addEventListener('click', () => {
    const name = document.getElementById('teaNameInput').value.trim()
    const collegeId = document.getElementById('teaCollegeSelect').value
    const positionId = document.getElementById('teaPositionSelect').value
    const gender = document.getElementById('teaGenderSelect').value
    //自动聚焦到姓名框
    document.getElementById('teaNameInput').focus()
    axios({
        url: '/teacher/add',
        method: 'post',
        data: {
            name,
            collegeId,
            positionId,
            gender
        }
    }).then(result => {
        if (result.data.code === 0) {
            //关闭模态框
            document.querySelector('#teacherModal .btn-secondary').click();
            //刷新学生列表
            renderTeacherList()
        } else {
            alert('添加教师失败：' + result.data.message)
        }
    })
})

//教师删除按钮点击事件
document.querySelector('.teacher-list').addEventListener('click', e => {
    if (e.target.classList.contains('tea-list-delete-btn')) {
        const teaId = e.target.parentNode.dataset.id
        console.log(teaId)
        axios({
            url: '/teacher/delete',
            method: 'post',
            data: {
                id: teaId
            }
        }).then(result => {
            if (result.data.code === 0) {
                //刷新学生列表
                renderTeacherList()
            } else {
                alert('删除教师失败：' + result.data.message)
            }
        })
    }


    const EditTraModalDom = document.getElementById('teacherEditModal')
    EditTraModalDom.addEventListener('shown.bs.modal', function () {
        // 正确！聚焦到学生的学号或姓名输入框
        document.getElementById('teaNameEditInput').focus();

        // 绑定 Enter 键提交（只在模态框打开时有效）
        EditTraModalDom.addEventListener('keydown', teacherEditHandleEnterKey)
    })
    EditTraModalDom.addEventListener('hidden.bs.modal', function () {
        EditTraModalDom.removeEventListener('keydown', teacherEditHandleEnterKey)
    })
    function teacherEditHandleEnterKey(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            document.getElementById('tea-edit-btn').click()
        }
    }
    if (e.target.classList.contains('tea-list-edit-btn')) {
        console.log('编辑按钮被点击了')
        //显示模态框
        const modalDom = document.getElementById('teacherEditModal')
        const modal = new bootstrap.Modal(modalDom)
        modal.show()

        // 获取数据
        const teaId = e.target.parentNode.dataset.id
        const teaName = e.target.parentNode.dataset.name
        const teaCollegeName = e.target.parentNode.dataset.collegeId
        const teaPositionName = e.target.parentNode.dataset.positionId
        const teaGender = e.target.parentNode.dataset.gender
        //填充数据到模态框输入框
        document.getElementById('teaNumberEditInput').value = teaId
        document.getElementById('teaNameEditInput').value = teaName
        document.querySelector('.tea-edit-college1').value = teaCollegeName
        document.querySelector('.tea-edit-position1').value = teaPositionName
        document.getElementById('teaGenderEditSelect').value = teaGender

        const teaPositionSelect = document.getElementById('teaPositionEditSelect')
        teaPositionSelect.innerHTML = '<option value="">请选择职位</option>'
        axios({
            url: '/position/list',
        }).then(result => {
            const positions = result.data.data
            positions.forEach(major => {
                const option = document.createElement('option');
                option.value = major.id
                option.textContent = major.name
                teaPositionSelect.appendChild(option)
            });
        })

        document.getElementById('tea-edit-btn').addEventListener('click', () => {
            const id = document.getElementById('teaNumberEditInput').value
            const teaName = document.getElementById('teaNameEditInput').value
            const teaCollegeId = document.querySelector('.tea-edit-college1').value
            const teaPositionId = document.querySelector('.tea-edit-position1').value
            const teaGender = document.getElementById('teaGenderEditSelect').value
            
            axios({
                url:'/teacher/update',
                method:'post',
                data: {
                    id: id,
                    name: teaName,
                    collegeId: teaCollegeId,
                    positionId: teaPositionId,
                    gender: teaGender
                }
            }).then(result=>{
                if(result.data.code === 0){
                    //关闭模态框
                    document.querySelector('#teacherEditModal .btn-secondary').click();
                    //刷新学生列表
                    renderTeacherList()
                }else{
                    alert('更新教师信息失败：'+result.data.message)
                }
            })
            
        })
    }
})