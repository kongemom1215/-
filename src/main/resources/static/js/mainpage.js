$(document).ready(function(){
    insertKakaoMap();
    getWorldcupInfo();
});

function insertKakaoMap(){
    var mapContainer = document.getElementById('map'),
          mapOption = {
                center: new kakao.maps.LatLng(36.706832, 127.965814),
                level: 13
            };

    var map = new kakao.maps.Map(mapContainer, mapOption);
    getRestaurantList(map);
}

function getRestaurantList(map){
    $.ajax({
            url: '/api/map-list',
            type: 'GET',
            success: function(result){
                if(result.resultCode == "0000"){
                    insertMapMarker(result.data, map);
                    insertMarketingAlert();
                } else {
                    insertErrorAlert();
                }
            }
            , error: function(e){
                insertErrorAlert();
            }
        });
}

function insertErrorAlert(){
    $('#map').prepend(`<div class="alert alert-map-danger fade-out-alert" role="alert" style="
                           position: absolute;
                           z-index: 2;
                           width: 100%;
                           border-radius: 0;
                       ">지도를 불러오는 도중 오류가 발생하였습니다.
                       </div>`);
}

function insertMarketingAlert(){
    $('#map').prepend(`<div class="alert alert-map fade-out-alert" role="alert" style="
                           position: absolute;
                           z-index: 2;
                           width: 100%;
                           border-radius: 0;">
                           맛집 게시판에 맛집을 등록하면, 네비게이션에 추가될 수도 있어요!
                           <span onclick="goReviewForumWrite('all');" class="alert-link">추가하러가기</span>
                       </div>`);
}

function insertMapMarker(markerList, map){
    if(markerList != null && markerList != undefined){
        markerList.forEach(function(marker){
            var imgSrc = '/assets/img/map_marker.png',
                imgSize = new kakao.maps.Size(30, 30),
                imgOption = {offset: new kakao.maps.Point(0, 0)};
            var markerImage = new kakao.maps.MarkerImage(imgSrc, imgSize, imgOption),
                markerPosition = new kakao.maps.LatLng(marker.latitude, marker.longitude);
            var markerTip = new kakao.maps.Marker({
                  position: markerPosition,
                  image: markerImage
            });
            markerTip.setMap(map);

            var content = `<div class="customoverlay">
                                <a href="${marker.mapUrl}" target="_blank">
                                    <span class="title">${marker.name} 맛집</span>
                                </a>
                            </div>`;
            var position = new kakao.maps.LatLng(marker.latitude, marker.longitude);
            var customOverlay = new kakao.maps.CustomOverlay({
                map: map,
                position: position,
                content: content,
                yAnchor: 1
            });
        });
    }
}


function getWorldcupInfo(){
    $.ajax({
        type: 'GET',
        url: '/api/worldcup/top-rank',
        success: function(result) {
            if(result.resultCode == "0000"){
                if(result.data != null){
                    settingWorldCupCard(result.data);
                } else {
                    settingWorldCupCardNon();
                }
            } else {
                settingWorldCupCardError();
            }
        }, error: function (e) {
            settingWorldCupCardError();
        }
    });
}

function settingWorldCupCard(data) {
    var worldcup = data.worldcup;
    var topRank = data.topRank;

    if(topRank != null){
        var worldCupCardHtml = `<a href="/worldcup" class="h6 m-0 font-weight-bold text-deep-potato">${worldcup.title}<i class="bi bi-box-arrow-in-right"></i></a>
                                <div class="chart-pie pt-4 pb-2"><div class="chartjs-size-monitor"><div class="chartjs-size-monitor-expand"><div class=""></div></div><div class="chartjs-size-monitor-shrink"><div class=""></div></div></div>
                                    <canvas id="myPieChart" width="355" height="245" style="display: block; width: 355px; height: 245px;" class="chartjs-render-monitor"></canvas>
                                </div>
                                <div class="mt-4 text-center small">`;
        var labels = [];
        var dataValues = [];
        var index = 0;
        Object.keys(topRank).forEach(function(key) {
                labels.push(key);
                dataValues.push(topRank[key]);
                switch(index){
                    case 0 :
                        worldCupCardHtml += `<span class="mr-2">
                                                 <i class="bi bi-circle-fill text-deep-potato"></i> ${key}
                                             </span>`
                        break;
                    case 1 :
                        worldCupCardHtml += `<span class="mr-2">
                                                 <i class="bi bi-circle-fill text-sweet-potato"></i> ${key}
                                             </span>`
                        break;
                    case 2 :
                        worldCupCardHtml += `<span class="mr-2">
                                                 <i class="bi bi-circle-fill text-light-green"></i> ${key}
                                             </span>`
                        break;
                    case 3 :
                        worldCupCardHtml += `<span class="mr-2">
                                                 <i class="bi bi-circle-fill text-potato"></i> ${key}
                                             </span>`
                        break;
                }
                index++;
            });
            worldCupCardHtml+= `</div>`;
            $('#chartDiv').html(worldCupCardHtml);
            var ctx = document.getElementById("myPieChart");
            var myPieChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: dataValues,
                        backgroundColor: ['#D6962F', '#E08865', '#BDE065', '#DCBD4D'],
                        hoverBackgroundColor: ['#B27B23', '#BD856F', '#86AD22', "#BBA760"],
                        hoverBorderColor: "rgba(234, 236, 244, 1)",
                    }],
                },
                options: {
                    maintainAspectRatio: false,
                    tooltips: {
                    backgroundColor: "rgb(255,255,255)",
                    bodyFontColor: "#858796",
                    borderColor: '#dddfeb',
                    borderWidth: 1,
                    xPadding: 15,
                    yPadding: 15,
                    displayColors: false,
                    caretPadding: 10,
                    },
                    plugins:{
                        legend: {
                            display: false
                        },
                    },
                    cutoutPercentage: 80,
                },
            });
    } else {
        settingWorldCupCardNon();
    }
}

