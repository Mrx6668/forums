package com.example.backend.utils;

public interface Const {
    String JWT_BLACK_LIST = "jwt:blacklist:";
    String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    String VERIFY_EMAIL_DATA = "verify:email:data:";
    int ORDER_CORS = -102;
    String FLOW_LIMIT_COUNTER = "flow:counter:";
    String FLOW_LIMIT_BLOCK = "flow:block:";
    String ATTR_USER_ID = "userId";
    String BUCKET = "forum";
    //    1.5 MB
    long MAX_AVATAR_SIZE = (long) (1.5 * 1024 * 1024);

    String UPLOAD_AVATAR_NAME = "/userAvatar/";

}
