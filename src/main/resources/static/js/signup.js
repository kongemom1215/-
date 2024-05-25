$(document).ready(function(){
    requireFourLengthCode();

    $('#continueBtn2').click(function(){
        verificationCode();
    });
});

function requireFourLengthCode(){
    const codeInput = document.getElementById('emailCode');

    codeInput.addEventListener('input', function() {
        // 입력된 값이 4자리 숫자인지 확인
        if (this.value.length === 4 && /^\d+$/.test(this.value)) {
            $('#disabledBtn2').css('display','none');
            $('#continueBtn2').css('display','block');
        } else {
            $('#disabledBtn2').css('display','block');
            $('#continueBtn2').css('display','none');
        }
    });
}


function verificationCode(){
    $.ajax({
        type: "POST",
        url: "/api/signup/email-auth",
        contentType: "application/json",
        data: JSON.stringify({
            email : $('#inputEmail').val(),
            code : $('#emailCode').val()
        }),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                sessionStorage.setItem("emailKey", result.data.uuid);
                location.href='/community/login/signupForm?uuid='+result.data.uuid+'&email='+$('#inputEmail').val();
            } else if(result.resultCode == "9999"){ //실패
                alert(result.resultMsg);
            } else if(result.resultCode == "9998"){ // 10회 이상 시도
                alert(result.resultMsg);
                location.href="/community/login";
            } else if(result.resultCode == "9997") { //이미 가입된 메일
                alert(result.resultMsg);
                location.href="/community/login";
            } else if(result.resultCode == "9996") {
                alert(result.resultMsg);
                location.href="/community/login";
            }
        },
        error: function(xhr, status, error) {
            // 요청이 실패한 경우
            var errorMessage = xhr.responseJSON.message;
            alert("이메일 전송 실패! 잠시후 다시 시도해보세요");
        }
    });
}

function sendEmailCode(){
    var email = $('#inputEmail').val();
    updateContinueBtn("load");

    $.ajax({
        type: "POST",
        url: "/api/signup/send-email-auth",
        data: {
            inputEmail: email
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                console.log(result.resultMsg);
                cookieUtil.setCookie("inputEmail", email);
                updateContinueBtn("avail");
                showCodeInput(email);
            } else { //10회 이상 시도
                alert(result.resultMsg);
                updateContinueBtn("avail");
            }
        },
        error: function(xhr, status, error) {
            // 요청이 실패한 경우
            var errorMessage = xhr.responseJSON.message;
            alert("이메일 전송 실패! 잠시후 다시 시도해보세요");
            updateContinueBtn("avail");
        }
    });

}


function updateContinueBtn(type){
    switch(type){
        case 'avail':
            var continueBtnHtml= `<a onclick="sendEmailCode();" id="continueBtn" class="btn btn-primary btn-user btn-block">
                                    계속하기
                                  </a>`;
            $("#continueBtnDiv_1").html(continueBtnHtml);
            break;
        case 'load':
            var continueBtnHtml= `<a href="#" id="LoadingContinueBtn" class="btn btn-primary btn-user btn-block">
                                     <div class="spinner-border spinner-border-sm" role="status">
                                         <span class="visually-hidden">Loading...</span>
                                     </div>
                                  </a>`
            $('#continueBtnDiv_1').html(continueBtnHtml);
            break;
        case 'non-avail':
            var continueBtnHtml= `<a href="#" id="disabledBtn" class="btn btn-secondary btn-user btn-block disabled">
                                      계속하기
                                  </a>`
            $('#continueBtnDiv_1').html(continueBtnHtml);
            break;
    }
}

function showCodeInput(email){
    $('.user_email').css('display','none');
    $('.user_email_validate').css('display','block');

    $('#email').val(email);
}

function backEmailForm(){
    $('.user_email').css('display','block');
    $('.user_email_validate').css('display','none');
    $('.send-feedback').css('display','none');

    $('#email').val('');
    $('#emailCode').val('');
}

function resendCode(){
    $('.send-feedback').css('display','block');
    $('.send-feedback').html(`전송중... <div class="spinner-border spinner-border-sm" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>`);

    var email = $('#email').val();

    $.ajax({
        type: "POST",
        url: "/api/signup/send-email-auth",
        data: {
            inputEmail: email
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                $('.send-feedback').html(`코드를 재전송하였습니다.`);
                console.log(result.resultMsg);
                cookieUtil.setCookie("inputEmail", email);
                showCodeInput(email);
            } else {
                $('.send-feedback').css('display','none');
                alert(result.resultMsg);
            }
        },
        error: function(xhr, status, error) {
            var errorMessage = xhr.responseJSON.message;
            alert("이메일 전송 실패! 잠시후 다시 시도해보세요");
        }
    });

}


jQuery(function() {
    const emailForm = $('.user_email');

    $.validator.addMethod("regex", function(value, element, regex){
        var regExp = new RegExp(regex);
        return regExp.test(value);
    });

    emailForm.validate({
        debug:true,

        rules: {
            inputEmail: {
                required: true,
                regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
            }
        },
        messages: {
            inputEmail: {
                required: '필수 입력 항목입니다.',
                regex : '올바른 이메일 형식으로 입력하세요.'
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "inputEmail") {
                error.addClass('mt-2');
                error.addClass('mx-2');
                error.insertAfter(element);
            } else {
                error.insertAfter(element);
            }
        }
    });

    $('#inputEmail').on('keyup change', function() {
        if (emailForm.valid()) { // 유효성 검사 통과 여부 확인
            updateContinueBtn('avail');
        } else {
            updateContinueBtn('non-avail')
        }
    });
});
