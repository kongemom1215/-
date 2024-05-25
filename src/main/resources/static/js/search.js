function searchFree(){
    var type = $('#searchSelect').val();
    var keyword = $('#searchText').val();

    if(keyword.length < 1){
        alert("검색어를 입력해주세요.");
        throw new Error('editor title is not validate!');
    }

    location.href = "/community/free/search?type=" + type + "&keyword=" + keyword;
}

function searchShare(){
    var type = $('#searchSelect').val();
    var keyword = $('#searchText').val();

    if(keyword.length < 1){
        alert("검색어를 입력해주세요.");
        throw new Error('editor title is not validate!');
    }

    location.href = "/community/share/search?type=" + type + "&keyword=" + keyword;
}

function searchReview(){
    var type = $('#searchSelect').val();
    var keyword = $('#searchText').val();

    if(keyword.length < 1){
        alert("검색어를 입력해주세요.");
        throw new Error('editor title is not validate!');
    }

    location.href = "/community/review/search?type=" + type + "&keyword=" + keyword;
}

function searchRecipe(){
    var type = $('#searchSelect').val();
    var keyword = $('#searchText').val();

    if(keyword.length < 1){
        alert("검색어를 입력해주세요.");
        throw new Error('editor title is not validate!');
    }

    location.href = "/community/recipe/search?type=" + type + "&keyword=" + keyword;
}


