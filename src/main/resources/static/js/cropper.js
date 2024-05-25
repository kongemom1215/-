$(function(){
    cropper = '';
    let $confirmBtn = $('#confirm-button');
    let $resetBtn = $('#reset-button');
    let $cutBtn = $('#cut-button');
    let $newProfileImage = $('#new-profile-image');
    let $currentProfileImage = $('#current-profile-image');
    let $resultImage = $('#cropped-new-profile-image');
    let $profileImage = $('#profileImage');

    let $cardCurrentImg = $('#current-card-image');
    let $cardUpdateImg = $('#update-card-image');


    $newProfileImage.hide();
    $cutBtn.hide();
    $resetBtn.hide();
    $confirmBtn.hide();

    $('#profile-image-file').change(function(e){
        if(e.target.files.length == 1){
            const reader = new FileReader();
            reader.onload = e => {
                if(e.target.result){
                    let img = document.createElement("img");
                    img.id = 'new-profile';
                    img.src = e.target.result;
                    img.width = 250;

                    $newProfileImage.html(img);
                    $newProfileImage.show();
                    $currentProfileImage.hide();

                   // 이미지 로드 완료 후 Cropper 초기화
                    $(img).on('load', function() {
                        // Cropper 초기화
                        cropper = new Cropper(img, {
                            aspectRatio: 1,
                        });

                        $cutBtn.show();
                        $confirmBtn.hide();
                    });
                }
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });

    $resetBtn.click(function(){
        $currentProfileImage.show();
        $cardCurrentImg.show();
        $newProfileImage.hide();
        $cardUpdateImg.hide();
        $resultImage.hide();
        $resetBtn.hide();
        $cutBtn.hide();
        $confirmBtn.hide();
        $profileImage.val('');
        $('#profile-image-file').val('');
    });

    $cutBtn.click(function(){
        let dataUrl = cropper.getCroppedCanvas().toDataURL();
        let newImage = document.createElement("img");
        newImage.id = "cropped-new-profile-image";
        newImage.src = dataUrl;
        newImage.width = 125;
        $resultImage.html(newImage);
        $resultImage.show();
        $confirmBtn.show();

        $confirmBtn.click(function(){
            $newProfileImage.html(newImage);
            $cutBtn.hide();
            $confirmBtn.hide();
            $resetBtn.show();
            $profileImage.val(dataUrl);

            $('#imgUpdateModal').modal('hide');
            $cardCurrentImg.hide();
            $cardUpdateImg.html(newImage);
            $cardUpdateImg.show();
        });
    });
});