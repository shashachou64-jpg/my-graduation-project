//教师username
const teacherUsername = localStorage.getItem('username')
//获得后面四位并去除前导0
const teacherId = parseInt(teacherUsername.slice(-4), 10)

// 课程管理 Vue 实例
const courseAPP = new Vue({
    el: '#page-courses',
    data:{
        courseList: []
    },
    methods:{
        async loadCourseList(){
            const response = await axios({
                url: `/course/listByTeacherId/${localStorage.getItem('token')}`,
                method: 'get'
            })
            if(response.data.code === 0){
                this.courseList = response.data.data;
            }else{
                console.error('获取课程列表失败', response.data.message);
            }
        }
    }
})

// 作业管理Vue实例
const homeWorkAPP = new Vue({
    el: '#page-homework',
    data:{
        //发布作业表单数据
        publishHomeworkForm:{
            title:'',
            courseId:'',
            groupId:'',
            startTime:'',
            deadline:'',
            remark:'',
            totalScore:100,
            teacherId:teacherId
        },
        courseList: [],
        groupList: []
    },
    methods:{
        async addHomework(){
            this.publishHomeworkForm = {
                title:'',
                courseId:'',
                groupId:'',
                startTime:'',
                deadline:'',
                remark:'',
                totalScore:100,
                teacherId:teacherId
            }
            await this.loadCourseList()
        },

        async loadCourseList(){
            const response = await axios({
                url: `/course/listByTeacherId/${localStorage.getItem('token')}`,
                method: 'get'
            })
            if(response.data.code === 0){
                this.courseList = response.data.data;
            }else{
                console.error('获取课程列表失败', response.data.message);
            }
        },
        async loadGroupList(){
            const response = await axios({
                url: '/group/listByCourseId',
                method: 'get',
                params: {
                    courseId: this.publishHomeworkForm.courseId
                }
            })
            if(response.data.code === 0){
                this.groupList = response.data.data;
            }else{
                console.error('获取小组列表失败', response.data.message);
            }
        },
        async publishHomework(){
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
            const response = await axios({
                url: '/homework/publish',
                method: 'post',
                data: dto
            })
            //关闭模态框
            $('#publishHomeworkModal').modal('hide');
            //刷新作业列表
            await this.loadHomeworkList();
        }
            
    }
})



// 侧边栏 Vue 实例
const sidebar = new Vue({
    el: '#sidebar',
    data:{
        name:'',
        positionName:'',
    },
    methods:{
        async loadTeacherInfo(){
            const response = await axios({
                url: '/teacher/teacherInfo',
                method: 'get',
                headers: {
                    Authorization: 'Bearer ' + (localStorage.getItem('token') || '')
                }
            })
            if(response.data.code === 0){
                this.name = response.data.data.name;
                this.positionName = response.data.data.positionName;
            }
        },
        async getCourseList(){
            await courseAPP.loadCourseList()
        }
    },
    mounted(){
        this.loadTeacherInfo()
    }
})

