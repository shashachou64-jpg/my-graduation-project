//教师username
const teacherUsername = localStorage.getItem('username')
//获得后面四位并去除前导0
const teacherId = parseInt(teacherUsername.slice(-4), 10)

// 课程管理 Vue 实例
const courseAPP = new Vue({
    el: '#page-courses',
    data: {
        courseList: []
    },
    methods: {
        async loadCourseList() {
            const response = await axios({
                url: `/course/listByTeacherId/${localStorage.getItem('token')}`,
                method: 'get'
            })
            if (response.data.code === 0) {
                this.courseList = response.data.data;
            } else {
                console.error('获取课程列表失败', response.data.message);
            }
        }
    }
})

// 作业管理Vue实例
const homeWorkAPP = new Vue({
    el: '#page-homework',
    data: {
        //发布作业表单数据
        publishHomeworkForm: {
            title: '',
            courseId: '',
            groupId: '',
            startTime: '',
            deadline: '',
            remark: '',
            totalScore: 100,
            teacherId: teacherId
        },
        courseList: [],
        groupList: [],
        homeworkList: []
    },
    methods: {
        async addHomework() {
            this.publishHomeworkForm = {
                title: '',
                courseId: '',
                groupId: '',
                startTime: '',
                deadline: '',
                remark: '',
                totalScore: 100,
                teacherId: teacherId
            }
            await this.loadCourseList()
        },

        async loadCourseList() {
            const response = await axios({
                url: `/course/listByTeacherId/${localStorage.getItem('token')}`,
                method: 'get'
            })
            if (response.data.code === 0) {
                this.courseList = response.data.data;
            } else {
                console.error('获取课程列表失败', response.data.message);
            }
        },
        async loadGroupList() {
            try {
                const response = await axios({
                    url: '/group/listByCourseId',
                    method: 'get',
                    params: {
                        courseId: this.publishHomeworkForm.courseId
                    }
                })
                if (response.data.code === 0) {
                    this.groupList = response.data.data;
                }
            } catch (error) {
                // 后端服务未实现该功能，静默处理
                console.log('小组列表功能暂不可用');
                this.groupList = [];
            }
        },
        async publishHomework() {
            //封装dto
            const dto = {
                title: this.publishHomeworkForm.title,
                courseId: this.publishHomeworkForm.courseId,
                groupId: this.publishHomeworkForm.groupId,
                startTime: this.publishHomeworkForm.startTime,
                deadline: this.publishHomeworkForm.deadline,
                description: this.publishHomeworkForm.remark,
                totalScore: this.publishHomeworkForm.totalScore,
                teacherId: teacherId
            }
            if (this.publishHomeworkForm.title === '' || this.publishHomeworkForm.courseId === '' || this.publishHomeworkForm.deadline === '' || this.publishHomeworkForm.totalScore === 0) {
                showAlert('请填写完整！');
                return;
            }
            if (this.publishHomeworkForm.startTime > this.publishHomeworkForm.deadline) {
                showAlert('开始时间不能大于截止时间！');
                return;
            }
            if (this.publishHomeworkForm.totalScore < 0) {
                showAlert('总分不能小于0！');
                return;
            }
            if (this.publishHomeworkForm.totalScore > 100) {
                showAlert('总分不能大于100！');
                return;
            }
            const response = await axios({
                url: '/homework/publish',
                method: 'post',
                data: dto
            })
            if (response.data.code === 0) {
                showSuccessAlert('发布作业成功！');
                //关闭模态框
                const modalEl = document.getElementById('publishHomeworkModal');
                const modal = bootstrap.Modal.getInstance(modalEl);
                modal.hide();
                //刷新作业列表
                await this.loadHomeworkList();
            } else {
                showAlert('发布作业失败！请重试！');
            }
        },

        async loadHomeworkList() {
            const response = await axios({
                url: `/homework/list/${teacherId}`,
                method: 'get'
            })
            if (response.data.code === 0) {
                this.homeworkList = response.data.data;
            } else {
                console.error('获取作业列表失败', response.data.message);
            }
        },
        async loadSubmissionList() {

        },

        /**
         * 查看学生作业提交详情
         * @param {Object} homework - 作业对象
         */
        viewSubmissionDetail(homework) {
            localStorage.setItem('currentHomework', JSON.stringify(homework));
            localStorage.setItem('teacherId', JSON.stringify(teacherId));
            // 跳转到学生提交情况页面
            window.location.href = 'homework_submission.html';

        }

    }
})



// 侧边栏 Vue 实例
const sidebar = new Vue({
    el: '#sidebar',
    data: {
        name: '',
        positionName: '',
    },
    methods: {
        async loadTeacherInfo() {
            const response = await axios({
                url: '/teacher/teacherInfo',
                method: 'get',
                headers: {
                    Authorization: 'Bearer ' + (localStorage.getItem('token') || '')
                }
            })
            if (response.data.code === 0) {
                this.name = response.data.data.name;
                this.positionName = response.data.data.positionName;
            }
        },
        async getCourseList() {
            await courseAPP.loadCourseList()
        },
        async getHomeworkList() {
            await homeWorkAPP.loadHomeworkList()
        }
    },
    mounted() {
        this.loadTeacherInfo()
    }
})





/**
 * 显示危险提示框
 * @param {string} message 提示内容
 * @returns {void}
 */
function showAlert(message) {
    const alertEl = document.getElementById('alert-DangerB');
    if (!alertEl) return;

    alertEl.innerHTML = message;  // 使用 innerHTML 解析 HTML 标签

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