function settingWorldCupCardNon(){
    $('#chartDiv').html(`<div class="chart-pie pt-4 pb-2">
                            <div class="chartjs-size-monitor"><div class="chartjs-size-monitor-expand"><div class=""></div></div><div class="chartjs-size-monitor-shrink"><div class=""></div></div></div>
                             <canvas id="myPieChart" width="355" height="245" style="display: block; width: 355px; height: 245px;" class="chartjs-render-monitor"></canvas>
                         </div>
                         <div class="worldcupDIv">
                             <div class="fs-8 mb-2">
                                 월드컵 하러가기
                             </div>
                             <button onclick="goWorldCup();" class="worldcupBtn">
                                 <span>GO!</span>
                             </button>
                         </div>`);

    var ctx = document.getElementById("myPieChart");
    var myPieChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ["??", "??", "??", "??"],
            datasets: [{
                data: [55, 30, 10, 5],
                backgroundColor: ['#D6962F', '#E08865', '#BDE065', '#DCBD4D'],
                hoverBackgroundColor: ['#B27B23', '#BD856F', '#86AD22', "#BBA760"],
                hoverBorderColor: "rgba(234, 236, 244, 1)",
            }],
        },
        options: {
            maintainAspectRatio: false,
            tooltips: {
            backgroundColor: "rgb(255,255,255)",
            bodyFontColor: "#858796",
            borderColor: '#dddfeb',
            borderWidth: 1,
            xPadding: 15,
            yPadding: 15,
            displayColors: false,
            caretPadding: 10,
            },
            plugins:{
                legend: {
                    display: false
                },
            },
            cutoutPercentage: 80,
        },
    });
}

function settingWorldCupCardError(){
    $('#chartDiv').html(`<small class="text-secondary">
                             불러오는 도중 오류가 발생하였습니다.<br>
                             <button onclick="getWorldcupInfo();" class="worldcupBtn mt-1">
                                 <span>다시시도</span>
                             </button>
                         </small>`);
}

function askGpt(){
    const query = $('#gptQuery').val();

    if(query.length < 1){
        alert('내용을 입력해 주세요.');
        throw new Error('content is required!');
    }

    changeStateGptAskBtn('load');

    $.ajax({
        url: "/api/ask-openai",
        type : "POST",
        data: {
            'query' : query
        },
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                $('#gpt-answer').removeClass('d-none');
                $('#gpt-error').addClass('d-none');
                const jsonData = JSON.parse(result.data);
                insertGptAnswer(jsonData);
                changeStateGptAskBtn('avail');
            } else {
                $('#gpt-answer').addClass('d-none');
                $('#gpt-error').removeClass('d-none');
                $('#gpt-error').html(`<div class="alert alert-danger" role="alert">
                                        ${result.resultMsg}
                                      </div>`);
                changeStateGptAskBtn('avail');
            }
        }, error : function(e) {
            $('#gpt-answer').addClass('d-none');
            $('#gpt-error').removeClass('d-none');
            $('#gpt-error').html(`<div class="alert alert-danger" role="alert">
                                    처리 도중 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.
                                  </div>`);
            changeStateGptAskBtn('avail');
        }
    });
}

function changeStateGptAskBtn(type){
    switch(type){
        case 'load':
            var continueBtnHtml= `<div class="spinner-grow spinner-grow-sm" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                  </div>`;
            $('#gptAskBtn').off('click', askGpt);
            $("#gptAskBtn").html(continueBtnHtml);
            break;
        case 'avail':
            $('#gptAskBtn').on('click', askGpt);
            $('#gptAskBtn').html('질문하기');
            break;
    }
}

function insertGptAnswer(data){
    if(data != null && data.choices != null){
        const answer = data.choices[0].text;
        let index = 0;
        $('#typing-text').html('');

        const interval = setInterval(() => {
            document.getElementById('typing-text').textContent += answer[index];
            index++;
            if (index === answer.length)
                clearInterval(interval);
        }, 50);
    }
}

function registerMsg(){
    const msgTitle = $('#msgTitle').val();
    const colorInput = $('#colorInput').val();
    const msgContent = $('#msgTextarea').val();

    if(msgTitle.length < 1 || msgTitle.length > 15){
        alert("제목은 15자 이내로 입력해주세요.");
        throw new Error('msg title is not validate!');
    }
    if(!colorInput){
        alert("색상을 설정해주세요.");
        throw new Error('colorInput is not validate!');
    }
    if(msgContent.length < 1 || msgContent.length > 40){
        alert("내용은 40자 이내로 입력해주세요.");
        throw new Error('msgContent is not validate!');
    }

    $.ajax({
        type : 'POST',
        url : '/api/collect/msg',
        contentType: 'application/json',
        data: JSON.stringify({
            title : msgTitle,
            color : colorInput,
            content : msgContent
        }),
        success: function(result) {
            if(result.resultCode == "0000"){ //성공
                alert("등록이 완료되었습니다.");
                $('#inputMsgModal').modal('hide');
            } else {
                alert(result.resultMsg);
            }
        }, error: function(e){
            alert("처리 도중 오류가 발생하였습니다. 현상이 지속될 경우 문의바랍니다.");
        }
    });
}
