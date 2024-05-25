$(document).ready(function(){
    showErrorAlert();
    setEmailField();
});

function showErrorAlert(){
    var errorCode = $('#errorCode').val();

    if(errorCode){
        switch(errorCode){
            case '7000':
                alert("계정이 잠겼습니다. 비밀번호 찾기 후 로그인 해 주세요.");
                break;
            case '7010':
                alert("아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해주세요.");
                break;
            case '9999':
                alert("알 수 없는 오류로 로그인 요청을 처리할 수 없습니다. 관리자에게 문의하세요.");
                break;
        }
    }
}

function setEmailField(){
    var email = cookieUtil.getCookie("inputEmail");
    if(email != null){
        $('#inputEmail').val(email);
    }

    cookieUtil.deleteCookie("inputEmail");
}

function isValidLogin(){
    var inputEmail = $('#inputEmail').val();
    var inputPassword = $('#inputPassword').val();

    if(inputEmail.length < 1){
        alert("이메일을 입력해주세요.");
        return false;
    }
    if(inputPassword.length < 1){
        alert("비밀번호를 입력해주세요.");
        return false;
    }

    return true;
}