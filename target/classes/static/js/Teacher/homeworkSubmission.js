/**
 * 作业提交情况页面 - Vue2 应用
 */

// 获取作业信息
function getHomeworkInfo() {
    const homeworkStr = localStorage.getItem('currentHomework');
    if (homeworkStr) {
        try {
            return JSON.parse(homeworkStr);
        } catch (e) {
            console.error('解析作业信息失败:', e);
            return null;
        }
    }
    return null;
}

// 获取教师ID
function getTeacherId() {
    const teacherUsername = localStorage.getItem('username');
    if (teacherUsername) {
        return parseInt(teacherUsername.slice(-4), 10);
    }
    const savedTeacherId = localStorage.getItem('teacherId');
    if (savedTeacherId) {
        try {
            const parsed = JSON.parse(savedTeacherId);
            return typeof parsed === 'number' ? parsed : parseInt(parsed, 10);
        } catch (e) {
            return parseInt(savedTeacherId, 10);
        }
    }
    return 0;
}

// 获取作业ID
function getHomeworkId() {
    const homework = getHomeworkInfo();
    if (homework && homework.id) {
        return homework.id;
    }
    const urlParams = new URLSearchParams(window.location.search);
    const homeworkId = urlParams.get('homeworkId');
    if (homeworkId) {
        return parseInt(homeworkId, 10);
    }
    return null;
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 作业提交情况 Vue 实例
const homeworkSubmissionApp = new Vue({
    el: '#homework-submission-app',
    data: {
        // 作业信息
        homeworkId: null,
        teacherId: null,
        // 原始数据列表（全部数据）
        allSubmissions: [],
        // 筛选后的数据列表
        submissionList: [],
        // 加载状态
        loading: true,
        // 加载更多状态
        loadingMore: false,
        // 分页相关
        currentPage: 1,
        pageSize: 25,
        total: 0,
        // 筛选状态: 'all'(全部), 'submitted'(已提交), 'pending'(未提交), 'graded'(已批改)
        filterStatus: 'all',
        // 防抖定时器
        scrollTimeout: null
    },
    computed: {
        // 筛选后的数据列表（用于显示）
        filteredList() {
            return this.submissionList;
        },
        // 根据筛选状态计算总数
        filteredTotal() {
            if (this.filterStatus === 'all') {
                return this.allSubmissions.length;
            }
            return this.allSubmissions.filter(item => {
                if (this.filterStatus === 'submitted') {
                    return item.submitStatus === 1;
                } else if (this.filterStatus === 'pending') {
                    return item.submitStatus === 0;
                } else if (this.filterStatus === 'graded') {
                    return item.submitStatus === 2;
                }
                return true;
            }).length;
        }
    },
    methods: {
        /**
         * 加载指定页的数据
         * @param {number} pageNum - 页码
         */
        async loadPage(pageNum) {
            if (!this.homeworkId || !this.teacherId) {
                return [];
            }

            try {
                const response = await axios({
                    url: `/homework/teacher/homework/detail/${this.homeworkId}`,
                    method: 'get',
                    params: {
                        teacherId: this.teacherId,
                        pagenum: pageNum,
                        pagesize: this.pageSize
                    }
                });

                console.log('第' + pageNum + '页响应:', response.data);

                if (response.data.code === 0) {
                    const data = response.data.data || [];
                    return data;
                } else {
                    console.error('获取数据失败', response.data.message);
                    return [];
                }
            } catch (error) {
                console.error('请求第' + pageNum + '页失败', error);
                return [];
            }
        },

        /**
         * 初始加载：获取第一页数据
         */
        async loadFirstPage() {
            this.loading = true;
            this.allSubmissions = [];
            this.submissionList = [];

            console.log('加载第1页数据...');

            const data = await this.loadPage(1);

            this.allSubmissions = data;
            this.filterSubmissions(this.filterStatus);  // 应用筛选
            this.currentPage = 1;
            this.total = 25;

            console.log('第一页数据: ' + this.submissionList.length + ' 条');

            this.loading = false;
        },

        /**
         * 加载更多数据
         */
        async loadMore() {
            if (this.loadingMore) {
                return;
            }

            const nextPage = this.currentPage + 1;
            this.loadingMore = true;

            console.log('加载第' + nextPage + '页数据...');

            const data = await this.loadPage(nextPage);

            if (data.length > 0) {
                // 追加到原始数据
                this.allSubmissions = this.allSubmissions.concat(data);
                // 重新应用筛选
                this.applyFilter();
                this.currentPage = nextPage;
                console.log('当前共 ' + this.submissionList.length + ' 条数据');
            } else {
                // 没有更多数据了
                this.total = this.submissionList.length;
                console.log('没有更多数据了，总共 ' + this.total + ' 条');
            }

            this.loadingMore = false;
        },

        /**
         * 应用筛选
         */
        applyFilter() {
            if (this.filterStatus === 'all') {
                // 全部：显示所有数据
                this.submissionList = [...this.allSubmissions];
            } else if (this.filterStatus === 'submitted') {
                // 已提交：submitStatus = 1
                this.submissionList = this.allSubmissions.filter(item => item.submitStatus === 1);
            } else if (this.filterStatus === 'pending') {
                // 未提交：submitStatus = 0
                this.submissionList = this.allSubmissions.filter(item => item.submitStatus === 0);
            } else if (this.filterStatus === 'graded') {
                // 已批改：submitStatus = 2
                this.submissionList = this.allSubmissions.filter(item => item.submitStatus === 2);
            }
            console.log('筛选完成: ' + this.submissionList.length + ' 条数据');
        },

        /**
         * 筛选提交状态
         */
        async filterSubmissions(status) {
            this.filterStatus = status;
            this.currentPage = 1;
            // 重新应用筛选
            this.applyFilter();
            console.log('切换筛选条件: ' + status + ', 共 ' + this.submissionList.length + ' 条');
        },

        /**
         * 获取提交状态样式类
         */
        getStatusClass(status) {
            switch (status) {
                case 1:
                    return 'status-submitted';
                case 0:
                    return 'status-pending';
                case 2:
                    return 'status-graded';
                default:
                    return 'status-pending';
            }
        },

        /**
         * 获取提交状态文本
         */
        getStatusText(status) {
            switch (status) {
                case 1:
                    return '已提交';
                case 0:
                    return '未提交';
                case 2:
                    return '已批改';
                default:
                    return '未知';
            }
        },

        /**
         * 处理滚动事件（带防抖）
         */
        handleScroll() {
            // 清除之前的定时器
            if (this.scrollTimeout) {
                clearTimeout(this.scrollTimeout);
            }

            // 设置新的定时器，100ms后才真正执行
            this.scrollTimeout = setTimeout(() => {
                const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
                const windowHeight = window.innerHeight || document.documentElement.clientHeight;
                const documentHeight = document.documentElement.scrollHeight || document.body.scrollHeight;

                // 距离底部 800px 时触发加载（需要较大滚动幅度）
                if (scrollTop + windowHeight >= documentHeight - 800) {
                    this.loadMore();
                }
            }, 100);
        },

        /**
         * 初始化页面
         */
        async init() {
            // 获取教师ID
            this.teacherId = getTeacherId();
            console.log('获取到的教师ID:', this.teacherId);

            // 获取作业ID
            this.homeworkId = getHomeworkId();
            console.log('获取到的作业ID:', this.homeworkId);

            if (!this.homeworkId) {
                console.error('作业ID缺失');
                showAlert('作业ID缺失，请从作业管理页面重新进入');
                this.loading = false;
                return;
            }

            if (!this.teacherId) {
                console.error('教师ID缺失');
                showAlert('教师ID缺失，请重新登录');
                this.loading = false;
                return;
            }

            // 更新localStorage
            localStorage.setItem('teacherId', this.teacherId);

            const currentHomework = getHomeworkInfo();
            if (!currentHomework || !currentHomework.id) {
                localStorage.setItem('currentHomework', JSON.stringify({ id: this.homeworkId }));
            }

            // 更新面包屑标题
            const homeworkInfo = getHomeworkInfo();
            if (homeworkInfo && homeworkInfo.title) {
                const breadcrumbTitle = document.getElementById('breadcrumb-title');
                if (breadcrumbTitle) {
                    breadcrumbTitle.textContent = homeworkInfo.title + ' - 提交情况';
                }
            }

            // 更新URL参数
            const url = new URL(window.location.href);
            if (!url.searchParams.has('homeworkId')) {
                url.searchParams.set('homeworkId', this.homeworkId);
                window.history.replaceState({}, '', url);
            }

            // 添加滚动监听（带防抖）
            window.addEventListener('scroll', this.handleScroll);

            // 加载数据
            await this.loadFirstPage();
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.init();
        });
    },
    beforeDestroy() {
        window.removeEventListener('scroll', this.handleScroll);
        if (this.scrollTimeout) {
            clearTimeout(this.scrollTimeout);
        }
    }
});

/**
 * 返回作业管理页面
 */
function goBack() {
    window.location.href = 'TKKCteacher.html';
}

/**
 * 显示危险提示框
 */
function showAlert(message) {
    const alertEl = document.getElementById('alert-DangerB');
    if (!alertEl) {
        alert(message);
        return;
    }

    alertEl.innerHTML = message;
    alertEl.style.display = 'block';
    setTimeout(() => {
        alertEl.style.opacity = '1';
    }, 10);
    setTimeout(() => {
        alertEl.style.opacity = '0';
        setTimeout(() => {
            alertEl.style.display = 'none';
        }, 300);
    }, 4000);
}

/**
 * 显示成功提示框
 */
function showSuccessAlert(message) {
    const alertEl = document.getElementById('alert-SuccessMessage');
    if (!alertEl) {
        alert(message);
        return;
    }

    alertEl.textContent = message;
    alertEl.style.display = 'block';
    setTimeout(() => {
        alertEl.style.opacity = '1';
    }, 10);
    setTimeout(() => {
        alertEl.style.opacity = '0';
        setTimeout(() => {
            alertEl.style.display = 'none';
        }, 300);
    }, 4000);
}
