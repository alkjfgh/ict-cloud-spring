$(document).ready(function () {
    $('.nav-item, .list-group-item').on('click', function (e) {
        e.preventDefault();
        const target = $(this).data('target');
        $('.nav-item, .list-group-item').removeClass('active');
        $('.nav-item[data-target="' + target + '"], .list-group-item[data-target="' + target + '"]').addClass('active');
        $('#dashboard, #user-management, #file-management, #storage-usage, #settings').hide();
        $(target).show();
    });

    loadLogFiles();

    getUserStorageSizeList().then(users => {
        populateUserTable(users);
        renderChart(users);
    });
});


const loadLogFiles = () => {
    axios.get('/api/logs')
        .then(function (response) {
            const logFiles = response.data;
            const logFilesList = $('#logFiles');
            logFilesList.empty();
            logFiles.forEach(function (file) {
                const listItem = $('<li class="list-group-item">')
                    .text(file)
                    .click(function () {
                        loadLogContent(file);
                    });
                logFilesList.append(listItem);
            });
        })
        .catch(function (error) {
            console.error('Error fetching log files:', error);
        });
}

const loadLogContent = (fileName) => {
    axios.get(`/api/logs/${fileName}`)
        .then(function(response) {
            $('#logContent').show();
            $('#logText').text(response.data);
        })
        .catch(function(error) {
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
        const response = await axios.post('/file/initAll', { userID });
        const data = response.data;
        if (data.status === 'success') {
            alert(`${userName} successfully initialized`);
        } else {
            console.error('Failed to init user:', data.message);
        }
    } catch (error) {
        console.error('Error initializing user:', error);
    }
}

const deleteUser = async (userID, userName) => {
    alert('Deleting user: ' + userName);
    try {
        const response = await axios.post('/user/delete', { userID });
        const data = response.data;
        if (data.status === 'success') {
            alert(`${userName} successfully deleted`);
            getUserStorageSizeList();
            loadLogFiles();
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
            getUserStorageSizeList();
            loadLogFiles();
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

        console.log(usedSize, remainingSize);

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