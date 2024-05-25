var dayList = dateList.day_list;
var dayListSize = dayList.length;
var weekList = dateList.week_list;
var weekListSize = weekList.length;;
var monthList = dateList.month_list;
var monthListSize = monthList.length;
var selectDateInfo;
var selectTab = 'daily';
var selectTabIdx = 0;

function hotTabViewSetting(){
    var $listGroupItems = $('#hotBoardTab .list-group-item');
    var $subContents = $('#hotBoardDiv .sub-content');

    selectDateInfo = decodeURIComponent(cookieUtil.getCookie("hotBoardDateHistory"));
    if(selectDateInfo != null && selectDateInfo.indexOf('|') > -1){
        selectDateInfo = selectDateInfo.split('|');
        var tab = selectDateInfo[0];
        selectTab = tab;
        var date_idx = selectDateInfo[1];
        if(tab == "daily") {
            $listGroupItems.eq(0).addClass('active');
            $subContents.eq(0).show();
            daily.loadHot(date_idx);
            weekly.loadHot();
            monthly.loadHot();
        } else if(tab == "weekly"){
            $listGroupItems.eq(1).addClass('active');
            $subContents.eq(1).show();
            daily.loadHot();
            weekly.loadHot(date_idx);
            monthly.loadHot();
        } else if(tab == "monthly"){
            $listGroupItems.eq(2).addClass('active');
            $subContents.eq(2).show();
            monthly.settingDate(date_idx);
            daily.loadHot();
            weekly.loadHot();
            monthly.loadHot(date_idx);
        }
        cookieUtil.deleteCookie("hotBoardDateHistory");
    } else {
        $listGroupItems.eq(0).addClass('active');
        $subContents.eq(0).show();

        if(dateList != null){
            daily.loadHot(); // 일별 포스트 불러오기
            weekly.loadHot(); // 주별 포스트 불러오기
            monthly.loadHot(); // 월별 포스트 불러오기
        }
    }

    $listGroupItems.click(function() {
        $listGroupItems.removeClass('active');
        $(this).addClass('active');
        $subContents.hide();
        var targetId = $(this).data('target');
        $('#' + targetId).show();
        selectTab = targetId;
    });
}

var daily = {
    loadHot: function(idx){
        //날짜 선택
        if(dayListSize > 0){
            if(idx != undefined){
                this.settingDate(idx);
            } else {
                this.settingDate(0);
            }
        } else {
            $('#dailyDateDiv').addClass('d-none');
            $('#dailyContent').html(`<div class="card">
                  <div class="card-body">
                      <div class="d-flex justify-content-center">
                          <p class="mt-3">
                              인기글로 등록된 글이 없습니다.
                              <button onclick="goForumWrite();" class="btn btn-sm btn-outline-potato">🥔글쓰러가기🥔</button>
                          </p>
                      </div>
                  </div>
              </div>`);
        }
    },
    settingDate: function(idx){
        if(idx == 0){
            $('#day').text(this.formatDate(dayList[0]));    // 날짜 설정
            if(dayListSize > 1){                   // 최근 날짜 이전 버튼 활성화
                $('#dayPreviousBtn').removeClass('d-none');
                $('#dayPreviousBtn').attr('onclick', 'daily.settingDate(1);');
            }
            $('#dayNextBtn').addClass('d-none');            // 최근 날짜 이후 버튼 비활성화
             this.settingPostList(boardList[dayList[0]]);        // 포스트 view 세팅
        } else if(dayList[idx] != null) {
            idx = parseInt(idx);
            $('#day').text(this.formatDate(dayList[idx])); // 날짜 설정
            $('#dayNextBtn').removeClass('d-none');        // 선택 날짜 이후 버튼 활성화
            $('#dayNextBtn').attr('onclick', 'daily.settingDate('+ (idx-1) +');');
            if(idx+1 < dayListSize) {  // 선택 날짜 이전 버튼 활성화
                $('#dayPreviousBtn').removeClass('d-none');
                $('#dayPreviousBtn').attr('onclick', 'daily.settingDate('+ (idx+1) +');');
            } else {                            // 선택 날짜 이전 버튼 비활성화
                $('#dayPreviousBtn').addClass('d-none');
            }

            this.settingPostList(boardList[dayList[idx]]); // 포스트 view 세팅
        }
        selectTabIdx = idx;
    },
    settingPostList: function(list){
        if(list !== undefined && list.length > 0) {
            var postListHtml = `<div class="list-group">`;
            list.forEach(function(post){
                var postHtml = `<a href="${post.postLink}" onclick="setBackUrl();" class="list-group-item list-group-item-action p-3" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <div class="align-self-center text-left">
                                             <div class="d-flex align-items-center">
                                                 <div class="me-4">
                                                     <span class="badge text-bg-light-green">${post.ranking}</span>
                                                 </div>
                                                 <div>
                                                     <span>${post.postTitle}</span>
                                                 </div>
                                             </div>
                                         </div>
                                     </div>
                                 </a>`;
                postListHtml += postHtml;
            });
            postListHtml += `</div>`
            $('#dailyContent').html(postListHtml);
        } else {
            $('#dailyContent').html(`<div class="card align-items-center p-5">
                                          <p>글을 불러오지 못했습니다.</p>지속적으로 발생할 경우 고객센터로 문의해주세요.
                                      </div>`);
        }
    },
    formatDate: function(dateString){
        return dateString.replace(/(\d{4})(\d{2})(\d{2})/, "$1.$2.$3");
    }
};

