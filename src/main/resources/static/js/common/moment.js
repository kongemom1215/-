var moments = {
    // 이메일 값을 쿠키에 저장하는 함수
    changeToDate: function(datetime) {
        var now = moment(new Date());
        var duration = moment.duration(now.diff(datetime));
        var seconds = duration.asSeconds();
        var minute = duration.asMinutes();
        var hours = duration.asHours();
        var days = duration.asDays();
        var weeks = duration.asWeeks();
        var month = duration.asMonths();
        var year = duration.asYears();

        if (minute < 1) {
            return parseInt(seconds) + '초 전'
        } else if (hours < 1) {
            return parseInt(minute) + '분 전'
        } else if (hours < 24) {
            return parseInt(hours) + '시간 전'
        } else if (weeks < 1) {
            return parseInt(days) + '일 전'
        } else if (month < 1) {
            return parseInt(weeks) + '주 전'
        } else if (year < 1) {
            return parseInt(month) + '달 전'
        } else {
            return parseInt(year) + '년 전'
        }
    },
    changeDateFormat: function(datetime){
        var date = moment(datetime);
        return date.format("YYYY.M.D HH:mm");
    }
};