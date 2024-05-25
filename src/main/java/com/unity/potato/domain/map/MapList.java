package com.unity.potato.domain.map;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_map_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MapList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="map_url")
    private String mapUrl;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "view_yn", columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char viewYn;
}
