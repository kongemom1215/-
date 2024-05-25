function like(postId){
    if (isLiked) { // 이미 좋아요 상태일 때, 좋아요 취소
        $('#likeBtn').html('<i class="bi bi-heart"></i> '+ (--likeCnt)); // 아이콘과 숫자 변경
        unlikeAction(postId);
    } else { // 좋아요 상태가 아닐 때, 좋아요
        $('#likeBtn').html('<i class="bi bi-heart-fill text-danger"></i> '+ (++likeCnt)); // 아이콘과 숫자 변경
        likeAction(postId);
    }
}

function likeAction(postId){
    isLiked = true;

    $.ajax({
        type: "POST",
        url: "/api/post-like",
        contentType: 'application/json',
        data: JSON.stringify({
            postId : postId,
            pageCode : 'free'
        }),
        success: function(result){
            if(!result.resultCode == "0000"){ //성공
                console.log("error : " + result.resultMsg);
            }
        }, error: function(e){
            console.log(e);
        }
    });
}

function unlikeAction(postId){
    isLiked = false;

    $.ajax({
        type: "POST",
        url: "/api/post-unlike",
        contentType: 'application/json',
        data: JSON.stringify({
            postId : postId,
            pageCode : 'free'
        }),
        success: function(result){
            if(!result.resultCode == "0000"){ //성공
                console.log("error : " + result.resultMsg);
            }
        }, error: function(e){
            console.log(e);
        }
    });
}


function deletePost(postId){
    if(confirm("글을 삭제하시겠습니까?")){
        $.ajax({
            type: "POST",
            url: "/api/post-delete",
            contentType: 'application/json',
            data: JSON.stringify({
                id : postId,
                pageCode : 'free'
            }),
            success: function(result) {
                if(result.resultCode == "0000"){ //성공
                    alert("글이 삭제되었습니다.");
                    location.href="/community/free";
                } else {
                    alert(result.resultMsg);
                }
            }, error: function(xhr){
                console.log("error");
            }
        });
    }
}

function setDeleteReplyId(id){
    deleteReplyId = id;
}

function deleteReply(){
     $.ajax({
        type: 'POST',
        url: '/api/post/reply-delete',
        contentType: 'application/json',
        data: JSON.stringify({
            pageCode : 'free',
            id : deleteReplyId
        }),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                getReply();
                $('#replyDeleteModal').modal('hide');
            } else {
                alert(result.resultMsg);
                $('#replyDeleteModal').modal('hide');
            }
        }, error: function(xhr){
            $('#replyDeleteModal').modal('hide');
        }
    });
}


function setBoardMoment() {
    var momentRegDt = moments.changeDateFormat(regDt);
    if(updateDt != null) {
        momentRegDt += " (수정됨)";
    }

    $("#writeMoment").html(momentRegDt);
}

function getReply(callback){
    addLoadingDiv();
    addReplyInputDiv();

    $.ajax({
        type: 'POST',
        url: '/api/post/reply',
        contentType: 'application/json',
        data : JSON.stringify({
           postId : post_id,
           pageCode : 'free'
        }),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                if(!result.data){
                    deleteLoadingDiv();
                } else {
                    addReplyDiv(result.data);
                    if (typeof callback === "function") {
                        callback(); // 콜백 함수 호출
                    }
                }
            } else {
                addErrorReplyDiv();
            }
        }, error: function(xhr){
            addErrorReplyDiv();
        }
    });
}

function addReplyInputDiv() {
    var html = `<form name="replyForm">
                    <div class="col-12">
                        <textarea name="content" class="form-control mb-2" rows="3" maxlength="500" placeholder="주제와 무관한 댓글, 타인의 권리를 침해하거나 명예를 훼손하는 게시물은 별도의 통보 없이 제재를 받을 수 있습니다."></textarea>
                    </div>
                    <div class="col-12">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="charCount text-muted">0 / 500</span>
                            <button id="replyUploadBtn" onclick="reply();" class="btn btn-potato">댓글 등록</button>
                        </div>
                    </div>
                </form>`;
    $('#replyInputDiv').html(html);
    removeFormEvent('replyUploadBtn');
}

