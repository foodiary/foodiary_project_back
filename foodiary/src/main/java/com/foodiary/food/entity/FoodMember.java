package com.foodiary.food.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodMember {
    private int foodMemberId;
    private int foodId;
    private int memberId;
    private int like;
    private int hate;

}
