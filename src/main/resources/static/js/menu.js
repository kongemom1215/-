function goForum(){
    location.href="/community/free";
}

function goForumWrite(){
    location.href="/community/free/write";
}

function goShareForum(){
    location.href="/community/share";
}

function goShareForumWrite(){
    location.href="/community/share/write";
}

function goReviewForum(region){
    location.href="/community/review/" + region;
}

function goHotForum(){
    location.href="/community/hot";
}

function goReviewForumWrite(region){
    location.href= "/community/review/" + region + "/write";
}

function goRecipeForumWrite(){
    location.href="/community/recipe/write";
}
function goWorldCup(){
    location.href="/worldcup";
}


function goForumList() {
    var listBtnUrl = cookieUtil.getCookie("listBtnUrl");
    if(listBtnUrl != null){
        location.href= decodeURIComponent(listBtnUrl);
    } else{
        history.back();
    }
}

function goRankingTable(id){
    location.href= "/worldcup/rank/" + id;
}

function makeBackUrl() {
    cookieUtil.setCookieWithExpire("listBtnUrl", encodeURIComponent(location.href), 1);
}

function validateSearch(){
    var keyword = $("#searchInput").val();

    if(keyword.length < 1){
        alert("검색어를 입력해주세요.");
        return false;
    }

    return true;
}