function addLoadingDiv() {
    var html = `<div class="card align-items-center p-5">
                    <div class="spinner-border" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>`;
    $('#replyContentDiv').html(html);
}

function addErrorReplyDiv(){
    var errorHtml = `<div class="card align-items-center p-5">
                         <p>댓글을 불러오지 못했습니다.</p>지속적으로 발생할 경우 고객센터로 문의해주세요.
                         <button class="btn btn-light mt-2" onclick="getReply();"><i class="bi bi-arrow-clockwise"></i></button>
                     </div>`;

    $('#replyContentDiv').html(errorHtml);
}

function deleteLoadingDiv(){
    $('#replyContentDiv').html('');
}

function addReplyDiv(replies){
    var replyCnt = 0;
    var replyListHtml = `<div id="replyListDiv" class="card">
                        <ul class="list-group list-group-flush">`;

    replies.forEach(function(reply){
        replyCnt++;
        var replyHtml =
        `<li id="reply${reply.id}" class="list-group-item">
            <div class="d-flex justify-content-between">
                <div>
                    <a href="/profile/${reply.user_id}" class="fs-7 fw-bold text-dark">
                        ${reply.profile_img != null ? `<img src="${reply.profile_img}" alt="댓글 작성자 이미지" height="25" class="rounded-circle me-1">` : `<svg data-jdenticon-value="${reply.user_name}" width="25" height="25" class="rounded-circle me-1"></svg>`}
                        ${reply.user_name}
                    </a>
                    <span class="board-divider"></span>
                     <span class="fs-7 text-light-secondary">${moments.changeToDate(reply.reg_dt)}</span>
               </div>`;
            if(reply.is_owner){
                replyHtml+= `<div class="dropdown no-arrow">
                                 <button class="btn p-0 dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                     <i class="bi bi-three-dots-vertical"></i>
                                 </button>
                                 <ul class="dropdown-menu text-center" style="min-width:5.5rem; font-size:0.9rem;">
                                     <li><a class="dropdown-item" href="javascript:void(0);" onclick="addReplyEditInput(${reply.id}, '${reply.content}')">수정하기</a></li>
                                     <li><a class="dropdown-item" href="#" onclick="setDeleteReplyId(${reply.id})" data-bs-toggle="modal" data-bs-target="#replyDeleteModal">삭제하기</a></li>
                                 </ul>
                             </div>`;
            }
            replyHtml +=
            `</div>
            <div class="mt-1">${reply.content}</div>
            <div class="mt-1 text-gray-800">
                <a href="javascript:void(0);" onclick="addReplyInput(${reply.id})" class="reply-btn"><i class="bi bi-chat-right-dots"></i> 답글</a>
            </div>
        </li>`;

        replyListHtml += replyHtml;

        if(reply.children.length > 0){
            reply.children.forEach(function(child){
                replyCnt++;
                var childHtml =
                `<li id="reply${child.id}" class="list-group-item bg-light">
                     <div class="row">
                         <div class="col-md-1">
                             <i class="bi bi-arrow-return-right"></i>
                         </div>
                         <div class="col-md-11">
                            <div class="d-flex justify-content-between">
                                <div>
                                    <a href="/profile/${child.user_id}" class="fs-7 fw-bold text-dark">
                                        ${child.profile_img != null ? `<img src="${child.profile_img}" alt="댓글 작성자 이미지" height="25" class="rounded-circle me-1">` : `<svg data-jdenticon-value="${child.user_name}" width="25" height="25" class="rounded-circle me-1"></svg>`}
                                        ${child.user_name}
                                    </a>
                                    <span class="board-divider"></span>
                                    <span class="fs-7 text-light-secondary">${moments.changeToDate(child.reg_dt)}</span>
                                </div>`;
                                if(child.is_owner){
                                    childHtml += `<div class="dropdown no-arrow">
                                                       <button class="btn p-0 dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                                           <i class="bi bi-three-dots-vertical"></i>
                                                       </button>
                                                       <ul class="dropdown-menu text-center" style="min-width:5.5rem; font-size:0.9rem;">
                                                           <li><a class="dropdown-item" href="javascript:void(0);" onclick="addReplyEditInput(${child.id}, '${child.content}')">수정하기</a></li>
                                                           <li><a class="dropdown-item" href="#" onclick="setDeleteReplyId(${child.id})" data-bs-toggle="modal" data-bs-target="#replyDeleteModal">삭제하기</a></li>
                                                       </ul>
                                                   </div>`;
                                }
                            childHtml += `</div>
                            <div class="mt-1">`;
                            if(child.mentioned_user_name != null) {
                                childHtml += `<mark class="text-gray-800 bg-warning">${child.mentioned_user_name}</mark>`
                            }
                            childHtml += ` ${child.content}
                            </div>
                            <div class="mt-1 text-gray-800">
                                 <a href="javascript:void(0);" onclick="addChildReplyInput(${child.parent_id}, ${child.id})" class="reply-btn"><i class="bi bi-chat-right-dots"></i> 답글</a>
                             </div>
                         </div>
                     </div>
                 </li>`;

                replyListHtml += childHtml;
            });
        }
    });

    $(".replyCnt").html("댓글 " + replyCnt);
    $('#replyContentDiv').html(replyListHtml);
}

