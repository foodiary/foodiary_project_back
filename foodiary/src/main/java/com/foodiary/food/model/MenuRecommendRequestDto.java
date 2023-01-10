package com.foodiary.food.model;

import lombok.*;
import org.joda.time.DateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuRecommendRequestDto {

    @Setter private int menuId;

    private int memberId;

    private String menuMonLunchCategory;

    private String menuMonLunch;

    private String menuMonDinnerCategory;

    private String menuMonDinner;


    private String menuTueLunchCategory;

    private String menuTueLunch;

    private String menuTueDinnerCategory;

    private String menuTueDinner;


    private String menuWedLunchCategory;

    private String menuWedLunch;

    private String menuWedDinnerCategory;

    private String menuWedDinner;


    private String menuThuLunchCategory;

    private String menuThuLunch;

    private String menuThuDinnerCategory;

    private String menuThuDinner;


    private String menuFriLunchCategory;

    private String menuFriLunch;

    private String menuFriDinnerCategory;

    private String menuFriDinner;


    private String menuSatLunchCategory;

    private String menuSatLunch;

    private String menuSatDinnerCategory;

    private String menuSatDinner;


    private String menuSunLunchCategory;

    private String menuSunLunch;

    private String menuSunDinnerCategory;

    private String menuSunDinner;
}
