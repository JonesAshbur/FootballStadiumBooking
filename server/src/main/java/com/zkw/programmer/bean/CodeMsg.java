package com.zkw.programmer.bean;



/**
 * 错误码统一处理类，所有的错误码统一定义在这里
 */
public class CodeMsg {

    private Integer code;//错误码

    private String msg;//错误信息

    /**
     * 构造函数私有化即单例模式
     * 该类负责创建自己的对象，同时确保只有单个对象被创建。这个类提供了一种访问其唯一的对象的方式，可以直接访问，不需要实例化该类的对象。
     * @param code
     * @param msg
     */
    private CodeMsg(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg() {

    }

    public Integer getCode() {
        return code;
    }



    public void setCode(Integer code) {
        this.code = code;
    }



    public String getMsg() {
        return msg;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    //通用错误码定义
    //处理成功消息码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    //通用数据错误码
    public static CodeMsg DATA_ERROR = new CodeMsg(-1, "非法数据！");
    public static CodeMsg VALIDATE_ENTITY_ERROR = new CodeMsg(-2, "");
    public static CodeMsg CAPTCHA_EMPTY = new CodeMsg(-3, "验证码不能为空!");
    public static CodeMsg NO_PERMISSION = new CodeMsg(-4, "您没有当前操作的权限哦！");
    public static CodeMsg CAPTCHA_ERROR = new CodeMsg(-5, "验证码错误！");
    public static CodeMsg USER_SESSION_EXPIRED = new CodeMsg(-6, "还未登录或会话失效，请重新登录！");
    public static CodeMsg UPLOAD_PHOTO_SUFFIX_ERROR = new CodeMsg(-7, "图片格式不正确！");
    public static CodeMsg PHOTO_SURPASS_MAX_SIZE = new CodeMsg(-8, "上传的图片不能超过2MB！");
    public static CodeMsg PHOTO_FORMAT_NOT_CORRECT = new CodeMsg(-9, "上传的图片格式不正确！");
    public static CodeMsg SAVE_FILE_EXCEPTION = new CodeMsg(-10, "保存文件异常！");
    public static CodeMsg FILE_EXPORT_ERROR = new CodeMsg(-11, "文件导出失败！");
    public static CodeMsg SYSTEM_ERROR = new CodeMsg(-12, "系统出现了错误，请联系管理员！");
    public static CodeMsg NO_AUTHORITY = new CodeMsg(-13, "不好意思，您没有权限操作哦！");
    public static CodeMsg CAPTCHA_EXPIRED = new CodeMsg(-14, "验证码已过期，请刷新验证码！");
    public static CodeMsg COMMON_ERROR = new CodeMsg(-15, "");
    public static CodeMsg PHOTO_EMPTY = new CodeMsg(-16, "上传的图片不能为空！");
    public static CodeMsg MUSIC_EMPTY = new CodeMsg(-17, "上传的音乐不能为空！");
    public static CodeMsg MUSIC_SURPASS_MAX_SIZE = new CodeMsg(-18, "上传的音乐不能超过30MB！");
    public static CodeMsg MUSIC_FORMAT_NOT_CORRECT = new CodeMsg(-19, "上传的音乐格式不正确！");


    //用户管理类错误码
    public static CodeMsg USER_ADD_ERROR = new CodeMsg(-1000, "用户信息添加失败，请联系管理员！");
    public static CodeMsg USER_NOT_EXIST  = new CodeMsg(-1001, "该用户不存在！");
    public static CodeMsg USER_EDIT_ERROR = new CodeMsg(-1002, "用户信息编辑失败，请联系管理员！");
    public static CodeMsg USER_DELETE_ERROR = new CodeMsg(-1003, "用户信息删除失败，请联系管理员！");
    public static CodeMsg USERNAME_EXIST = new CodeMsg(-1004, "用户昵称重复，请换一个！");
    public static CodeMsg USERNAME_EMPTY = new CodeMsg(-1005, "用户昵称不能为空！");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(-1006, "用户密码不能为空！");
    public static CodeMsg USERNAME_PASSWORD_ERROR = new CodeMsg(-1007, "用户昵称或密码错误！");
    public static CodeMsg REPASSWORD_EMPTY = new CodeMsg(-1008, "确认密码不能为空！");
    public static CodeMsg REPASSWORD_ERROR = new CodeMsg(-1009, "确认密码不一致！");
    public static CodeMsg USER_REGISTER_ERROR = new CodeMsg(-1010, "注册用户失败，请联系管理员！");
    public static CodeMsg USER_PHONE_EXIST = new CodeMsg(-1011, "用户手机号重复，请换一个！");

    //体育场馆管理类错误码
    public static CodeMsg HALL_ADD_ERROR = new CodeMsg(-2000, "体育场馆信息添加失败，请联系管理员！");
    public static CodeMsg HALL_EDIT_ERROR = new CodeMsg(-2001, "体育场馆编辑失败，请联系管理员！");
    public static CodeMsg HALL_DELETE_ERROR = new CodeMsg(-2002, "体育场馆删除失败，请联系管理员！");
    public static CodeMsg HALL_NOT_NORMAL = new CodeMsg(-2003, "体育场馆在维护中！");
    public static CodeMsg HALL_IS_APPOINTED = new CodeMsg(-2004, "这个时间段已经被预约，请换个时间段！");

    //体育器材管理类错误码
    public static CodeMsg EQUIPMENT_ADD_ERROR = new CodeMsg(-3000, "体育器材信息添加失败，请联系管理员！");
    public static CodeMsg EQUIPMENT_EDIT_ERROR = new CodeMsg(-3001, "体育器材编辑失败，请联系管理员！");
    public static CodeMsg EQUIPMENT_DELETE_ERROR = new CodeMsg(-3002, "体育器材删除失败，请联系管理员！");
    public static CodeMsg EQUIPMENT_STOCK_ERROR = new CodeMsg(-3003, "体育器材库存不足！");
    public static CodeMsg EQUIPMENT_ALREADY_OFF = new CodeMsg(-3004, "体育器材已经下架！");

    //租借管理类错误码
    public static CodeMsg RENTAL_ADD_ERROR = new CodeMsg(-4000, "租借信息添加失败，请联系管理员！");
    public static CodeMsg RENTAL_OPERATE_ERROR = new CodeMsg(-4001, "租借状态处理失败，请联系管理员！");
    public static CodeMsg RENTAL_EDIT_ERROR = new CodeMsg(-4002, "租借信息更新失败，请联系管理员！");
    public static CodeMsg RENTAL_DELETE_ERROR = new CodeMsg(-4003, "租借信息删除失败，请联系管理员！");

    //预约管理类错误码
    public static CodeMsg APPOINTMENT_ADD_ERROR = new CodeMsg(-5000, "预约信息添加失败，请联系管理员！");
    public static CodeMsg APPOINTMENT_OPERATE_ERROR = new CodeMsg(-5001, "预约状态处理失败，请联系管理员！");
    public static CodeMsg APPOINTMENT_EDIT_ERROR = new CodeMsg(-5002, "预约信息更新失败，请联系管理员！");
    public static CodeMsg APPOINTMENT_DELETE_ERROR = new CodeMsg(-5003, "预约信息删除失败，请联系管理员！");

    //公告管理类错误码
    public static CodeMsg ANNOUNCE_ADD_ERROR = new CodeMsg(-6000, "公告信息添加失败，请联系管理员！");
    public static CodeMsg ANNOUNCE_EDIT_ERROR = new CodeMsg(-6001, "公告信息更新失败，请联系管理员！");
    public static CodeMsg ANNOUNCE_DELETE_ERROR = new CodeMsg(-6002, "公告信息删除失败，请联系管理员！");
}
