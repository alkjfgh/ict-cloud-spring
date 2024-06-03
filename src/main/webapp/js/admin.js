$(document).ready(function () {
    let searchResults = []; // Array to store search results
    let currentPage = 1;
    const resultsPerPage = 10; // Number of results per page

    $('.nav-item, .list-group-item').on('click', function (e) {
        e.preventDefault();
        const target = $(this).data('target');
        $('.nav-item, .list-group-item').removeClass('active');
        $('.nav-item[data-target="' + target + '"], .list-group-item[data-target="' + target + '"]').addClass('active');
        $('#dashboard, #user-management, #file-management, #storage-usage, #settings').hide();
        $(target).show();
    });

    document.getElementById('searchForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const filename = document.getElementById('filename').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        const minFileSize = document.getElementById('minFileSize').value;
        const minFileSizeUnit = document.getElementById('minFileSizeUnit').value;
        const minFileSizeInBytes = convertToBytes(minFileSize, minFileSizeUnit);

        const maxFileSize = document.getElementById('maxFileSize').value;
        const maxFileSizeUnit = document.getElementById('maxFileSizeUnit').value;
        const maxFileSizeInBytes = convertToBytes(maxFileSize, maxFileSizeUnit);

        const userId = document.getElementById('userId').value;

        axios.get('/file/searchFiles', {
            params: {
                filename: filename,
                startDate: startDate,
                endDate: endDate,
                minFileSize: minFileSizeInBytes,
                maxFileSize: maxFileSizeInBytes,
                userId: userId
            }
        }).then(function (response) {
            searchResults = response.data;
            currentPage = 1;
            renderPage();
            renderPaginationControls();
        }).catch(function (error) {
            console.error('검색 중 오류 발생:', error);
        });
    });

    function renderPage() {
        const resultsContainer = document.getElementById('searchResults');
        resultsContainer.innerHTML = '';

        const start = (currentPage - 1) * resultsPerPage;
        const end = start + resultsPerPage;
        const pageResults = searchResults.slice(start, end);

        if (pageResults.length === 0) {
            resultsContainer.innerHTML = '<p>검색 결과가 없습니다.</p>';
        } else {
            const table = document.createElement('table');
            table.classList.add('table', 'table-bordered');

            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');
            const headers = ['파일 ID', '파일 이름', '파일 크기', '업로드 날짜', '사용자 ID'];
            headers.forEach(header => {
                const th = document.createElement('th');
                th.innerText = header;
                headerRow.appendChild(th);
            });
            thead.appendChild(headerRow);
            table.appendChild(thead);

            const tbody = document.createElement('tbody');
            pageResults.forEach(file => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${file.fileID}</td>
                    <td>${file.filename}</td>
                    <td>${formatBytes(file.fileSize)}</td>
                    <td>${new Date(file.uploadDate).toLocaleDateString()}</td>
                    <td>${file.userID}</td>
                `;
                tbody.appendChild(row);
            });
            table.appendChild(tbody);
            resultsContainer.appendChild(table);
        }
    }

    function renderPaginationControls() {
        const totalPages = Math.ceil(searchResults.length / resultsPerPage);
        const paginationContainer = document.getElementById('pagination');
        paginationContainer.innerHTML = '';

        for (let i = 1; i <= totalPages; i++) {
            const pageItem = document.createElement('li');
            pageItem.classList.add('page-item');
            if (i === currentPage) {
                pageItem.classList.add('active');
            }
            pageItem.innerHTML = `<a class="page-link" href="#">${i}</a>`;
            pageItem.addEventListener('click', function (e) {
                e.preventDefault();
                currentPage = i;
                renderPage();
                renderPaginationControls();
            });
            paginationContainer.appendChild(pageItem);
        }
    }

    document.getElementById('backupButton').addEventListener('click', function () {
        axios.post('/file/backup', {}, {responseType: 'blob'})
            .then(response => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'backup.zip');
                document.body.appendChild(link);
                link.click();
            })
            .catch(error => {
                console.error('Backup error:', error);
                alert('백업 실패');
            });
    });

    document.getElementById('restoreButton').addEventListener('click', function () {
        document.getElementById('restoreFileInput').click();
    });

    document.getElementById('restoreFileInput').addEventListener('change', function () {
        const file = document.getElementById('restoreFileInput').files[0];
        const formData = new FormData();
        formData.append('file', file);

        axios.post('/file/restore', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
            .then(response => {
                alert('복원 성공');
            })
            .catch(error => {
                console.error('Restore error:', error);
                alert('복원 실패');
            });
    });

    document.getElementById('noticeForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const title = document.getElementById('noticeTitle').value;
        const content = document.getElementById('noticeContent').value;

        const formData = new URLSearchParams();
        formData.append('title', title);
        formData.append('content', content);

        axios.post('/admin/uploadNotice', formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }).then(response => {
            if (response.data.status === 'success') {
                alert('공지사항 업로드 성공');
                document.getElementById('noticeForm').reset();
            } else {
                alert('공지사항 업로드 실패: ' + response.data.message);
            }
        }).catch(error => {
            console.error('공지사항 업로드 중 오류 발생:', error);
            alert('공지사항 업로드 중 오류 발생');
        });
    });

    $('.logout-link').on('click', async (event) => {
        event.preventDefault();

        const response = await axios.get("/user/logout");
        console.log(response);
        if(response.status === 200){
            location.href = "../main";
        }
    });

    loadLogFiles();
    fetchServerStatus();
    startUptimeUpdater();
    // Optionally, resynchronize server status every 5 minutes to adjust the server start time
    setInterval(fetchServerStatus, 5 * 60 * 1000);
    fetchDatabaseStatus();
    fetchStorageUsage();

    getUserStorageSizeList().then(users => {
        populateUserTable(users);
        renderChart(users);
    });

    activitySocket();
});

let currentOpenFile = ''; // 현재 열린 파일 이름을 추적하는 변수
let serverStartTime = null; // Will store the initial server start time

const loadLogFiles = () => {
    axios.get('/api/logs')
        .then(function (response) {
            const logFiles = response.data.reverse(); // Reverse the order of log files
            const logFilesList = $('#logFilesCarouselInner');
            logFilesList.empty();
            let carouselItem;
            let row;
            let col;
            logFiles.forEach((file, index) => {
                if (index % 9 === 0) { // Every 9 files, create a new carousel item
                    carouselItem = $('<div class="carousel-item"></div>');
                    if (index === 0) carouselItem.addClass('active');
                    logFilesList.append(carouselItem);
                    row = $('<div class="row text-center"></div>'); // Center-align text
                    carouselItem.append(row);
                }
                if (index % 3 === 0) { // Every 3 files, create a new column
                    col = $('<div class="col-md-4"></div>');
                    row.append(col);
                }
                const listItem = $('<li class="list-group-item logfile"></li>').text(file).click(function () {
                    // 파일명이 현재 열린 파일과 같으면 내용을 숨김
                    if (currentOpenFile === file) {
                        $('#logContent').hide();
                        currentOpenFile = ''; // 현재 열린 파일 변수를 초기화
                    } else {
                        loadLogContent(file);
                        currentOpenFile = file; // 현재 열린 파일을 업데이트
                    }
                });
                col.append(listItem);
            });
        })
        .catch(function (error) {
            console.error('Error fetching log files:', error);
        });
}

const loadLogContent = (fileName) => {
    axios.get(`/api/logs/${fileName}`)
        .then(function (response) {
            $('#logContent').show();
            const logText = response.data;
            const logLines = logText.split('\n');
            let formattedLogLines = [];
            let currentClass = '';
            let currentLogEntry = '';

            logLines.forEach(line => {
                if (line.includes('[INFO')) {
                    if (currentLogEntry) {
                        formattedLogLines.push(`<span class="${currentClass}">${currentLogEntry}</span>`);
                        currentLogEntry = '';
                    }
                    currentClass = 'log-info';
                } else if (line.includes('[ERROR')) {
                    if (currentLogEntry) {
                        formattedLogLines.push(`<span class="${currentClass}">${currentLogEntry}</span>`);
                        currentLogEntry = '';
                    }
                    currentClass = 'log-error';
                } else if (line.includes('[WARN')) {
                    if (currentLogEntry) {
                        formattedLogLines.push(`<span class="${currentClass}">${currentLogEntry}</span>`);
                        currentLogEntry = '';
                    }
                    currentClass = 'log-warning';
                } else if (line.includes('[DEBUG')) {
                    if (currentLogEntry) {
                        formattedLogLines.push(`<span class="${currentClass}">${currentLogEntry}</span>`);
                        currentLogEntry = '';
                    }
                    currentClass = 'log-debug';
                }

                currentLogEntry += `${line}\n`;
            });

            if (currentLogEntry) {
                formattedLogLines.push(`<span class="${currentClass}">${currentLogEntry}</span>`);
            }

            $('#logText').html(formattedLogLines.join('<br>'));
        })
        .catch(function (error) {
            console.error('Error fetching log content:', error);
        });
}

const getUserStorageSizeList = async () => {
    let users = null;

    try {
        const response = await axios.post('/admin/userStorageSizeList');
        const data = response.data;
        if (data.status === 'success') {
            users = data.storageSizeList;
        } else {
            console.error('Failed to load user data:', data.message);
        }
    } catch (error) {
        console.error('Error fetching user data:', error);
    }

    return users;
}

function populateUserTable(users) {
    const userTableBody = $('#user-management').find('table').find('tbody');
    userTableBody.empty();

    users.forEach(user => {
        const row = $('<tr></tr>');
        const originalDate = new Date(user.registrationDate);
        const adjustedDate = new Date(originalDate.getTime() + 9 * 60 * 60 * 1000);
        const formattedDate = adjustedDate.toISOString().split('T')[0];

        row.html(`
            <td>${user.userID}</td>
            <td>${user.name}</td>
            <td>${user.email}</td>
            <td>${formattedDate}</td>
            <td>
                <button class="btn btn-warning btn-sm init-user" data-userid="${user.userID}" data-username="${user.name}">초기화</button>
                <button class="btn btn-primary btn-sm edit-user" data-user='${JSON.stringify(user)}'>편집</button>
                <button class="btn btn-danger btn-sm delete-user" data-userid="${user.userID}" data-username="${user.name}">삭제</button>
            </td>
        `);

        userTableBody.append(row);
    });

    userTableBody.off('click', '.init-user');
    userTableBody.on('click', '.init-user', async function () {
        const userID = $(this).data('userid');
        const userName = $(this).data('username');
        await initUser(userID, userName);
    });

    userTableBody.off('click', '.delete-user');
    userTableBody.on('click', '.delete-user', async function () {
        const userID = $(this).data('userid');
        const userName = $(this).data('username');
        await deleteUser(userID, userName);
    });

    userTableBody.off('click', '.edit-user');
    userTableBody.on('click', '.edit-user', function () {
        const user = $(this).data('user');

        $('#editUserId').val(user.userID);
        $('#editUserName').val(user.name);
        $('#editUserEmail').val(user.email);
        $('#editUserPassword').val(user.password);
        $('#editUserLevel').val(user.level);
        $('#editUserStorageMaxSize').val(user.storageMaxSize);
        $('#editUserModal').modal('show');
    });
}

const initUser = async (userID, userName) => {
    alert('Initializing user: ' + userName);
    try {
        const response = await axios.post('/file/initAll', {userID});
        const data = response.data;
        if (data.status === 'success') {
            alert(`${userName} successfully initialized`);
            getUserStorageSizeList().then(users => {
                populateUserTable(users);
                renderChart(users);
            });
        } else {
            alert('Failed to init user: ' + data.message);
        }
    } catch (error) {
        console.error('Error initializing user:', error);
    }
}

const deleteUser = async (userID, userName) => {
    alert('Deleting user: ' + userName);
    try {
        const response = await axios.post('/user/delete', {userID});
        const data = response.data;
        if (data.status === 'success') {
            alert(`${userName} successfully deleted`);
            getUserStorageSizeList().then(users => {
                populateUserTable(users);
                renderChart(users);
            });
            // loadLogFiles();
        } else {
            console.error('Failed to delete user:', data.message);
        }
    } catch (error) {
        console.error('Error deleting user:', error);
    }
}

$('#saveEditUser').on('click', async function () {
    alert('Edit User Start');
    const userID = $('#editUserId').val();
    const userName = $('#editUserName').val();
    const userEmail = $('#editUserEmail').val();
    const userPassword = $('#editUserPassword').val();
    const userLevel = $('#editUserLevel').val();
    const userStorageMaxSize = $('#editUserStorageMaxSize').val();

    try {
        const response = await axios.post('/user/edit', {
            userID,
            name: userName,
            email: userEmail,
            password: userPassword,
            level: userLevel,
            storageMaxSize: userStorageMaxSize
        });
        const data = response.data;
        if (data.status === 'success') {
            alert(`${userName} successfully edited`);
            $('#editUserModal').modal('hide');
            getUserStorageSizeList().then(users => {
                populateUserTable(users);
                renderChart(users);
            });
            // loadLogFiles();
        } else {
            console.error('Failed to edit user:', data.message);
        }
    } catch (error) {
        console.error('Error editing user:', error);
    }
});

let storageCharts = {};

function renderChart(storageSizeList) {
    const chartContainer = document.getElementById('storageChart');
    chartContainer.innerHTML = ''; // 기존 차트를 초기화

    storageSizeList.forEach((user, index) => {
        // 차트를 감쌀 div 요소 생성
        const chartItem = document.createElement('div');
        chartItem.className = 'chart-item';

        // 캔버스 요소 생성
        const chartCanvas = document.createElement('canvas');
        chartCanvas.id = `storageChart-${index}`;
        chartItem.appendChild(chartCanvas);
        chartContainer.appendChild(chartItem);

        const usedSize = user.totalSize;
        const remainingSize = user.storageMaxSize - user.totalSize;
        const ctx = chartCanvas.getContext('2d');

        if (storageCharts[user.name]) {
            storageCharts[user.name].destroy();
        }

        storageCharts[user.name] = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['Used Size', 'Remaining Size'],
                datasets: [{
                    data: [usedSize, remainingSize],
                    backgroundColor: [
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 99, 132, 0.2)'
                    ],
                    borderColor: [
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 99, 132, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    title: {
                        display: true,
                        text: user.name
                    }
                }
            }
        });
    });
}

function parseDuration(duration) {
    const parts = duration.split(':');
    const hours = parseInt(parts[0], 10);
    const minutes = parseInt(parts[1], 10);
    const seconds = parseInt(parts[2], 10);
    return hours * 3600 * 1000 + minutes * 60 * 1000 + seconds * 1000; // convert to milliseconds
}

function fetchServerStatus() {
    axios.get('/api/system-status/server')
        .then(response => {
            const data = response.data;
            $('#serverStatusText').text(data.status);

            // If serverStartTime not set, calculate it based on the current time minus the uptime duration
            if (!serverStartTime) {
                const uptimeDuration = parseDuration(data.uptime); // Parse the duration into milliseconds
                serverStartTime = new Date(new Date() - uptimeDuration); // Set the start time by subtracting duration from the current time
                startUptimeUpdater(); // Start the timer to update the display
                console.log(data.uptime);
                console.log(serverStartTime);
            }
        })
        .catch(error => {
            console.error('Error fetching server status:', error);
        });
}

// Function to update uptime every second
function startUptimeUpdater() {
    setInterval(() => {
        if (serverStartTime) {
            const now = new Date();
            const uptime = new Date(now - serverStartTime);
            const uptimeStr = formatUptime(uptime);
            $('#serverUptime').text(uptimeStr);
        }
    }, 1000);
}

// Format uptime into a human-readable format
function formatUptime(uptime) {
    const hours = uptime.getUTCHours();
    const minutes = uptime.getUTCMinutes();
    const seconds = uptime.getUTCSeconds();
    return `${hours}h ${minutes}m ${seconds}s`;
}

function fetchDatabaseStatus() {
    axios.get('/api/system-status/database')
        .then(response => {
            const data = response.data;
            $('#databaseStatusText').text(data.status);
            $('#databaseSize').text(formatBytes(data.dbSize)); // 변환 함수 적용
        })
        .catch(error => {
            console.error('Error fetching database status:', error);
        });
}

function fetchStorageUsage() {
    axios.get('/api/system-status/storage')
        .then(response => {
            const data = response.data;
            $('#usedSpace').text(formatBytes(data.usedSpace)); // 변환 함수 적용
            $('#totalSpace').text(formatBytes(data.totalSpace)); // 변환 함수 적용
        })
        .catch(error => {
            console.error('Error fetching storage usage:', error);
        });
}

function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

function convertToBytes(size, unit) {
    const units = {
        B: 1,
        KB: 1024,
        MB: 1024 * 1024,
        GB: 1024 * 1024 * 1024
    };
    return size * (units[unit] || 1);
}

const activitySocket = () => {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/fileActivity', function (activity) {
            console.log('Received: ' + activity.body); // 메시지 수신 시 콘솔에 출력
            console.log(activity)
            // showActivity(JSON.parse(activity.body));
            showActivity(activity.body);
        });
    }, function (error) {
        console.error('STOMP error: ' + error); // 연결 실패 시 콘솔에 출력
    });

    function showActivity(message) {
        const activityLog = document.getElementById('activityLog');
        const newItem = document.createElement('li');
        newItem.classList.add('list-group-item');
        newItem.classList.add('real-activity-log');
        newItem.textContent = message;
        activityLog.insertBefore(newItem, activityLog.firstChild);

        // 최대 50개 항목 유지
        while (activityLog.children.length > 50) {
            activityLog.removeChild(activityLog.lastChild);
        }
    }
}