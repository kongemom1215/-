function profileTabViewSetting(){
    // tab 클릭에 따른 content 노출
    $('input[name="listGroupCheckableRadios"]').change(function() {
        var selectedValue = $(this).val();

        // 모든 content 영역 숨기기
        $('.content').hide();

        // 선택된 value에 해당하는 content 영역 보이기
        $('#' + selectedValue + 'Content').show();
    });

    // 페이지 로드 시 URL에서 탭 식별자 읽어와서 해당 탭을 활성화
    var hash = window.location.hash.substr(1); // # 제거
    if (hash) {
        var $targetItem = $('input[value="' + hash + '"]');
        if ($targetItem.length > 0) {
            $targetItem.prop('checked', true).change();
        }
    }
}

function communityTabViewSetting(){
    var $listGroupItems = $('#communityContent .list-group-item');
    var $subContents = $('#communityContent .sub-content');

    var communityTabHistroy = cookieUtil.getCookie("profileCommunityTabHistory");
    if(communityTabHistroy != null){
        if(communityTabHistroy == "writtenPosts") {
            $listGroupItems.eq(0).addClass('active');
            $subContents.eq(0).show();
        } else if(communityTabHistroy == "commentedPosts"){
            $listGroupItems.eq(1).addClass('active');
            $subContents.eq(1).show();
        } else if(communityTabHistroy == "likedPosts"){
            $listGroupItems.eq(2).addClass('active');
            $subContents.eq(2).show();
        }
        cookieUtil.deleteCookie("profileCommunityTabHistory");
    } else { // 초기에 첫 번째 항목에 active 클래스 추가 및 해당 영역 보이기
        $listGroupItems.eq(0).addClass('active');
        $subContents.eq(0).show();
    }

    $listGroupItems.click(function() {
        $listGroupItems.removeClass('active');
        $(this).addClass('active');
        $subContents.hide();
        var targetId = $(this).data('target');
        $('#' + targetId).show();
    });
}

/* 커뮤니티활동 > 작성한 글 */
var myWrite = {
    getWriteList: function(){
        $.ajax({
            url: '/api/profile/post-list',
            type: 'POST',
            data : {
                category : 'write',
                userId : $("#userId").val()
            },
            success: (result) => {
                if(result.resultCode == "0000"){
                    this.addPostList(result.data)
                    if(result.data.hasNext){
                        this.addPostNextBtn(result.data);
                    }
                } else {
                    this.addPostError(result.resultMsg)
                }
            },
            error: (e) => {
                this.addPostError("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
            }
        });
    },
    nextWriteList: function(page){
        this.loadPostBtn();

        $.ajax({
            url: '/api/profile/post-list',
            type: 'POST',
            data: {
                category : 'write',
                page : page,
                userId : $("#userId").val()
            },
            success: (result) => {
                this.removePostNextBtn();
                if(result.resultCode == "0000"){
                    this.appendPostList(result.data)
                    if(result.data.hasNext){
                        this.addPostNextBtn(result.data);
                    }
                } else {
                    alert(result.resultMsg);
                }
            }, error: (e) => {
                alert("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
                this.removePostNextBtn();
            }
        });
    },
    addPostNextBtn: function(data){
        var btnHtml = `<div id="postNextBtnDiv" class="row mt-3 my-3">
                            <div class="d-grid">
                                <button type="button" class="btn btn-beige" onclick="myWrite.nextWriteList(${data.pageNumber + 1});">
                                    다음
                                </a>
                            </div>
                        </div>`;
        $('#writtenPosts').append(btnHtml);
    },
    loadPostBtn: function(){
        var btnHtml = `<div class="d-grid">
                            <button type="button" class="btn btn-beige">
                                <div class="spinner-grow spinner-grow-sm" role="status">
                                  <span class="visually-hidden">Loading...</span>
                                </div>
                            </a>
                        </div>`;
        $('#postNextBtnDiv').html(btnHtml);
    },
    removePostNextBtn: function(){
        $('#postNextBtnDiv').remove();
    },
    addPostList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length < 1) {
            $('#writtenPosts').html(`<div class="card">
                                            <div class="card-body">
                                                등록한 글이 없습니다.
                                            </div>
                                        </div>
                                    </div>`);
        } else {
            var boardListHtml = `<div id="postListGroup" class="list-group">`;
            boardList.forEach(function(board){
                var regDate = board.reg_dt.split('T')[0];
                var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <p class="mb-1">
                                             ${board.title}
                                         </p>
                                         <small><span class="badge rounded-pill text-bg-secondary">${board.reply_cnt}</span></small>
                                     </div>
                                     <span class="write-info text-muted" style="font-size:0.8rem;">${board.nickname}   ${regDate}   조회 ${board.read_cnt}</span>
                                 </a>`;
                boardListHtml += boardHtml;
            });
            boardListHtml += `</div>`;
            $('#writtenPosts').html(boardListHtml);
        }
    },
    appendPostList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length > 0) {
            var boardListHtml = '';
            boardList.forEach(function(board){
                var regDate = board.reg_dt.split('T')[0];
                var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <p class="mb-1">
                                             ${board.title}
                                         </p>
                                         <small><span class="badge rounded-pill text-bg-secondary">${board.reply_cnt}</span></small>
                                     </div>
                                     <span class="write-info text-muted" style="font-size:0.8rem;">${board.nickname}   ${regDate}   조회 ${board.read_cnt}</span>
                                 </a>`;
                boardListHtml += boardHtml;
            });
            $('#postListGroup').append(boardListHtml);
        }
    },
    addPostError: function(msg){
        $('#writtenPosts').html(`<div class="card">
                                        <div class="card-body">
                                            ${msg}
                                        </div>
                                    </div>
                                </div>`);
    }
}

