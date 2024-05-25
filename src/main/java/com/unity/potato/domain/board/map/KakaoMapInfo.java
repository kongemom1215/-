package com.unity.potato.domain.board.map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class KakaoMapInfo {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "place_name", nullable = false, length = 255)
    private String placeName;

    @Column(name = "road_address_name", nullable = false, length = 255)
    private String roadAddressName;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "latitude", nullable = false, length = 20)
    private String latitude;

    @Column(name = "longitude", nullable = false, length = 20)
    private String longitude;
}
