$(document).ready(function(){
    insertKakaoMap();
    settingWorldCupCardNon();

    $('.open-login-modal').click(function(){
        $('#loginModal').modal('show');
    });
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
                                <a href="#" data-bs-toggle="modal" data-bs-target="#loginModal">
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


function settingWorldCupCardNon(){
    $('#chartDiv').html(`<div class="h6 m-0 mt-1 font-weight-bold text-deep-potato">감자 월드컵</div>
                        <div class="chart-pie pt-3 pb-2">
                            <div class="chartjs-size-monitor"><div class="chartjs-size-monitor-expand"><div class=""></div></div><div class="chartjs-size-monitor-shrink"><div class=""></div></div></div>
                             <canvas id="myPieChart" width="355" height="245" style="display: block; width: 355px; height: 245px;" class="chartjs-render-monitor"></canvas>
                         </div>
                         <div class="worldcupDIv">
                             <div class="fs-8 mb-2">
                                 월드컵 하러가기
                             </div>
                             <button type="button" class="worldcupBtn" data-bs-toggle="modal" data-bs-target="#loginModal">
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