/* 커뮤니티활동 > 댓글단 글 */
var myReply = {
    getReplyList: function(){
        $.ajax({
            url: '/api/profile/post-list',
            type: 'POST',
            data : {
                category : 'reply',
                userId : $("#userId").val()
            },
            success: (result) => {
                if(result.resultCode == "0000"){
                    this.addReplyList(result.data)
                    if(result.data.hasNext){
                        this.addReplyNextBtn(result.data);
                    }
                } else {
                    this.addReplyError(result.resultMsg)
                }
            },
            error: (e) => {
                this.addReplyError("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
            }
        });
    },
    nextReplyList: function(page){
        this.loadReplyBtn();

        $.ajax({
            url: '/api/profile/post-list',
            type: 'POST',
            data: {
                category : 'reply',
                page : page,
                userId : $("#userId").val()
            },
            success: (result) => {
                this.removeReplyNextBtn();
                if(result.resultCode == "0000"){
                    this.appendReplyList(result.data)
                    if(result.data.hasNext){
                        this.addReplyNextBtn(result.data);
                    }
                } else {
                    alert(result.resultMsg);
                }
            }, error: (e) => {
                alert("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
                this.removeReplyNextBtn();
            }
        });
    },
    addReplyNextBtn: function(data){
        var btnHtml = `<div id="replyNextBtnDiv" class="row mt-3 my-3">
                            <div class="d-grid">
                                <button type="button" class="btn btn-beige" onclick="myReply.nextReplyList(${data.pageNumber + 1});">
                                    다음
                                </a>
                            </div>
                        </div>`;
        $('#commentedPosts').append(btnHtml);
    },
    loadReplyBtn: function(){
        var btnHtml = `<div class="d-grid">
                            <button type="button" class="btn btn-beige">
                                <div class="spinner-grow spinner-grow-sm" role="status">
                                  <span class="visually-hidden">Loading...</span>
                                </div>
                            </a>
                        </div>`;
        $('#replyNextBtnDiv').html(btnHtml);
    },
    removeReplyNextBtn: function(){
        $('#replyNextBtnDiv').remove();
    },
    addReplyList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length < 1) {
            $('#commentedPosts').html(`<div class="card">
                                            <div class="card-body">
                                                등록한 댓글이 없습니다.
                                            </div>
                                        </div>
                                    </div>`);
        } else {
            var boardListHtml = `<div id="replyListGroup" class="list-group">`;
            boardList.forEach(function(board){
                if(board != null){
                    var regDate = moments.changeToDate(board.reg_dt);
                    var boardName;
                    if(board.board_type == 'free'){
                        boardName = '자유게시판';
                    } else if(board.board_type == 'share'){
                       boardName = '감자 공유소';
                    } else if(board.board_type == 'review'){
                        boardName = '감자 맛집';
                    } else if(board.board_type == 'recipe'){
                        boardName = '감자 레시피';
                    }

                    var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                        <div class="d-flex w-100 justify-content-between">
                                            <p class="mb-1">${board.content}</p>
                                            <small class="fs-7 text-muted">${regDate}</small>
                                        </div>
                                        <div class="fs-7"><span class="badge text-bg-gray">${boardName}</span> ${board.title}</div>
                                    </a>`;

                    boardListHtml += boardHtml;
                }
            });

            boardListHtml += `</div>`;
            $('#commentedPosts').html(boardListHtml);
        }
    },
    appendReplyList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length > 0) {
            var boardListHtml = '';
            boardList.forEach(function(board){
                if(board != null) {
                    var regDate = moments.changeToDate(board.reg_dt);
                    var boardName;
                    if(board.board_type == 'free'){
                       boardName = '자유게시판';
                    } else if(board.board_type == 'share'){
                      boardName = '감자 공유소';
                    } else if(board.board_type == 'review'){
                       boardName = '감자 맛집';
                    } else if(board.board_type == 'recipe'){
                       boardName = '감자 레시피';
                    }

                    var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                         <div class="d-flex w-100 justify-content-between">
                                              <p class="mb-1">${board.content}</p>
                                              <small class="fs-7 text-muted">${regDate}</small>
                                          </div>
                                         <div class="fs-7"><span class="badge text-bg-gray">${boardName}</span> ${board.title}</div>
                                     </a>`;

                    boardListHtml += boardHtml;
                }
            });
            $('#replyListGroup').append(boardListHtml);
        }
    },
    addReplyError: function(msg){
        $('#commentedPosts').html(`<div class="card">
                                        <div class="card-body">
                                            ${msg}
                                        </div>
                                    </div>
                                </div>`);
    }
}

