// 课程统计图表初始化函数
function initCourseStatsCharts() {
    // 学院课程分布折线图
    const collegeCtx = document.getElementById('courseCollegeLineChart');
    if (collegeCtx) {
        new Chart(collegeCtx.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['信息学院', '人文学院', '法学院', '会计学院', '机自学院', '外语学院', '艺术学院'],
                datasets: [{
                    label: '课程数',
                    data: [156, 120, 98, 145, 132, 110, 95],
                    fill: true,
                    backgroundColor: 'rgba(155, 89, 182, 0.15)',
                    borderColor: '#9b59b6',
                    borderWidth: 3,
                    tension: 0.4,
                    pointRadius: 6,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true, max: 200, ticks: { stepSize: 40 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
}

// 页面加载完成后初始化课程统计图表
document.addEventListener('DOMContentLoaded', initCourseStatsCharts);

/**
 * 教师统计图表
 */
const teacherStatsApp = new Vue({
    el: '#teacherStatsApp',
    data: {
        totalTeacher: 0,
        manTeacher: 0,
        womanTeacher: 0,
        collegeTeacherList: [],
        collegeTeacherCounts: [],

        // 图表实例
        teacherGenderChart: null,
        teacherCollegeChart: null
    },
    methods: {
        async loadTeacherData() {
            try {
                const response = await axios({
                    url: '/teacher/total',
                    method: 'get'
                })
                if (response.data.code === 0) {
                    this.totalTeacher = response.data.data.totalTeacher;
                    this.manTeacher = response.data.data.manTeacher;
                    this.womanTeacher = response.data.data.womanTeacher;
                    this.collegeTeacherList = response.data.data.collegeTeacherList;
                    this.collegeTeacherCounts = response.data.data.collegeTeacherCounts;
                }

                this.$nextTick(() => {
                    this.initTeacherCharts()
                })
            } catch (error) {
                console.error('获取教师统计数据失败', error);
                showAlert('获取教师统计数据失败！请重试！');
            }
        },
        initTeacherCharts() {
            this.initTeacherGenderChart()
            this.initTeacherCollegeChart()
        },
        initTeacherGenderChart() {
            const ctx = this.$refs.teacherGenderChart.getContext('2d')
            if (!ctx) return
            this.teacherGenderChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['男教师', '女教师'],
                    datasets: [{
                        data: [this.manTeacher, this.womanTeacher],
                        backgroundColor: ['#667eea', '#e74c3c'],
                        borderWidth: 4,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '60%'
                }
            })
        },
        initTeacherCollegeChart() {
            const ctx = this.$refs.teacherCollegeLineChart;
            if (!ctx) return;

            // 从 collegeTeacherList 提取数据
            const labels = this.collegeTeacherList.map(item => item.collegeName);
            const counts = this.collegeTeacherList.map(item => item.collegeTeacher);

            // 计算 Y 轴最大值
            const maxCount = counts.length > 0 ? Math.max(...counts) : 100;
            const yMax = Math.ceil(maxCount * 1.2 / 50) * 50;  // 向上取整到50的倍数

            this.teacherCollegeChart = new Chart(ctx.getContext('2d'), {
                type: 'line',
                data: {
                    labels: labels,  // 使用提取的学院名称
                    datasets: [{
                        label: '教师人数',
                        data: counts,  // 使用提取的人数
                        fill: true,
                        backgroundColor: 'rgba(102, 126, 234, 0.15)',
                        borderColor: '#667eea',
                        borderWidth: 3,
                        tension: 0.4,
                        pointRadius: 6,
                        pointHoverRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: yMax,  // 使用计算后的最大值
                            ticks: { stepSize: 50 }
                        },
                        x: { grid: { display: false } }
                    }
                }
            })
        }
    },
    mounted() {
        this.loadTeacherData()
    },
    beforeDestroy() {
        // 清理图表实例
        if (this.teacherGenderChart) {
            this.teacherGenderChart.destroy();
        }
        if (this.teacherCollegeChart) {
            this.teacherCollegeChart.destroy();
        }
    }
})

const studentStatsApp = new Vue({
    el: '#studentStatsApp',
    data: {
        totalStudent: 0,
        manStudent: 0,
        womanStudent: 0,
        collegeStudentList: [],
        collegeStudentCounts: [],

        // 图表实例
        studentGenderChart: null,
        studentCollegeLineChart: null
    },
    mounted() {
        this.loadStudentData()
    },
    methods: {
        async loadStudentData() {
            try {
                const response = await axios({
                    url: '/student/total',
                    method: 'get'
                })
                if (response.data.code === 0) {
                    this.totalStudent = response.data.data.totalStudent;
                    this.manStudent = response.data.data.manStudent;
                    this.womanStudent = response.data.data.womanStudent;
                    this.collegeStudentList = response.data.data.collegeStudentList;
                    this.collegeStudentCounts = response.data.data.collegeStudentCounts;
                }

                this.$nextTick(() => {
                    this.initStudentCharts()
                })
            } catch (error) {

            }
        },
        initStudentCharts() {
            this.initStudentGenderChart()
            this.initStudentCollegeLineChart()
        },
        initStudentGenderChart() {
            const ctx = this.$refs.studentGenderChart.getContext('2d')
            if (!ctx) return
            this.studentGenderChart = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: ['男生', '女生'],
                    datasets: [{
                        data: [this.manStudent, this.womanStudent],
                        backgroundColor: ['#3498db', '#e74c3c'],
                        borderWidth: 4,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '60%'
                }
            })
        },
        initStudentCollegeLineChart() {
            const ctx = this.$refs.studentCollegeLineChart;
            if (!ctx) return;

            // 从 collegeStudentList 提取数据
            const labels = this.collegeStudentList.map(item => item.collegeName);
            const counts = this.collegeStudentList.map(item => item.collegeStudent);

            // 计算 Y 轴最大值
            const maxCount = counts.length > 0 ? Math.max(...counts) : 100;
            const yMax = Math.ceil(maxCount * 1.2 / 100) * 100;  // 向上取整到100的倍数

            this.studentCollegeLineChart = new Chart(ctx.getContext('2d'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '学生人数',
                        data: counts,
                        fill: true,
                        backgroundColor: 'rgba(52, 152, 219, 0.15)',
                        borderColor: '#3498db',
                        borderWidth: 3,
                        tension: 0.4,
                        pointRadius: 6,
                        pointHoverRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: yMax,
                            ticks: { stepSize: 100 }
                        },
                        x: { grid: { display: false } }
                    }
                }
            });
        }
    },
    beforeDestroy() {
        if(this.studentGenderChart) {
            this.studentGenderChart.destroy()
        }
        if(this.studentCollegeLineChart) {
            this.studentCollegeLineChart.destroy()
        }
    }
})

