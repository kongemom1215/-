jQuery(function() {
    const emailForm = $('.user_password');

    $.validator.addMethod("regex", function(value, element, regex){
        var regExp = new RegExp(regex);
        return regExp.test(value);
    });

    emailForm.validate({
        debug:true,

        rules: {
            email: {
                required: true,
                regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
            }
        },
        messages: {
            email: {
                required: '필수 입력 항목입니다.',
                regex : '올바른 이메일 형식으로 입력하세요.'
            }
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "email") {
                error.addClass('mt-2');
                error.addClass('mx-2');
                error.insertAfter(element);
            } else {
                error.insertAfter(element);
            }
        }
    });

    $('#inputEmail').on('keyup change', function() {
        if (emailForm.valid()) {
            updateContinueBtn('avail');
        } else {
            updateContinueBtn('non-avail');
        }
    });
});

function issuePassword() {
    var inputEmail = $('#inputEmail').val();
    updateContinueBtn('load');

    $.ajax({
        type: "POST",
        url: "/api/login/find-password",
        data: {
            email: inputEmail
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                updateContinueBtn('avail');
                alert(result.resultMsg);
                cookieUtil.setCookie("inputEmail", inputEmail);
                location.href = "/community/login";
            } else { //10회 이상 시도
                alert(result.resultMsg);
                updateContinueBtn('avail');
            }
        },
        error: function(xhr, status, error) {
            // 요청이 실패한 경우
            alert("이메일 전송 실패! 잠시 후 다시 시도해보세요");
            updateContinueBtn('avail');
        }
    });
}

function updateContinueBtn(type){
    switch(type){
        case 'avail':
            var continueBtnHtml= `<a id="resetPwdBtn" href="#" onclick="issuePassword();" class="btn btn-primary btn-user btn-block">
                                      비밀번호 재설정하기
                                  </a>`;
            $("#updatePwdBtn").html(continueBtnHtml);
            break;
        case 'load':
            var continueBtnHtml= `<a id="resetPwdBtn" href="#" class="btn btn-primary btn-user btn-block">
                                     <div class="spinner-border spinner-border-sm" role="status">
                                         <span class="visually-hidden">Loading...</span>
                                     </div>
                                  </a>`
            $('#updatePwdBtn').html(continueBtnHtml);
            break;
        case 'non-avail':
            var continueBtnHtml= `<a id="resetPwdBtn" href="#" class="btn btn-primary btn-user btn-block disabled">
                                      비밀번호 재설정하기
                                  </a>`
            $('#updatePwdBtn').html(continueBtnHtml);
            break;
    }
}