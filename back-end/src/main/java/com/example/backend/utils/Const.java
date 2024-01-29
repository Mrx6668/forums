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
    int MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    String UPLOAD_AVATAR_NAME = "/userAvatar/";
    String UPLOAD_CACHE_NAME = "/cache/";

    String FORUM_IMAGE_COUNTER = "forum:image:";
    String FORUM_POST_CREATE_LIMIT = "forum:post:limit:";
    String AI_TITLE_GENERATE_LIMIT = "forum:aiTitlePromote:";
    String FORUM_POST_PREVIEW_CACHE = "forum:post:preview:";
    String FORUM_POST_VIEW_COUNT = "forum:post:count";
    String FORUM_POST_COMMENT_COUNTER = "forum:post:comment";

}
