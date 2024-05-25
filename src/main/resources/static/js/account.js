function togglePasswordVisibility(inputId, obj) {
    var inputField = document.getElementById(inputId);
    var toggleButton = document.getElementById('toggle_' + inputId);

    if (inputField.type === 'password') {
        inputField.type = 'text';
        $(obj).html('<i class="bi bi-eye-slash-fill"></i>');
    } else {
        inputField.type = 'password';
        $(obj).html('<i class="bi bi-eye-fill"></i>');
    }
}

function updatePassword(){
    var formData = {
        currentPwd: $("#currentPwd").val(),
        newPwd: $("#newPwd").val(),
        newPwdRepeat: $("#newPwdRepeat").val()
    };

    updatePasswordBtn('load');

    $.ajax({
        type: 'POST',
        url: '/api/profile/update-password',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(result) {
            updatePasswordBtn('avail');

            if(result.resultCode == "0000"){ //성공
                alert("비밀번호 변경을 완료하였습니다.");
                $('#pwdUpdateModal').modal('hide');
                location.href="/profile/" + $("#userId").val() + "#account";
            } else {
                $('#alertPwdStateDiv').css('display','block');
                $('#alertPwdState').text(result.resultMsg);
            }
        }, error: function(xhr){
            updatePasswordBtn('avail');
            console.log(xhr);
        }
    });
}

function updatePasswordBtn(type){
    switch(type){
        case 'avail':
            var continueBtnHtml= `<button onclick="updatePassword();" type="button" class="btn btn-primary">비밀번호 재설정하기</button>`;
            $('#pwdUpdateBtnDiv').html(continueBtnHtml);
            break;
        case 'load':
            var continueBtnHtml= `<button type="button" class="btn btn-primary">
                                     <div class="spinner-border spinner-border-sm" role="status">
                                         <span class="visually-hidden">Loading...</span>
                                     </div>
                                  </button>`
            $('#pwdUpdateBtnDiv').html(continueBtnHtml);
            break;
        case 'non-avail':
            var continueBtnHtml= `<button type="button" class="btn btn-primary disabled">비밀번호 재설정하기</button>`
            $('#pwdUpdateBtnDiv').html(continueBtnHtml);
            break;
    }
}


function updateNickname(){
    if(cookieUtil.getCookie("nicknameCertYn") != "Y"){
        alert("닉네임 중복 여부를 확인하세요.");
        return;
    }
    var formData = {
        nickname: $("#nicknameInput").val()
    };
    activeNicknameUpdateBtn('load');

    $.ajax({
        type: 'POST',
        url: '/api/profile/update-nickname',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(result) {
            activeNicknameUpdateBtn('valid');
            if(result.resultCode == "0000"){ //성공
                alert("닉네임 변경을 완료하였습니다.");
                location.href="/profile/" + $("#userId").val() + "#account";
            } else {
                $('#invalidNicknameDiv').css('display','block');
                $('#invalidState').text(result.resultMsg);
            }
        }, error: function(xhr){
            activeNicknameUpdateBtn('valid');
            console.log(xhr);
        }
    });
}

function checkNickname(){
    $.ajax({
        type: "GET",
        url: "/api/validate/nickname",
        data: {
            nickname : $('#nicknameInput').val()
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                cookieUtil.setCookie("nicknameCertYn", "Y");
                $('#validNicknameDiv').css('display', 'block');
                activeNicknameUpdateBtn('valid');
            } else {
                cookieUtil.setCookie("nicknameCertYn", "N");
                $('#invalidNicknameDiv').css('display', 'block');
                $('#invalidState').text(result.resultMsg);
                activeNicknameUpdateBtn('invalid');
            }
        }, error: function(xhr){
            var errorMessage = xhr.responseJSON.resultMsg;
            console.log(errorMessage);
        }
    });
}

function withdrawUser(){
    $.ajax({
        type: "POST",
        url: "/api/profile/withdraw",
        data : {
            isAgree : $("#withdraw_agree").prop('checked')
        },
        success: function(result) {
            if(result.resultCode == "0000"){
                alert(result.resultMsg);
                location.href='/community/main';
            } else {
                alert(result.resultMsg);
            }
        }, error: function(xhr){
            var errorMessage = xhr.responseJSON.resultMsg;
            console.log(errorMessage);
        }
    });
}

function activeNicknameUpdateBtn(check){
    switch(check){
        case 'valid':
            var continueBtnHtml=
                    `<button onclick="updateNickname();" type="button" class="btn btn-primary">닉네임 변경하기</button>`;
            $("#nicknameUpdateBtnDiv").html(continueBtnHtml);
            break;
        case 'invalid':
            var continueBtnHtml=
                                `<button type="button" class="btn btn-primary disabled">닉네임 변경하기</button>`;
            $("#nicknameUpdateBtnDiv").html(continueBtnHtml);
            break;
        case 'load':
            var continueBtnHtml=
                                `<button type="button" class="btn btn-primary">
                                    <div class="spinner-border spinner-border-sm" role="status">
                                         <span class="visually-hidden">Loading...</span>
                                     </div>
                                </button>`;
            $("#nicknameUpdateBtnDiv").html(continueBtnHtml);
            break;
    }
}


