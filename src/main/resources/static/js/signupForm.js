function setEmailField(){
    var email = cookieUtil.getCookie("inputEmail");
    if(email != null){
        $('#authEmail').val(email);
    }
}

$(document).ready(function(){
    setEmailField();
});

function signup(){
    if(cookieUtil.getCookie("nicknameCertYn") != "Y"){
        alert("닉네임 중복 여부를 확인하세요.");
        return;
    }

    var signupData = JSON.stringify({
        email: $('#authEmail').val(),
        birthDate : $('#birthDate').val(),
        nickname : $('#nickname').val(),
        password : $('#password').val(),
        agreeTermsUse : $('#termOfUse').prop('checked'),
        agreeTermsPrivacy : $('#termOfPrivacy').prop('checked'),
        emailKey : sessionStorage.getItem("emailKey"),
        nicknameKey : sessionStorage.getItem("nicknameKey")
    });

    $.ajax({
        type: "POST",
        url: "/api/signup",
        contentType: 'application/json',
        data: signupData,
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                alert(result.resultMsg);
                cookieUtil.deleteCookie("nicknameCertYn");
                deleteSessionStorage();
                location.href="/community/login/signupSuccess?enUserId="+result.data.enUserId;
            } else if(result.resultCode == "9999"){
                alert("회원가입이 정상적으로 처리되지 않았습니다. 다시 시도해주세요.");
                deleteSessionStorage();
                deleteSignupCookie();
                location.href="/community/login";
            } else if(result.resultCode == "9998" || result.resultCode == "9990" ||
                        result.resultCode == "9970"  || result.resultCode == "9961" ||
                        result.resultCode == "9950" || result.resultCode == "9951" ||
                        result.resultCode == "9930"){
                alert(result.resultMsg);
            } else if(result.resultCode == "9980" || result.resultCode == "9960"){
                alert(result.resultMsg);
                deleteSessionStorage();
                deleteSignupCookie();
                location.href="/community/login";
            }
        }, error: function(xhr){

        }
    });
}

function deleteSignupCookie(){
    cookieUtil.deleteCookie("nicknameCertYn");
    cookieUtil.deleteCookie("inputEmail");
}

function deleteSessionStorage(){
    sessionStorage.clear();
}

function checkNickName(){

    $.ajax({
        type: "GET",
        url: "/api/validate/nickname",
        data: {
            nickname : $('#nickname').val()
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                sessionStorage.setItem("nicknameKey", result.data);
                cookieUtil.setCookie("nicknameCertYn", "Y");
                guideNicknameValidate(result.resultCode,result.resultMsg);
            } else if(result.resultCode == "9999"){ //중복된 닉네임
                guideNicknameValidate(result.resultCode,result.resultMsg);
            } else if(result.resultCode == "9998"){ //형식에 맞지 않는 닉네임
                alert("닉네임 규격을 맞춰주세요.");
            }
        }, error: function(xhr){
            var errorMessage = xhr.responseJSON.resultMsg;
            console.log(errorMessage);
        }
    });

}

function guideNicknameValidate(code, msg){
    switch(code){
        case '0000':
            if($("#guide-nickname").length){
                $("#guide-nickname").html('<div class="valid-feedback ml-1" style="display:block; width: 100%;margin-top: .25rem;font-size: 80%;color: #1cc88a;"><i class="bi bi-check-circle"></i> '+ msg +'</div>');
            } else {
                var validContent = $('<div id="guide-nickname"> <div class="valid-feedback ml-1" style="display:block; width: 100%;margin-top: .25rem;font-size: 80%;color: #1cc88a;"><i class="bi bi-check-circle"></i> '+ msg +'</div></div>');
                validContent.insertAfter($('#nicknameInputDiv'));
            }
            break;
        case '9999':
            if($("#guide-nickname").length){
                $("#guide-nickname").html('<div class="valid-feedback ml-1" style="display:block; width: 100%;margin-top: .25rem;font-size: 80%;color: #f00;"><i class="bi bi-exclamation-circle"></i> '+ msg +'</div>');
            } else {
                var validContent = $('<div id="guide-nickname"> <div class="valid-feedback ml-1" style="display:block; width: 100%;margin-top: .25rem;font-size: 80%;color: #f00;"><i class="bi bi-exclamation-circle"></i> '+ msg +'</div></div>');
                validContent.insertAfter($('#nicknameInputDiv'));
            }
            break;
    }
}


