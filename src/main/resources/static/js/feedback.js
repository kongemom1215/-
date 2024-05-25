jQuery(function() {
    const contactForm = $('#contactForm');

    $.validator.addMethod("regex", function(value, element, regex){
        var regExp = new RegExp(regex);
        return regExp.test(value);
    });

    contactForm.validate({
        debug:true,

        rules: {
            name:{
                required: true,
                maxlength: 20
            },
            'email-input': {
                required: true,
                regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                maxlength:255
            },
            message: {
                required: true,
                maxlength: 1000
            }
        },
        messages: {
            name:{
                required: "성함을 입력해주세요.",
                maxlength: "성함은 20자 이내로 입력해주세요."
            },
            'email-input' : {
                required: "이메일을 입력해주세요.",
                regex: "올바른 이메일 형식으로 입력하세요.",
                maxlength: "이메일은 255자 이내로 입력해주세요."
            },
            message: {
                required: "문의사항을 입력해주세요.",
                maxlength: "문의사항은 1000자 이내로 입력해주세요."
            }
        },
        errorPlacement: function(error, element){
            var errorId = element.attr("id") + "-error";
            $("#" + errorId).text(error.text());
        },
        success: function(label, element) {
            var errorId = $(element).attr("id") + "-error";
            $("#" + errorId).text("");
        }
    });

    $("#contactForm input, #contactForm textarea").on("input", function() {
        if(contactForm.valid()){
            $('#registerFeedbackBtnDiv').html(`<button type="button" onclick="setFeedbackSubmit();" class="btn btn-primary">제출</button>`);
        } else {
            $('#registerFeedbackBtnDiv').html(`<button type="button" class="btn btn-primary disabled">제출</button>`);
        }
    });
});


function setFeedbackSubmit(){
    var formData = {
        name: $("#name").val(),
        email: $("#email-input").val(),
        message: $("#message").val()
    };

    $.ajax({
        type: "POST",
        url: "/api/send-feedback",
        contentType: "application/json",
        data: JSON.stringify(formData),
        success: function(result) {
            if(result.resultCode == "0000"){
                $("#submitSuccessMessage").removeClass("d-none");
            } else {
                $("#submitErrorMessage").removeClass("d-none");
            }
            $('#registerFeedbackBtnDiv').remove();
        },
        error: function(xhr, status, error) {
            $("#submitErrorMessage").removeClass("d-none");
            $('#registerFeedbackBtnDiv').remove();
        }
    });
}