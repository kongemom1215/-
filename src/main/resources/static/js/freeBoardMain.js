function toggleActive(element) {
    if (element.id === 'recent-order') {
        element.dataset.active = 'true';
        $('#popular-order').attr('data-active', 'false');
    } else if (element.id === 'popular-order') {
        element.dataset.active = 'true';
        $('#recent-order').attr('data-active', 'false');
    }
}

function setBoardMoment() {
    boardList.content.forEach(function(board) {
        var regDt = board.regDt;
        var momentRegDt = moments.changeToDate(regDt);

        $('#time_' + board.postId).html(momentRegDt);
    });
}



$(document).ready(function(){
    setBoardMoment();
    makeBackUrl();
});