jQuery(function() {
    passwordValidate();
    nicknameValidate();
    withdrawValidate();
});


function passwordValidate(){
    $.validator.addMethod("regex", function(value, element, regex){
        var regExp = new RegExp(regex);
        return regExp.test(value);
    });

    $.validator.addMethod("checkPwdLength", function(value, element) {
        // 비밀번호가 8글자~16글자인지 확인합니다.
        return value.length >= 8 && value.length <= 16;
    });

    $.validator.addMethod("checkEmailContains", function(value, element) {
        // 비밀번호가 이메일 주소를 포함하는지 확인합니다.
        var inputEmail = $('#email').val();
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
        return (value.indexOf(' ') === -1); //공백 있는지 체크
    });

    const pwdForm = $('#password_form');

    pwdForm.validate({
        debug:true,

        rules: {
            currentPwd:{
                required: true
            },
            newPwd: {
                required: true,
                checkPwdLength: true,
                checkEmailContains: true,
                checkPwdRule: true,
                checkSpaceContains: true
            },
            newPwdRepeat: {
                required: true,
                equalTo: "#newPwd"
            }
        },
        messages: {
            newPwd: {
                required: '필수 입력 항목입니다.',
                checkPwdLength: '8~16글자 이내로 입력해주세요.',
                checkEmailContains: '이메일 주소가 포함되면 안됩니다.',
                checkPwdRule: '영문, 숫자, 기호 중 2가지 이상 조합해주세요.',
                checkSpaceContains: '공백은 들어갈 수 없습니다.'
            },
            newPwdRepeat:{
                required: '필수 입력 항목입니다.',
                equalTo: "비밀번호가 일치하지 않습니다."
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "newPwd") {
                error.addClass('mt-2');
                error.addClass('mx-2');
                error.insertAfter($('#newPwdInputDiv'));
            } else if (element.attr("name") == "newPwdRepeat") {
                error.addClass('mt-2');
                error.addClass('mx-2');
                error.insertAfter($('#newPwdRepeatInputDiv'));
            }
        }
    });

    $("#password_form input").on('keyup change', function() {
        if (pwdForm.valid()) {
            updatePasswordBtn('avail');
        } else {
            updatePasswordBtn('non-avail');
        }
    });
}

function nicknameValidate(){
    $.validator.addMethod("noSpaceOrSpecialChars", function(value, element) {
        // 닉네임에 띄어쓰기나 특수문자가 있는지 확인합니다.
        return /^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]+$/i.test(value);
    });

    $.validator.addMethod("nicknameLength", function(value, element) {
        // 닉네임의 길이가 2글자 이상 10글자 이하인지 확인합니다.
        return value.length >= 2 && value.length <= 10;
    });

    $.validator.addMethod("notEqualTo", function(value, element, param) {
        // 현재 닉네임과 비교합니다.
        return value !== $(param).val();
    });

    const nicknameForm = $('#nickname_form');

    nicknameForm.validate({
        debug:true,

        rules: {
            nicknameInput: {
                required: true,
                noSpaceOrSpecialChars : true,
                nicknameLength:true,
                notEqualTo: "#nickname"
            }
        },
        messages: {
            nicknameInput: {
                required: '필수 입력 항목입니다.',
                nicknameLength : '닉네임은 2글자 이상, 10글자 이하로 입력해야 합니다.',
                noSpaceOrSpecialChars: '닉네임에 띄어쓰기 혹은 특수문자를 사용하실 수 없습니다.',
                notEqualTo: '지금 사용중인 닉네임입니다.'
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "nicknameInput") {
                error.addClass('mt-2');
                error.addClass('mx-2');
                error.insertAfter($('#nicknameInputDiv'));
            }
        }
    });

    $("#nickname_form input").on('keyup change', function() {
        if (nicknameForm.valid()) {
            $('#checkNicknameBtn').css('display','block');
        } else {
            $('#checkNicknameBtn').css('display','none');
        }
    });

    $('#nicknameInput').on('keyup change', function() {
        $(".nicknameValidState").css('display', 'none');
        if (cookieUtil.getCookie("nicknameCertYn") == "Y") { // 닉네임 중복 여부 체크 확인
            cookieUtil.setCookie("nicknameCertYn", "N")
        }
        activeNicknameUpdateBtn('invalid');
    });
}

function withdrawValidate(){
    const withdrawForm = $('#withdraw_form');

    withdrawForm.validate({
        debug:true,

        rules: {
            withdraw_agree: {
                required: true
            }
        },
        messages: {
            withdraw_agree: {
                required: '',
            }
        }
    });

    $("#withdraw_agree").on('change', function() {
        if (withdrawForm.valid()) {
            $('#withdrawBtn').removeClass("disabled");
            $('#withdrawBtn').attr('onclick', 'withdrawUser();');
        } else {
            $('#withdrawBtn').addClass("disabled");
            $('#withdrawBtn').removeAttr('onclick');
        }
    });
}