const courseStatsApp = new Vue({
    el: '#courseStatsApp',
    data: {
        totalCourse: 0,
        collegeCourseList: [],
        courseCollegeLineChart: null
    },
    mounted() {
        this.loadCourseData()
    },
    methods:{
        async loadCourseData(){
            try {
                const response = await axios({
                    url: '/course/total',
                    method: 'get'
                })
                if (response.data.code === 0) {
                    this.totalCourse = response.data.data.totalCourse
                    // 后端返回的字段名是 collegeCourseVOList
                    this.collegeCourseList = response.data.data.collegeCourseVOList || []
                }

                this.$nextTick(() => {
                    this.initCourseCharts()
                })
            } catch (error) {
                console.error('获取课程数据失败', error)
                showAlert('获取课程数据失败！请重试！')
            }
        },
        initCourseCharts(){
            const ctx = this.$refs.courseCollegeLineChart
            if (!ctx) return

            // 防御性检查：确保 collegeCourseList 存在
            if (!this.collegeCourseList || this.collegeCourseList.length === 0) {
                console.warn('collegeCourseList 数据为空或未定义')
                return
            }

            // 从 collegeCourseList 提取数据
            const labels = this.collegeCourseList.map(item => item.collegeName);
            const counts = this.collegeCourseList.map(item => item.courseCount);

            // 计算 Y 轴最大值
            const maxCount = counts.length > 0 ? Math.max(...counts) : 100;
            const yMax = Math.ceil(maxCount * 1.2 / 50) * 50;  // 向上取整到50的倍数

            this.courseCollegeLineChart = new Chart(ctx.getContext('2d'), {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '课程数',
                        data: counts,
                        fill: true,
                        backgroundColor: 'rgba(155, 89, 182, 0.15)',
                        borderColor: '#9b59b6',
                        borderWidth: 3,
                        tension: 0.4,
                        pointRadius: 6,
                        pointHoverRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: yMax,
                            ticks: { stepSize: 50 }
                        },
                        x: { grid: { display: false } }
                    }
                }
            });
        },
        beforeDestroy(){
            if(this.courseCollegeLineChart){
                this.courseCollegeLineChart.destroy()
            }
        }
    }
})
