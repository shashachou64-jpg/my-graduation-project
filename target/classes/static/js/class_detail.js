document.addEventListener('DOMContentLoaded',()=>{
    //获取url参数
    const urlParams=new URLSearchParams(window.location.search)
    const courseId=urlParams.get('courseId')

    if(courseId){
        console.log('课程ID:',courseId)
    }else{
        alert('未找到课程ID参数')
    }
    // 请求后端获取该课程的学生列表并渲染
    axios.get(`/course/students?courseId=${courseId}`).then(res => {
        const students = res.data.data || []
        const tbody = document.querySelector('.students-form-body')
        if (students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center">当前课程暂无学生</td></tr>'
            return
        }
        const rows = students.map(s => {
            return `<tr class="students-form-item">
                        <td>${s.number || ''}</td>
                        <td>${s.name || ''}</td>
                        <td>${s.collegeName || ''}</td>
                        <td>${s.gender || ''}</td>
                        <td>${s.year || ''}</td>
                        <td>${s.classroom || ''}</td>
                        <td>${s.email || ''}</td>
                        <td>
                            <button type="button" class="btn btn-primary">修改</button>
                            <button type="button" class="btn btn-danger">删除</button>
                        </td>
                    </tr>`
        }).join('')
        tbody.innerHTML = rows
    }).catch(err => {
        console.error('获取课程学生列表失败', err)
        alert('获取学生数据失败')
    })
})