/* 커뮤니티활동 > 좋아요한 글 */
var myLike = {
    getLikeList: function(){
       $.ajax({
           url: '/api/profile/post-list',
           type: 'POST',
           data: {
                category : 'like',
                userId : $("#userId").val()
           },
           success: (result) => {
               if(result.resultCode == "0000"){
                   this.addLikeList(result.data);
                   if(result.data != null && result.data.hasNext){
                       this.addLikeNextBtn(result.data);
                   }
               } else {
                   this.addLikeError(result.resultMsg);
               }
           },
           error: (e) => { // 화살표 함수 사용
               this.addLikeError("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
           }
       });
    },
    nextLikeList: function(page){
        this.loadLikeBtn();

        $.ajax({
            url: '/api/profile/post-list',
            type: 'POST',
            data: {
                category : 'like',
                page : page,
                userId : $("#userId").val()
            },
            success: (result) => {
                this.removeLikeNextBtn();
                if(result.resultCode == "0000"){
                    this.appendLikeList(result.data)
                    if(result.data.hasNext){
                        this.addLikeNextBtn(result.data);
                    }
                } else {
                    alert(result.resultMsg);
                }
            }, error: (e) => {
                alert("오류가 발생하였습니다! 페이지를 다시 접속해주세요.");
                this.removeLikeNextBtn();
            }
        });
    },
    addLikeNextBtn: function(data){
        var btnHtml = `<div id="likeNextBtnDiv" class="row mt-3 my-3">
                            <div class="d-grid">
                                <button type="button" class="btn btn-beige" onclick="myLike.nextLikeList(${data.pageNumber + 1});">
                                    다음
                                </a>
                            </div>
                        </div>`;
        $('#likedPosts').append(btnHtml);
    },
    loadLikeBtn: function(){
        var btnHtml = `<div class="d-grid">
                            <button type="button" class="btn btn-beige">
                                <div class="spinner-grow spinner-grow-sm" role="status">
                                  <span class="visually-hidden">Loading...</span>
                                </div>
                            </a>
                        </div>`;
        $('#likeNextBtnDiv').html(btnHtml);
    },
    removeLikeNextBtn: function(){
        $('#likeNextBtnDiv').remove();
    },
    addLikeList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length < 1) {
            $('#likedPosts').html(`<div class="card">
                                            <div class="card-body">
                                                좋아요한 글이 없습니다.
                                            </div>
                                        </div>
                                    </div>`);
        } else {
            var boardListHtml = `<div id="likeListGroup" class="list-group">`;
            boardList.forEach(function(board){
                var regDate = board.reg_dt.split('T')[0];
                var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <p class="mb-1">
                                             ${board.title}
                                         </p>
                                         <small><span class="badge rounded-pill text-bg-secondary">${board.reply_cnt}</span></small>
                                     </div>
                                     <span class="write-info text-muted" style="font-size:0.8rem;">${board.nickname}   ${regDate}   조회 ${board.read_cnt}</span>
                                 </a>`;
                boardListHtml += boardHtml;
            });
            boardListHtml += `</div>`;
            $('#likedPosts').html(boardListHtml);
        }
    },
    appendLikeList: function(data){
        var boardList = data.boardDTOList;
        if(boardList.length > 0) {
            var boardListHtml = '';
            boardList.forEach(function(board){
                var regDate = board.reg_dt.split('T')[0];
                var boardHtml = `<a href="${board.url}" onclick="setCommunityListCookie();" class="list-group-item list-group-item-action" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <p class="mb-1">
                                             ${board.title}
                                         </p>
                                         <small><span class="badge rounded-pill text-bg-secondary">${board.reply_cnt}</span></small>
                                     </div>
                                     <span class="write-info text-muted" style="font-size:0.8rem;">${board.nickname}   ${regDate}   조회 ${board.read_cnt}</span>
                                 </a>`;
                boardListHtml += boardHtml;
            });
            $('#likeListGroup').append(boardListHtml);
        }
    },
    addLikeError: function(msg){
        $('#likedPosts').html(`<div class="card">
                                        <div class="card-body">
                                            ${msg}
                                        </div>
                                    </div>
                                </div>`);
    }
}

function setCommunityListCookie(){
    var backUrl = "/profile/" + $("#userId").val() + "#community";
    cookieUtil.setCookieWithExpire("listBtnUrl", encodeURIComponent(backUrl), 1);

    var communityActiveTab = $('#communityContent .list-group-item.active').data('target');
    cookieUtil.setCookieWithExpire("profileCommunityTabHistory", communityActiveTab, 1);
}

$(document).ready(function(){
    profileTabViewSetting();
    communityTabViewSetting();

    myWrite.getWriteList();
    myReply.getReplyList();
    myLike.getLikeList();
});