jQuery(function() {
    const userForm = $('.user_info');

    $.validator.addMethod("regex", function(value, element, regex){
        var regExp = new RegExp(regex);
        return regExp.test(value);
    });

    $.validator.addMethod("over14", function(value, element) {
        var birthDate = new Date(value.substring(0, 4), parseInt(value.substring(4, 6)) - 1, value.substring(6, 8));
        var currentDate = new Date();
        var minDate = new Date(birthDate.getFullYear() + 14, birthDate.getMonth(), birthDate.getDate());
        return minDate <= currentDate;
    });

    $.validator.addMethod("noSpaceOrSpecialChars", function(value, element) {
        // 닉네임에 띄어쓰기나 특수문자가 있는지 확인합니다.
        return /^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]+$/i.test(value);
    });

    $.validator.addMethod("nicknameLength", function(value, element) {
        // 닉네임의 길이가 2글자 이상 10글자 이하인지 확인합니다.
        return value.length >= 2 && value.length <= 10;
    });

    $.validator.addMethod("checkPwdLength", function(value, element) {
        // 비밀번호가 8글자~16글자인지 확인합니다.
        return value.length >= 8 && value.length <= 16;
    });

    $.validator.addMethod("checkEmailContains", function(value, element) {
        // 비밀번호가 이메일 주소를 포함하는지 확인합니다.
        var inputEmail = $('#authEmail').val();
        var emailPrefix = inputEmail.split('@')[0];
        return value.indexOf(emailPrefix) === -1;
    });

    $.validator.addMethod("checkPwdRule", function(value, element) {
        // 비밀번호가 영문 대/소문자, 숫자, 기호 중 2개 이상 조합되어 있는지 확인합니다.
        var hasUpperCase = /[A-Z]/.test(value);
        var hasLowerCase = /[a-z]/.test(value);
        var hasDigit = /\d/.test(value);
        var hasSpecialChar = /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(value);
        var totalChars = hasUpperCase + hasLowerCase + hasDigit + hasSpecialChar;
        return totalChars >= 2;
    });

    $.validator.addMethod("checkSpaceContains", function(value, element) {
        // 비밀번호에 공백이 들어갔는지 확인합니다.
        return (value.indexOf(' ') === -1);
    });

    userForm.validate({
        debug:true,

        rules: {
            birthDate: {
                required: true,
                digits: true,
                minlength: 8,
                maxlength: 8,
                regex: /^(?:19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])$/,
                over14: true
            },
            nickname: {
                required: true,
                noSpaceOrSpecialChars : true,
                nicknameLength:true
            },
            password: {
                required: true,
                checkPwdLength: true,
                checkEmailContains: true,
                checkPwdRule: true,
                checkSpaceContains: true
            },
            termOfUse: {
                required: true
            },
            termOfPrivacy: {
                required: true
            }
        },
        messages: {
            birthDate: {
                required: '필수 입력 항목입니다.',
                digits : '숫자만 입력해주세요.',
                minlength : '8자리로 입력해주세요',
                maxlength : '8자리로 입력해주세요',
                regex : '올바른 형식으로 입력하세요.',
                over14 : '14세 이상만 가입이 가능합니다.'
            },
            nickname: {
                required: '필수 입력 항목입니다.',
                nicknameLength : '닉네임은 2글자 이상, 10글자 이하로 입력해야 합니다.',
                noSpaceOrSpecialChars: '닉네임에 띄어쓰기 혹은 특수문자를 사용하실 수 없습니다.'
            },
            password: {
                required: '필수 입력 항목입니다.',
                checkPwdLength: '8~16글자 이내로 입력해주세요.',
                checkEmailContains: '이메일 주소가 포함되면 안됩니다.',
                checkPwdRule: '영문, 숫자, 기호 중 2가지 이상 조합해주세요.',
                checkSpaceContains: '공백은 들어갈 수 없습니다.'
            },
            termOfUse: {
                required: '이용약관에 동의해주세요'
            },
            termOfPrivacy: {
                required: '개인정보 이용약관에 동의해주세요'
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "birthDate") {
                error.addClass('mt-2 mx-2');
                error.insertAfter($('#birthInputDiv'));
            } else if (element.attr("name") == "nickname") {
                error.addClass('mt-2 mx-2');
                error.insertAfter($('#nicknameInputDiv'));
            } else if (element.attr("name") == "password") {
                error.addClass('mt-2 mx-2');
                error.insertAfter($('#passwordInputDiv'));
            }
        }
    });

    $("form input").on('keyup change', function() {
        if (userForm.valid()) {
            var continueBtnHtml=
                `<a onclick="signup();" id="continueBtn" class="btn btn-primary btn-user btn-block">
                    계속하기
                </a>`;
            $("#continueBtnDiv").html(continueBtnHtml);
        } else {
            var continueBtnHtml=
                `<a href="#" id="disabledBtn" class="btn btn-secondary btn-user btn-block disabled">
                    계속하기
                </a>`;
            $("#continueBtnDiv").html(continueBtnHtml);
        }
    });

    $('#nickname').on('keyup change', function() {
        if (cookieUtil.getCookie("nicknameCertYn") == "Y") { // 닉네임 중복 여부 체크 확인
            $("#guide-nickname").remove();
            cookieUtil.setCookie("nicknameCertYn", "N")
        }
    });

    $('#checkNicknameBtn').click(function(){
        if($('#nickname').valid()){
            checkNickName();
        }
    });
});