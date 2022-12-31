package com.foodiary.common.s3;

import lombok.Builder;

@Builder
public class Image {

    private int dailyId;

    private int memberId;

    private String originalName;

    private String saveName;

    private String path;

    private String type;

    private long size;


    public Image(int dailyId, int memberId, String originalName, String saveName, String path, String type, long size){
        this.dailyId = dailyId;
        this.memberId = memberId;
        this.originalName = originalName;
        this.saveName = saveName;
        this.path = path;
        this.type = type;
        this.size = size;
    }
}
