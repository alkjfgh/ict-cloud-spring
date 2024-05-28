$(document).ready(function () {
    getUserInfo().then(data => {
        if (data !== null) {
            const user = data.user;
            const containers = document.querySelectorAll('.container small');

            containers.item(0).innerHTML = user.name;
            containers.item(1).innerHTML = user.email;
            containers.item(2).innerHTML = user.registrationDate;
        }
    });
});

const getUserInfo = async () => {
    let data = null;

    await axios.get('/user/getInfo')
        .then(res => {
            if (res.status === 200) {
                data = res.data;
            } else {
                alert('Something error happened');
            }
        });

    return data;
}