// 总学生数的饼状图
const ctx = document.getElementById('myPieChart');
if (ctx) {
    new Chart(ctx.getContext('2d'), {
        type: 'pie',
        data: {
            labels: ['男生', '女生'],
            datasets: [{
                data: [750, 500],
                backgroundColor: ['#3498db', '#e74c3c'],
                borderColor: '#fff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: 'bottom' },
                tooltip: { enabled: true }
            }
        }
    });
}

// 学院统计折线图
const ctxLine = document.getElementById('collageLineChart');
if (ctxLine) {
    new Chart(ctxLine.getContext('2d'), {
        data: {
            labels: ['信息科学与技术学院', '人文传播学院', '法学院', '会计与金融学院', '机自学院'],
            datasets: [{
                type: 'line',
                label: '各学院学生人数',
                data: [400, 300, 200, 350, 250],
                fill: true,
                borderColor: '#2ecc71',
                tension: 0.4,
                borderWidth: 3,
                backgroundColor: 'rgba(46, 204, 113,0.2)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'top' },
                tooltip: { enabled: true }
            },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

// 教师统计图表初始化函数
function initTeacherStatsCharts() {
    // 性别比例环形图
    const genderCtx = document.getElementById('teacherGenderChart');
    if (genderCtx) {
        new Chart(genderCtx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['男教师', '女教师'],
                datasets: [{
                    data: [580, 676],
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
        });
    }

    // 学院分布折线图
    const collegeCtx = document.getElementById('teacherCollegeLineChart');
    if (collegeCtx) {
        new Chart(collegeCtx.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['信息学院', '人文学院', '法学院', '会计学院', '机自学院', '外语学院', '艺术学院'],
                datasets: [{
                    label: '教师人数',
                    data: [210, 185, 160, 240, 190, 156, 115],
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
                    y: { beginAtZero: true, max: 300, ticks: { stepSize: 50 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
}

// 页面加载完成后初始化教师统计图表
document.addEventListener('DOMContentLoaded', initTeacherStatsCharts);

// 学生统计图表初始化函数
function initStudentStatsCharts() {
    // 性别比例饼图
    const genderCtx = document.getElementById('studentGenderChart');
    if (genderCtx) {
        new Chart(genderCtx.getContext('2d'), {
            type: 'pie',
            data: {
                labels: ['男生', '女生'],
                datasets: [{
                    data: [1850, 1406],
                    backgroundColor: ['#3498db', '#e74c3c'],
                    borderWidth: 4,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    // 学院分布折线图
    const collegeCtx = document.getElementById('studentCollegeLineChart');
    if (collegeCtx) {
        new Chart(collegeCtx.getContext('2d'), {
            type: 'line',
            data: {
                labels: ['信息学院', '人文学院', '法学院', '会计学院', '机自学院', '外语学院', '艺术学院'],
                datasets: [{
                    label: '学生人数',
                    data: [580, 420, 380, 520, 460, 320, 276],
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
                    y: { beginAtZero: true, max: 700, ticks: { stepSize: 100 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }
}

// 页面加载完成后初始化学生统计图表
document.addEventListener('DOMContentLoaded', initStudentStatsCharts);

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
