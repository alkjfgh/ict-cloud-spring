$(document).ready(function () {
    let password;
    getUserInfo().then(data => {
        if (data !== null) {
            const user = data.user;
            const containers = document.querySelectorAll('.container small');

            containers.item(0).innerHTML = user.name;
            containers.item(1).innerHTML = user.email;
            containers.item(2).innerHTML = user.registrationDate;
            password = user.password;
        }
    });

    $('#change-password').on('click', function () {
        const inputList = $('.modal-body').find('input');
        const passwordArray = [inputList[0].value, inputList[1].value, inputList[2].value];

        if (passwordArray[0].length === 0 || passwordArray[1].length === 0 || passwordArray[2].length === 0) {
            alert('must insert password');
            return false;
        }

        if (passwordArray[1] !== passwordArray[2]) {
            alert('check change password equals');
            return false;
        }

        if(!isValidPassword(passwordArray[2])){
            alert('check password is current format');
            return false;
        }

        if(password !== passwordArray[0]){
            alert('check existing password')
            return false;
        }

        updatePassword(passwordArray[2]).then(check => {
            if (check) $('#close-modal').click();
            else {
                inputList.forEach((input) => {
                    input.val("");
                });
            }
        })
    });

    $('#sign-out-link').on('click', function () {
        signOut().then(check => {
            if(check) location.href = "../main";
        });
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

const updatePassword = async (changePassword) => {
    let check = false;

    await axios.post('/user/updatePassword', {changePassword}).then(res => {
        if (res.status === 200) {
            alert('change password success');
            check = true;
        } else {
            alert('Something error happened');
        }
    });

    return check;
}

const signOut = async () => {
    let check = false;

    await axios.post('/user/signOut').then(res => {
        if (res.status === 200) {
            alert('sign out success');
            check = true;
        } else {
            alert('Something error happened');
        }
    });

    return check;
}

function isValidPassword(password) {
    let passwd_regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,16}$/;
    return passwd_regex.test(password);
}