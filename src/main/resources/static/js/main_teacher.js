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
        teacherList: [],
        fullTeacherList: [] // 保存完整的教师列表
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
                    this.fullTeacherList = response.data.data // 保存完整列表
                } else {
                    showAlert(response.data.message || '获取教师列表失败！');
                }
            } catch (error) {
                console.error('获取教师列表失败', error);
                showAlert('获取教师列表失败！请重试！');
            }
        },
        // 过滤教师列表
        filterTeachers(searchType, searchValue) {
            this.teacherList = this.fullTeacherList.filter(teacher => {
                let fieldValue
                switch (searchType) {
                    case 'number':
                        fieldValue = teacher.username
                        break
                    case 'name':
                        fieldValue = teacher.name
                        break
                    case 'position':
                        fieldValue = teacher.positionName
                        break
                    case 'college':
                        fieldValue = teacher.collegeName
                        break
                    default:
                        fieldValue = teacher.username
                }
                return fieldValue && fieldValue.toString().toLowerCase().includes(searchValue)
            })
        },
        // 显示全部教师
        showAllTeachers() {
            this.teacherList = [...this.fullTeacherList]
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
                    console.log(response.data);
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

                const editTeacherDTO = {
                    username: this.form.username,
                    name: this.form.name,
                    collegeId: college ? college.id : null,
                    positionId: position ? position.id : null,
                    gender: this.form.gender
                }
                const response = await axios({
                    url: '/teacher/update',
                    method: 'put',
                    data: editTeacherDTO
                })
                console.log(response)
                if (response.data.code === 0) {
                    showSuccessAlert('修改教师成功！')
                    //关闭模态框
                    const modalEl = document.getElementById('teacherEditModal');
                    const modal = bootstrap.Modal.getInstance(modalEl);
                    teacherListAPP.loadTeacherList()
                    if (modal) {
                        modal.hide()
                    }
                } else {
                    showAlert('修改教师失败！请重试！')
                }
            } catch (error) {
                console.error('修改教师失败', error)
                showAlert('修改教师失败！请重试！')
            }
        }
    }
})

//===============================
//批量导入教师
//===============================
const teacherImportAPP = new Vue({
    el: '#teacherImportAPP',
    data: {
        selectedFile: null,
        importing: false
    },
    methods: {
        triggerImport() {
            document.getElementById('teacherFileInput').click();
        },
        handleTeacherFileChange(e) {
            const file = e.target.files[0]
            if (!file) { return }
            const filename = file.name.toLowerCase()
            if (!filename.endsWith('.xlsx') && !filename.endsWith('.xls')) {
                showAlert('请选择 Excel 文件（.xlsx 或 .xls）！');
                this.selectedFile = null;
                return;
            }

            this.selectedFile = file

            this.handleImport()
        },
        async handleImport() {
            if (!this.selectedFile) {
                showAlert('请选择文件！')
                return
            }

            this.importing = true
            try {
                const formData = new FormData()
                formData.append('file', this.selectedFile)
                const response = await axios({
                    url: '/teacher/batchAddTeaInfo',
                    method: 'post',
                    data: formData,
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                })
                if (response.data.code === 0) {
                    showSuccessAlert('导入成功！')
                    //更新教师列表
                    teacherListAPP.loadTeacherList()
                } else {
                    const message = response.data.message || '导入失败！'
                    // 换行符转 <br> 用于显示
                    showAlert(message.replace(/\n/g, '<br>'))
                }
            } catch (error) {
                showAlert('导入失败！请重试！');
            }
            finally {
                document.getElementById('teacherFileInput').value = '';
                this.selectedFile = null;
                this.importing = false;
            }
        }
    }
})

//===============================
//教师搜索功能
//===============================
const teacherSearchAPP = new Vue({
    el: '#teacherSearchAPP',
    data: {
        searchType: 'number',
        searchValue: ''
    },
    methods: {
        searchTeacher() {
            const searchValue = this.searchValue.trim().toLowerCase()
            const searchType = this.searchType

            if (!searchValue) {
                // 如果搜索值为空，显示全部教师
                teacherListAPP.showAllTeachers()
                return
            }

            // 过滤教师列表
            teacherListAPP.filterTeachers(searchType, searchValue)
        },
        resetSearch() {
            this.searchType = 'number'
            this.searchValue = ''
            teacherListAPP.showAllTeachers()
        }
    }
})