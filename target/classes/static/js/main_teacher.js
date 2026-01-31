//===============================
//添加单个教师
//===============================
const addTeacherAPP = new Vue({
    el: '#addTeacherModal',
    data: {
        form: {
            name: '',
            collegeName: '',
            position: '',
            gender: ''
        },
        colleges: [],
        positionList: [],
        genders: [
            { value: '男', label: '男' },
            { value: '女', label: '女' }
        ]
    },
    methods: {
        async loadColleges() {
            if (this.colleges.length > 0) return
            try {
                const response = await axios({
                    url: '/college/list',
                    method: 'get',
                })
                this.colleges = response.data.data
            } catch (error) {
                console.error('获取学院列表失败', error)
                return []
            }
        },
        async loadPositions() {
            if (this.positionList.length > 0) return

            try {
                const response = await axios({
                    url: '/position/list',
                    method: 'get',
                })
                this.positionList = response.data.data
            } catch (error) {
                console.error('获取职位列表失败', error)
                return []
            }
        },
        async addTeacher() {
            try {
                // 表单验证
                if (!this.form.name) {
                    showAlert('请输入教师姓名！');
                    return;
                }
                if (!this.form.collegeName) {
                    showAlert('请选择学院！');
                    return;
                }
                if (!this.form.position) {
                    showAlert('请选择职位！');
                    return;
                }
                if (!this.form.gender) {
                    showAlert('请选择性别！');
                    return;
                }

                // 发送添加请求
                const response = await axios({
                    url: '/teacher/add',
                    method: 'post',
                    data: this.form
                })

                if (response.data.code === 0) {
                    showSuccessAlert('添加教师成功！');
                    // 关闭模态框
                    const modalEl = document.getElementById('addTeacherModal');
                    const modal = bootstrap.Modal.getInstance(modalEl);
                    teacherListAPP.loadTeacherList()
                    if (modal) {
                        modal.hide();
                    }
                    // 重置表单
                    this.form = {
                        name: '',
                        collegeName: '',
                        position: '',
                        gender: ''
                    }

                } else {
                    showAlert(response.data.message || '添加教师失败！');
                }
            } catch (error) {
                console.error('添加教师失败', error);
                showAlert(error.response?.data?.message || '添加教师失败，请稍后重试！');
            }
        }
    },
    mounted() {
        document.getElementById('addTeacherModal').addEventListener('shown.bs.modal', () => {
            this.loadColleges()
            this.loadPositions()
        })
    }
})



//===============================
//教师列表
//===============================
const teacherListAPP = new Vue({
    el: '#teacherListAPP',
    data: {
        teacherList: []
    },
    methods: {
        async loadTeacherList() {
            try {
                const response = await axios({
                    url: '/teacher/list',
                    method: 'get',
                })
                if (response.data.code === 0) {
                    this.teacherList = response.data.data
                } else {
                    showAlert(response.data.message || '获取教师列表失败！');
                }
            } catch (error) {

            }
        },
        async editTeacher(teacher) {
            //弹出修改教师模态框
            if (editTeacherAPP) {
                editTeacherAPP.openEditModal(teacher)
            } else {
                console.error('editTeacherApp 未初始化')
            }
        },
        async deleteTeacher(username) {
            try {
                const response = await axios({
                    url: '/teacher/delete',
                    method: 'delete',
                    params: {
                        username: username
                    }
                })
                if (response.data.code === 0) {
                    showSuccessAlert('删除教师成功！');
                    this.loadTeacherList()
                } else {
                    showAlert(response.data.message || '删除教师失败！');
                }
            } catch (error) {
                console.error('删除教师失败', error);
                showAlert('删除教师失败，请稍后重试！');
            }
        }
    },
    mounted() {
        this.loadTeacherList()
    }

})
//===============================
//修改教师模态框
//===============================
const editTeacherAPP = new Vue({
    el: '#teacherEditModal',
    data: {
        form: {
            username: '',
            name: '',
            collegeName: '',
            positionName: '',
            gender: ''
        },
        colleges: [],
        positionList: [],

    },
    methods: {
        async openEditModal(teacher) {
            /**
             * 获取学院列表
             * 获取职位列表
             * 填充模态框数据
             * 显示模态框
             */
            const response = await axios({
                url: '/college/list',
                method: 'get',
            })
            const colleges = response.data.data
            const response2 = await axios({
                url: '/position/list',
                method: 'get',
            })
            const positionList = response2.data.data
            this.form = {
                username: teacher.username,
                name: teacher.name,
                collegeName: teacher.collegeName,
                positionName: teacher.positionName,
                gender: teacher.gender
            }
            this.colleges = colleges
            this.positionList = positionList
            const modal = new bootstrap.Modal(document.getElementById('teacherEditModal'))
            modal.show()
        },
        async editTeacher() {
            try {
                const college = this.colleges.find(c => c.name === this.form.collegeName);
                const position = this.positionList.find(p => p.name === this.form.positionName);

                const teacherDTO = {
                    username: this.form.username,
                    name: this.form.name,
                    collegeId: college ? college.id : null,
                    positionId: position ? position.id : null,
                    gender: this.form.gender
                }
                const response=await axios({
                    url: '/teacher/update',
                    method: 'put',
                    data: teacherDTO
                })
            } catch (error) {
                console.error('修改教师失败', error);
                showAlert('修改教师失败，请稍后重试！');
            }
        }
    }
})
//===============================