var weekly = {
    loadHot: function(idx){
        //날짜 선택
        if(weekListSize > 0){
            if(idx != undefined){
                this.settingDate(idx);
            } else {
                this.settingDate(0);
            }
        } else {
            $('#weeklyDateDiv').addClass('d-none');
            $('#weeklyContent').html(`<div class="card">
                  <div class="card-body">
                      <div class="d-flex justify-content-center">
                          <p class="mt-3">
                              인기글로 등록된 글이 없습니다.
                              <button onclick="goForumWrite();" class="btn btn-sm btn-outline-potato">🥔글쓰러가기🥔</button>
                          </p>
                      </div>
                  </div>
              </div>`);
        }
    },
    settingDate: function(idx){
        if(idx == 0){
            $('#week').text(this.formatDate(weekList[0]));    // 날짜 설정
            if(weekListSize > 1){                   // 최근 날짜 이전 버튼 활성화
                $('#weekPreviousBtn').removeClass('d-none');
                $('#weekPreviousBtn').attr('onclick', 'weekly.settingDate(1);');
            }
            $('#weekNextBtn').addClass('d-none');            // 최근 날짜 이후 버튼 비활성화
             this.settingPostList(boardList[weekList[0]]);        // 포스트 view 세팅
        } else if(dayList[idx] != null) {
            idx = parseInt(idx);
            $('#week').text(this.formatDate(weekList[idx])); // 날짜 설정
            $('#weekNextBtn').removeClass('d-none');        // 선택 날짜 이후 버튼 활성화
            $('#weekNextBtn').attr('onclick', 'weekly.settingDate('+ (idx-1) +');');
            if(idx+1 < weekListSize) {  // 선택 날짜 이전 버튼 활성화
                $('#weekPreviousBtn').removeClass('d-none');
                $('#weekPreviousBtn').attr('onclick', 'weekly.settingDate('+ (idx+1) +');');
            } else {                            // 선택 날짜 이전 버튼 비활성화
                $('#weekPreviousBtn').addClass('d-none');
            }

            this.settingPostList(boardList[weekList[idx]]); // 포스트 view 세팅
        }
        selectTabIdx = idx;
    },
    settingPostList: function(list){
        if(list !== undefined && list.length > 0) {
            var postListHtml = `<div class="list-group">`;
            list.forEach(function(post){
                var postHtml = `<a href="${post.postLink}" onclick="setBackUrl();" class="list-group-item list-group-item-action p-3" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <div class="align-self-center text-left">
                                             <div class="d-flex align-items-center">
                                                 <div class="me-4">
                                                     <span class="badge text-bg-light-green">${post.ranking}</span>
                                                 </div>
                                                 <div>
                                                     <span>${post.postTitle}</span>
                                                 </div>
                                             </div>
                                         </div>
                                     </div>
                                 </a>`;
                postListHtml += postHtml;
            });
            postListHtml += `</div>`
            $('#weeklyContent').html(postListHtml);
        } else {
            $('#weeklyContent').html(`<div class="card align-items-center p-5">
                                          <p>글을 불러오지 못했습니다.</p>지속적으로 발생할 경우 고객센터로 문의해주세요.
                                      </div>`);
        }
    },
    formatDate: function(dateString){
        var year = dateString.slice(0, 4);
        var month = dateString.slice(4, 6);
        var week = dateString.slice(6);

        return year + "년 " + month + "월 " + week + "주차";
    }
};

