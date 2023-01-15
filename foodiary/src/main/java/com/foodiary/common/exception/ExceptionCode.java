package com.foodiary.common.exception;


public enum ExceptionCode {
    BAD_REQUEST(400, "요청 정보를 다시 입력해주세요."),
    OVER_REQUEST(400, "오늘 추천 횟수가 초과되었습니다."),

    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    MEMBER_NOT_EXISTS(404, "탈퇴한 회원입니다."),
    POST_NOT_FOUND(404, "존재하지 않는 게시글입니다."),
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글입니다."),
    LIKE_NOT_FOUND(404, "좋아요 한 적 없는 게시글입니다."),
    SCRAP_NOT_FOUND(404, "저장한 스크랩이 없습니다."),

    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),

    MEMBER_EXISTS(409, "이미 존재하는 회원입니다."),
    LIKE_EXISTS(409, "이미 좋아요 한 게시글입니다."),
    SCRAP_EXISTS(409, "이미 저장한 스크랩입니다."),

    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),

    IMAGE_BAD_REQUEST(400, "이미지를 한장 이상 올려주세요"),
    FILE_BAD_REQUEST(400, "해당 파일의 확장자를 올릴수 없습니다."),
    EMAIL_BAD_REQUEST(400, "이메일이 중복입니다"),
    NICKNAME_BAD_REQUEST(400, "닉네임이 중복입니다"),
    LOGINID_BAD_REQUEST(400, "아이디가 중복입니다"),
    SEARCH_NOT_FOUND(404, "검색어와 일치하는 게시글이 없습니다."),
    LAST_SEARCH_NOT_FOUND(404, "최근 검색어가 없습니다."),
    NOT_FOUND(404, "요청한 데이터가 없습니다."),
    NOT_AUTHORIZED(401, "접근 권한이 없는 사용자입니다."),
    NUM_BAD_REQUEST(400, "인증번호가 일치하지 않습니다."),
    NUM_TIMEOUT(400, "인증 시간을 초과하였습니다. 다시 시도해주세요"),
    MORE_PW_ERROR(400, "비밀번호가 일치하지않습니다."),
    TERMS_ERROR(400, "필수약관에 동의해주세요"),
    SELECT_ERROR(500, "데이터 가져오기에 실패했습니다. 다시 시도해주세요"),
    SAVE_ERROR(500, "저장 실패했습니다. 다시 시도해주세요"),
    UPDATE_ERROR(500, "업데이트 실패했습니다. 다시 시도해주세요"),
    DELETE_ERROR(500, "삭제 실패했습니다. 다시 시도해주세요"),
    MYPAGE_NOT_FOUND(404, "게시글이 없습니다."),
    FILE_NOT_FOUND(404, "파일이 없습니다.")
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
