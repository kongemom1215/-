$(document).ready(function(){
    if(alarmList.length > 0){
        setBoardMoment();
        readNotification();
    }
});

function setBoardMoment() {
    alarmList.forEach(function(alarm) {
        var regDt = alarm.regDt;
        var momentRegDt = moments.changeToDate(regDt);

        $('#time_' + alarm.id).html(momentRegDt);
    });
}

function readNotification(){
    const unReadList = [];
    alarmList.forEach(alarm => {
        if(!alarm.checked){
            const alarmId = alarm.id;
            unReadList.push(alarmId);
        }
    });

    if(unReadList.length > 0) {
        $.ajax({
            type: "POST",
            url: '/api/read-alarm',
            contentType: 'application/json',
            data: JSON.stringify({
                'alarmIdList' : unReadList
            }),
            success: function(result) {
            }, error : function (e) {
            }
        });
    }
}