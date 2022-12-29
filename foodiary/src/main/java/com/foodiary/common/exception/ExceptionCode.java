package com.foodiary.common.exception;

public enum ExceptionCode {

    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    POST_NOT_FOUND(404, "존재하지 않는 게시글입니다."),
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글입니다."),
    LIKE_NOT_FOUND(404, "좋아요 한 적 없는 게시글입니다."),
    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
    MEMBER_EXISTS(409, "이미 존재하는 회원입니다."),
    LIKE_EXISTS(409, "이미 좋아요 한 게시글입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR")
    ;


    private int status;
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }
}
