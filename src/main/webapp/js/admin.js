$(document).ready(function () {
    $('.nav-item, .list-group-item').on('click', function (e) {
        e.preventDefault();

        const target = $(this).data('target');

        $('.nav-item, .list-group-item').removeClass('active');

        $('.nav-item[data-target="' + target + '"], .list-group-item[data-target="' + target + '"]').addClass('active');

        $('#dashboard, #user-management, #file-management, #storage-usage, #settings').hide();

        $(target).show();
    });
});

const getUserStorageSizeList = async () => {
    axios.post('/admin/userStorageSizeList')
        .then(response => {
            const data = response.data;

            if (data.status === 'success') {
                const users = data.storageSizeList;

                // Populate the user table
                populateUserTable(users);

                // Render the storage chart
                renderChart(users);
            } else {
                console.error('Failed to load user data:', data.message);
            }
        })
        .catch(error => {
            console.error('Error fetching user data:', error);
        });
}

document.addEventListener('DOMContentLoaded', function () {
    getUserStorageSizeList();
});

function populateUserTable(users) {
    const userTableBody = $('#user-management').find('table').find('tbody');
    userTableBody.empty(); // 기존 행을 모두 제거

    users.forEach(user => {
        const row = $('<tr></tr>'); // jQuery 객체 생성

        const originalDate = new Date(user.registrationDate);
        const adjustedDate = new Date(originalDate.getTime() + 9 * 60 * 60 * 1000); // 9시간 더하기
        const formattedDate = adjustedDate.toISOString().split('T')[0]; // '년도-월-날짜' 형식으로 변환

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

        // row를 tbody에 추가
        userTableBody.append(row);
    });

    // 초기화 버튼에 대한 이벤트 핸들러를 동적으로 할당
    userTableBody.on('click', '.init-user', function () {
        const userID = $(this).data('userid');
        const userName = $(this).data('username');
        initUser(userID, userName).then(r => {
        });
    });

    // 삭제 버튼에 대한 이벤트 핸들러를 동적으로 할당
    userTableBody.on('click', '.delete-user', function () {
        const userID = $(this).data('userid');
        const userName = $(this).data('username');
        deleteUser(userID, userName).then(r => {
        });
    });

    // 편집 버튼에 대한 이벤트 핸들러를 동적으로 할당
    userTableBody.on('click', '.edit-user', function () {
        const user = $(this).data('user');

        // 모달에 데이터 설정
        $('#editUserId').val(user.userID);
        $('#editUserName').val(user.name);
        $('#editUserEmail').val(user.email);
        $('#editUserPassword').val(user.password); // 패스워드 필드 추가
        $('#editUserLevel').val(user.level); // 레벨 필드 추가
        $('#editUserStorageMaxSize').val(user.storageMaxSize); // 스토리지 최대 크기 필드 추가

        // 모달 표시
        $('#editUserModal').modal('show');
    });
}

const initUser = async (userID, userName) => {
    alert('Initializing user: ' + userName);
    axios.post('/file/initAll', {userID: userID})
        .then(response => {
            const data = response.data;

            if (data.status === 'success') {
                alert(`${userName} successfully initialized`);
            } else {
                console.error('Failed to init user:', data.message);
            }
        })
        .catch(error => {
            console.error('Error fetching init user:', error);
        });
}

const deleteUser = async (userID, userName) => {
    alert('Deleting user: ' + userName)
    axios.post('/user/delete', {userID: userID})
        .then(response => {
            const data = response.data;

            if (data.status === 'success') {
                alert(`${userName} successfully deleted`);
                getUserStorageSizeList();
            } else {
                console.error('Failed to delete user:', data.message);
            }
        })
        .catch(error => {
            console.error('Error fetching delete user:', error);
        });
}

$('#saveEditUser').on('click', async function() {
    alert('Edit User Start');

    const userID = $('#editUserId').val();
    const userName = $('#editUserName').val();
    const userEmail = $('#editUserEmail').val();
    const userPassword = $('#editUserPassword').val();
    const userLevel = $('#editUserLevel').val();
    const userStorageMaxSize = $('#editUserStorageMaxSize').val();

    axios.post('/user/edit', {
        userID: userID,
        name: userName,
        email: userEmail,
        password: userPassword,
        level: userLevel,
        storageMaxSize: userStorageMaxSize
    }).then(response => {
        const data = response.data;

        if (data.status === 'success') {
            alert(`${userName} successfully edited`);
            $('#editUserModal').modal('hide');
            getUserStorageSizeList();
        } else {
            console.error('Failed to edit user:', data.message);
        }
    }).catch(error => {
        console.error('Error editing user:', error);
    });
});

let storageChart = null;

// Function to render the chart
function renderChart(storageSizeList) {
    const userLabels = storageSizeList.map(user => user.name); // 사용자 이름 또는 고유 ID
    const totalSizes = storageSizeList.map(user => user.totalSize);
    const maxSizes = storageSizeList.map(user => user.storageMaxSize);

    const ctx = document.getElementById('storageChart').getContext('2d');

    // 기존 차트가 존재하면 파괴
    if (storageChart !== null) {
        storageChart.destroy();
    }

    // 새로운 차트 생성
    storageChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: userLabels,
            datasets: [{
                label: 'Total Size',
                data: totalSizes,
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }, {
                label: 'Max Size',
                data: maxSizes,
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    type: 'logarithmic'
                }
            }
        }
    });
}
