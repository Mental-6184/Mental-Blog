package org.example.mentalblogh.common.result;

public enum ErrorCode {
    // 通用错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 用户认证
    USERNAME_EXISTS(1001, "用户名已存在"),
    EMAIL_EXISTS(1002, "邮箱已被注册"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "账户已被封禁"),
    INVALID_TOKEN(1005, "无效的Token"),
    TOKEN_EXPIRED(1006, "Token已过期"),

    // 文章
    ARTICLE_NOT_FOUND(2001, "文章不存在"),
    CATEGORY_NOT_FOUND(2002, "分类不存在"),
    TAG_NOT_FOUND(2003, "标签不存在"),
    SLUG_EXISTS(2004, "URL别名已存在"),

    // 评论
    COMMENT_NOT_FOUND(3001, "评论不存在"),
    COMMENT_DISABLED(3002, "评论功能已关闭"),

    // 文件
    FILE_TOO_LARGE(4001, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(4002, "文件类型不允许"),
    UPLOAD_FAILED(4003, "文件上传失败"),

    // 图片
    IMAGE_PERMISSION_DENIED(5001, "你没有使用图库的权限，请先向管理员申请");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
