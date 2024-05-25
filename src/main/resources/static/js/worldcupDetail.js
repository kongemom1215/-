var selectedCandidates = [];
var round = cupInfo.round;
var roundNum = 0;
var leftCandidate;
var rightCandidate;
var finalSelection;

function selectImage(side) {
    if (side === 'left') {
        selectedCandidates.push(leftCandidate);
    } else if(side == 'right') {
        selectedCandidates.push(rightCandidate);
    }
    //남은 후보가 있는지
    if(!isRemainCandidate()){
        //진행사항 세팅
        settingRoundProgress();
        //랜덤 후보 뽑기
        getRandomCandidate();
        //후보 세팅
        settingCandidates();
    } else {
        saveWinner();
    }
}

function saveWinner(){
    $.ajax({
        url:'/api/worldcup/save-winner',
        type:'post',
        contentType: 'application/json',
        data: JSON.stringify({
            cupId : cupInfo.id,
            winnerId : finalSelection.id
        }),
        success: function(result) {
        }, error: function(xhr){
            console.log(errorMessage);
        }
    });
}

function settingRoundProgress(){
    if(selectedCandidates.length == 0){
        round = candidates.length;
    }

    $('#worldcup-round').html(round);
    $('#round-progress').html(roundNum + '/' + round/2);
}

function settingCandidates(){
    document.getElementById('leftImage').src = leftCandidate.imgSrc;
    document.getElementById('leftName').textContent = leftCandidate.name;
    document.getElementById('rightImage').src = rightCandidate.imgSrc;
    document.getElementById('rightName').textContent = rightCandidate.name;
}

function isRemainCandidate(){
    if(candidates.length == 0 && selectedCandidates.length == 1){
        finalSelection = selectedCandidates.pop();
        $('#finalName').html(finalSelection.name);
        $('#finalImage').attr('src', finalSelection.imgSrc);
        $('#worldcup-content').addClass('d-none');
        addAnimation();
        return true;
    } else if(candidates.length == 0){
        candidates = selectedCandidates;
        selectedCandidates = [];

        roundNum = 1;
    } else {
        roundNum ++;
    }

    return false;
}

function getRandomCandidate() {
    var leftIndex = Math.floor(Math.random() * candidates.length);
    leftCandidate = candidates[leftIndex];
    candidates.splice(leftIndex, 1);

    var rightIndex = Math.floor(Math.random() * candidates.length);
    rightCandidate = candidates[rightIndex];
    candidates.splice(rightIndex, 1);
}

function addAnimation(){
    var finalImage = document.getElementById('finalImage');
    var worldcupFinal = document.getElementById('worldcup-final');

    worldcupFinal.classList.remove('d-none'); // d-none 클래스 제거하여 요소를 표시

    setTimeout(function() {
        finalImage.classList.add('enlarged'); // finalImage에 대한 애니메이션 효과 트리거
    }, 700); // 1초 후에 실행;
}

function startWorldCup(){
    $('#startModal').modal('hide');
}

window.onload = function() {
    $('#startModal').modal('show');
};

$(document).ready(function(){
    selectImage();
});