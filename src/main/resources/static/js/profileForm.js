document.addEventListener('DOMContentLoaded', function() {
    // 글자수 세기
    const textarea = document.getElementById('bio');
    const charCount = document.getElementById('charCount');

    textarea.addEventListener('input', function() {
        const maxLength = parseInt(textarea.getAttribute('maxlength'));
        const currentLength = textarea.value.length;

        if (currentLength > maxLength) {
            textarea.value = textarea.value.slice(0, maxLength); // 최대 글자 수까지 자르기
        }

        charCount.textContent = `${currentLength} / ${maxLength} 글자`;
    });
});

function profileUpdate(){
    var profileImg = $('#profileImage').val();
    var bio = $('#bio').val();

    var formData = {
        profileImg: profileImg,
        bio: bio
    }

    $.ajax({
        type: 'POST',
        url: '/api/profile/update-setting',
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(result) {
            if(result.resultCode == "0000"){
                location.href="/profile/" + result.data;
            } else if(result.resultCode = "9999"){
                alert(result.resultMsg);
            }
        },error: function(xhr){
            console.log(xhr);
        }
    });
}