function addReplyEditInput(id, content){
    $('.replyForm').remove();
    var replyEditInputHtml = `<li class="list-group-item replyForm">
                                    <form name="replyEditForm">
                                        <textarea name="content" class="form-control mb-2 reply-input" rows="3" maxlength="500" placeholder="주제와 무관한 댓글, 타인의 권리를 침해하거나 명예를 훼손하는 게시물은 별도의 통보 없이 제재를 받을 수 있습니다.">${content}</textarea>

                                        <div class="d-flex justify-content-between mb-2">
                                            <span id="charCount" class="text-muted">0 / 500</span>
                                            <div>
                                                <button id="replyEditCancelBtn" onclick="showOriginReply(${id})" class="btn btn-sm btn-secondary">취소</button>
                                                <button id="replyEditBtn" onclick="updateReply(${id})" class="btn btn-sm btn-primary">수정</button>
                                            </div>
                                        </div>
                                    </form>
                                </li>`;

    $('#reply' + id).after(replyEditInputHtml);
    $('#reply' + id).css('display','none');
    removeFormEvent('replyEditCancelBtn');
    removeFormEvent('replyEditBtn');
}

function updateReply(id){
    var form = document.replyEditForm;
    var content = form.content.value;

    if(content.length < 1){
        alert('댓글 내용을 입력해 주세요.');
        throw new Error('content is required!');
    }

     var formData = {
        pageCode : 'free',
        id : id,
        content : content
    };

    postReplyEdit(formData);
}

function postReplyEdit(formData){
    $.ajax({
        type: "POST",
        url: "/api/post/reply-edit",
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                getReply();
            } else {
                alert(result.resultMsg);
                showOriginReply(formData.id);
            }
        }, error: function(xhr){
            console.log("error");
        }
    });
}

function showOriginReply(id){
    $('.replyForm').remove();
    $('#reply' + id).css('display','block');
}

function reply(){
    var form = document.replyForm;
    var content = form.content.value;

    if(content.length < 1){
        alert('댓글 내용을 입력해 주세요.');
        throw new Error('content is required!');
    }

    var formData = {
        pageCode : 'free',
        postId : post_id,
        content : content
    };

    postReply(formData);
}

function replyToReply(replyId) {
    var form = document.replyToReplyForm;
    var content = form.content.value;

    if(content.length < 1){
        alert('댓글 내용을 입력해 주세요.');
        throw new Error('content is required!');
    }

    var formData = {
        pageCode : 'free',
        postId : post_id,
        content : content,
        parentId : replyId
    };

    postReply(formData);
}