var monthly = {
    loadHot: function(idx){
        //날짜 선택
        if(monthListSize > 0){
            if(idx != undefined){
                this.settingDate(idx);
            } else {
                this.settingDate(0);
            }
        } else {
            $('#monthlyDateDiv').addClass('d-none');
            $('#monthlyContent').html(`<div class="card">
                  <div class="card-body">
                      <div class="d-flex justify-content-center">
                          <p class="mt-3">
                              인기글로 등록된 글이 없습니다.
                              <button onclick="goForumWrite();" class="btn btn-sm btn-outline-potato">🥔글쓰러가기🥔</button>
                          </p>
                      </div>
                  </div>
              </div>`);
        }
    },
    settingDate: function(idx){
        if(idx == 0){
            $('#month').text(this.formatDate(monthList[0]));    // 날짜 설정
            if(monthListSize > 1){                   // 최근 날짜 이전 버튼 활성화
                $('#monthPreviousBtn').removeClass('d-none');
                $('#monthPreviousBtn').attr('onclick', 'monthly.settingDate(1);');
            }
            $('#monthNextBtn').addClass('d-none');            // 최근 날짜 이후 버튼 비활성화
             this.settingPostList(boardList[monthList[0]]);        // 포스트 view 세팅
        } else if(dayList[idx] != null) {
            idx = parseInt(idx);
            $('#month').text(this.formatDate(monthList[idx])); // 날짜 설정
            $('#monthNextBtn').removeClass('d-none');        // 선택 날짜 이후 버튼 활성화
            $('#monthNextBtn').attr('onclick', 'monthly.settingDate('+ (idx-1) +');');
            if(idx+1 < monthListSize) {  // 선택 날짜 이전 버튼 활성화
                $('#monthPreviousBtn').removeClass('d-none');
                $('#monthPreviousBtn').attr('onclick', 'monthly.settingDate('+ (idx+1) +');');
            } else {                            // 선택 날짜 이전 버튼 비활성화
                $('#monthPreviousBtn').addClass('d-none');
            }

            this.settingPostList(boardList[monthList[idx]]); // 포스트 view 세팅
        }
        selectTabIdx = idx;
    },
    settingPostList: function(list){
        if(list !== undefined && list.length > 0) {
            var postListHtml = `<div class="list-group">`;
            list.forEach(function(post){
                var postHtml = `<a href="${post.postLink}" onclick="setBackUrl();" class="list-group-item list-group-item-action p-3" aria-current="true">
                                     <div class="d-flex w-100 justify-content-between">
                                         <div class="align-self-center text-left">
                                             <div class="d-flex align-items-center">
                                                 <div class="me-4">
                                                     <span class="badge text-bg-light-green">${post.ranking}</span>
                                                 </div>
                                                 <div>
                                                     <span>${post.postTitle}</span>
                                                 </div>
                                             </div>
                                         </div>
                                     </div>
                                 </a>`;
                postListHtml += postHtml;
            });
            postListHtml += `</div>`
            $('#monthlyContent').html(postListHtml);
        } else {
            $('#monthlyContent').html(`<div class="card align-items-center p-5">
                                          <p>글을 불러오지 못했습니다.</p>지속적으로 발생할 경우 고객센터로 문의해주세요.
                                      </div>`);
        }
    },
    formatDate: function(dateString){
        var year = dateString.slice(0, 4);
        var month = dateString.slice(4, 6);

        return year + "년 " + month + "월 ";
    }
};

function setBackUrl(){
    selectDateInfo = selectTab + "|" + selectTabIdx;
    cookieUtil.setCookieWithExpire("listBtnUrl", encodeURIComponent(location.href), 1);
    cookieUtil.setCookieWithExpire("hotBoardDateHistory", encodeURIComponent(selectDateInfo), 1);
}

$(document).ready(function(){
    if(error != undefined){
        $('#hotBoardDiv').html(`<div class="card align-items-center p-5">
                                  <p>글을 불러오지 못했습니다.</p>지속적으로 발생할 경우 고객센터로 문의해주세요.
                              </div>`);
    } else {
        hotTabViewSetting();
    }
});