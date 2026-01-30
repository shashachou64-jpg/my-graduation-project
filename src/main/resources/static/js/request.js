axios.interceptors.request.use(function (config) {
    const token = localStorage.getItem('token');

    if (token) {
        // 标准 JWT 携带方式：Bearer + 空格 + token
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});

// 3. 【强烈推荐加上】响应拦截器：处理 token 失效（401）
axios.interceptors.response.use(function (response) {
    // 正常返回，直接放行
    return response;
}, function (error) {
    // 如果后端返回 401（token 过期或无效）
    if (error.response && error.response.status === 401) {
        // 清除失效的 token
        localStorage.removeItem('token');

        // 提示用户（可选）
        alert('登录状态已失效，请重新登录');

        // 跳转到登录页
        window.location.href = '/index.html';
    }

    // 其他错误继续抛出
    return Promise.reject(error);
});