package com.foodiary.food.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodMemberDto {
    private int foodMemberId;
    private int foodId;
    private int memberId;
    private int like;
    private int hate;

}
