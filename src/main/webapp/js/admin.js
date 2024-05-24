$(document).ready(function () {
    $('.nav-link, .list-group-item').on('click', function (e) {
        e.preventDefault();

        const target = $(this).data('target');

        $('.nav-link, .list-group-item').removeClass('active');
        $(this).addClass('active');

        $('#dashboard, #user-management, #file-management, #storage-usage, #settings').hide();
        $(target).show();
    });
});

document.addEventListener('DOMContentLoaded', function () {
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

    getUserStorageSizeList();
});
function populateUserTable(users) {
    const userTableBody = $('#user-management').find('table');
    userTableBody.innerHTML = ''; // Clear existing rows

    console.log(users)
    users.forEach(user => {
        const row = document.createElement('tr');

        // registrationDate에 9시간 더하기
        const originalDate = new Date(user.registrationDate);
        const adjustedDate = new Date(originalDate.getTime() + 9 * 60 * 60 * 1000); // 9시간 더하기
        const formattedDate = adjustedDate.toISOString().split('T')[0]; // '년도-월-날짜' 형식으로 변환

        row.innerHTML = `
                <td>${user.userID}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${formattedDate}</td>
                <td>
                    <button class="btn btn-primary btn-sm">편집</button>
                    <button class="btn btn-danger btn-sm">삭제</button>
                </td>
            `;

        userTableBody.append(row);
    });
}
// Function to render the chart
function renderChart(storageSizeList) {
    const userLabels = storageSizeList.map(user => user.name); // 사용자 이름 또는 고유 ID
    const totalSizes = storageSizeList.map(user => user.totalSize);
    const maxSizes = storageSizeList.map(user => user.storageMaxSize);

    const ctx = document.getElementById('storageChart').getContext('2d');
    new Chart(ctx, {
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