function replyToChildReply(replyId, targetId){
     var form = document.replyToChildForm;
    var content = form.content.value;

    if(content.length < 1){
        alert('댓글 내용을 입력해 주세요.');
        throw new Error('content is required!');
    }

    var formData = {
        pageCode : 'free',
        postId : post_id,
        content : content,
        parentId : replyId,
        targetId : targetId
    };

    postReply(formData);
}

function postReply(formData){
    $.ajax({
        type: "POST",
        url: "/api/post/reply-upload",
        contentType: 'application/json',
        data: JSON.stringify(formData),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                getReply();
            } else {
                alert(result.resultMsg);
            }
        }, error: function(xhr){
            console.log("error");
        }
    });
}

function addReplyInput(replyId){
    $('.replyForm').remove();
    var replyInputHtml = `<li class="list-group-item replyForm">
                    <form name="replyToReplyForm">
                        <div class="row">
                            <div class="col-md-1">
                                <i class="bi bi-arrow-return-right"></i>
                            </div>
                            <div class="col-md-11">
                                <!-- 입력 창 -->
                                <textarea name="content" class="form-control mb-2 reply-input" rows="3" maxlength="500" placeholder="주제와 무관한 댓글, 타인의 권리를 침해하거나 명예를 훼손하는 게시물은 별도의 통보 없이 제재를 받을 수 있습니다."></textarea>

                                <div class="d-flex justify-content-between mb-2">
                                    <!-- 글자 수 표시 -->
                                    <span id="charCount" class="text-muted">0 / 400</span>
                                    <!-- 댓글 등록 버튼 -->
                                    <button id="replyChildUploadBtn" onclick="replyToReply(${replyId});" class="btn btn-sm btn-potato">등록</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </li>`;
    $('#reply' + replyId).after(replyInputHtml);
    focusReplyInput('reply-input');
    removeFormEvent('replyChildUploadBtn');
}

function addChildReplyInput(replyId, targetId){
    $('.replyForm').remove();
    var replyChildInputHtml = `<li class="list-group-item replyForm bg-light">
                                    <form name="replyToChildForm">
                                        <div class="row">
                                            <div class="col-md-1">
                                                <i class="bi bi-arrow-return-right"></i>
                                            </div>
                                            <div class="col-md-11">
                                                <!-- 입력 창 -->
                                                <textarea name="content" class="form-control mb-2 reply-input" rows="3" maxlength="500" placeholder="주제와 무관한 댓글, 타인의 권리를 침해하거나 명예를 훼손하는 게시물은 별도의 통보 없이 제재를 받을 수 있습니다."></textarea>

                                                <div class="d-flex justify-content-between mb-2">
                                                    <!-- 글자 수 표시 -->
                                                    <span id="charCount" class="text-muted">0 / 400</span>
                                                    <!-- 댓글 등록 버튼 -->
                                                    <button id="replyChildUploadBtn" onclick="replyToChildReply(${replyId}, ${targetId});" class="btn btn-sm btn-potato">등록</button>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                </li>`;
    $('#reply' + targetId).after(replyChildInputHtml);
    focusReplyInput('reply-input');
    removeFormEvent('replyChildUploadBtn');
}

function focusReplyInput(className){
    var startElement = $('.' + className);
    $('html, body').animate({
        scrollTop: startElement.offset().top
    }, 1000);
}

function removeFormEvent(btnId){
    const button = document.getElementById(btnId);
    if (button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
        });
    }
}

function scrollHash() {
    var hash = window.location.hash;
    if (hash) {
        var element = document.querySelector(hash);
        if (element) {
            element.scrollIntoView();
        }
    }
}

$(document).ready(function(){
    if(board != null){
        setBoardMoment();
        getReply(function(){
            scrollHash();
        });
    }

    removeFormEvent('replyUploadBtn');
});

