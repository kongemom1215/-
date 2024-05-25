let editor;

$(document).ready(function() {
    editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '600px',
        initialEditType: "wysiwyg",
        initialValue: post_content,
        viewer: false,
        usageStatistics: !0,
        hideModeSwitch: 'true',     //하단의 타입 선택 탭 숨기기
        toolbarItems: [
            ["image", "link", "heading", "bold", "italic", "strike"], ["hr", "quote"], ["ul", "ol"], ["scrollSync"],
        ],
        hooks: {
            async addImageBlobHook(blob, callback) {
                const allowImageSizeBytes = 10 * 1024 * 1024; // 10MB
                const allowedFileTypes = ["image/png", "image/jpeg", "image/gif"];

                const formData = new FormData();
                formData.append('image', blob);

                if(!allowedFileTypes.includes(blob.type)){
                    alert("이미지 파일만 업로드 가능합니다.");
                    throw new Error("image size over");
                }

                if(blob.size > allowImageSizeBytes){
                    alert("10MB 이하의 이미지를 요청해주세요.");
                    throw new Error("image size over");
                }

                $.ajax({
                    url:'/api/file/image-upload',
                    type:'post',
                    data: formData,
                    processData: false,  // 데이터를 query string으로 변환하지 않음
                    contentType: false,  // Content-Type 헤더를 설정하지 않음 (FormData가 자동으로 설정)
                    success: function(result){
                        if(result.resultCode == "0000"){
                            if(result.data != ""){
                                const imageUrl = `/api/file/image-print?filename=${result.data}`;
                                callback(imageUrl, 'image alt attribute');
                            } else {
                                alert("이미지가 존재하지 않습니다.");
                            }
                        } else {
                           alert(result.resultMsg);
                        }
                    }, error: function(xhr) {
                       alert("서버 에러가 발생했습니다.");
                    }
                });
            }
        }
    });

    editor.on('change', () => {
        const htmlLength = editor.getHTML().length;
        if(htmlLength > 2000){
            $('#alertMaxOver').html(`<div class="alert alert-danger" role="alert">
                                       제한된 글자 수를 넘었습니다.
                                     </div>`);
        } else {
            $('#alertMaxOver').html(``);
        }
    });
});


function updatePost() {
    var titleVar = $('#title').val();
    var contentVar = editor.getHTML();

    if(titleVar.length > 40 || titleVar.length < 1){
        alert("제목은 40자 이내로 입력해주세요.");
        throw new Error('editor title is not validate!');
    }
    if (editor.getMarkdown().length < 1) {
        alert('에디터 내용을 입력해 주세요.');
        throw new Error('editor content is required!');
    }

    if(contentVar.length > 2000) {
        alert('content가 2000 이상은 등록할 수 없습니다.');
        throw new Error('editor content is over');
    }

    const params = {
        id : $('#postId').val(),
        title: titleVar,
        content: contentVar,
        pageCode : 'free',
        imgYn : checkImages()
    };

    $.ajax({
        type: "POST",
        url: "/api/post-update",
        contentType: 'application/json',
        data: JSON.stringify(params),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                location.href="/community/free/"+result.data;
            } else {
                alert(result.resultMsg);
            }
        }, error: function(xhr){
            var errorMessage = xhr.responseJSON.resultMsg;
            console.log(errorMessage);
        }
    });
}

function checkImages() {
    var htmlString = editor.getHTML();

    const imgTagPattern = /<img[^>]+src="([^">]+)"/g;
    const matches = htmlString.match(imgTagPattern);

    // 이미지 태그가 발견되었는지 여부에 따라 imgYn 변수 설정
    const imgYn = matches && matches.length > 0 ? 'Y' : 'N';

    return imgYn;
}