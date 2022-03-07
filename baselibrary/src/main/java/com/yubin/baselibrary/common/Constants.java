package com.yubin.baselibrary.common;

/**
 * Created by Administrator on 2015/9/28.
 */
public final class Constants {

    public static final int TYPE_INVALID = Integer.MIN_VALUE;

    public static final int TYPE_TIMES_ONE = -1;
    public static final int TYPE_TIMES_FIVE = -2;

    public static final int TYPE_KLINE_DAY = 0;
    public static final int TYPE_KLINE_WEEK = 1;
    public static final int TYPE_KLINE_MONTH = 2;
    public static final int TYPE_KLINE_MINUTE_1 = 3;
    public static final int TYPE_KLINE_MINUTE_5 = 4;
    public static final int TYPE_KLINE_MINUTE_15 = 5;
    public static final int TYPE_KLINE_MINUTE_30 = 6;
    public static final int TYPE_KLINE_MINUTE_60 = 7;

    public static final String LOGIN_FAILED_TIME = "login_failed_time";
//    public static final String GUIDE = "guide_" + BuildConfig.VERSION_CODE;
    public static final String APP_CONFIG = "app_config";
    public static final String SEVER_URL_LIST = "severUrlList";
    public static final String CONNECTION = "connection";
    public static final String VERIFY_CODE = "verifyCode";
    public static final String OPENID = "openid";
    public static final String OPENIDTYPE = "openidType";
    public static final String ACCESSTOKEN = "access_token";
    public static final int INIT_TO_REGISTER = 0;
    public static final int INIT_TO_LOGIN = 1;
    public static final String HK_MARKET_VALUE = "hk";
    public static final String USA_MARKET_VALUE = "ix";
    public static final String SZ_MARKET_VALUE = "sz";
    public static final String SH_MARKET_VALUE = "sh";
    public static final String LOGIN_REGISTER_CONFIG = "loginRegisterConfig";
    public static final String PDF_DIRECTORY = "InfoFiles";
    public static final String IS_HK_ORDER_CHECKED = "isHKOderChecked";
    public static final String IS_USA_ORDER_CHECKED = "isUSOderChecked";
    public static final String IS_CN_ORDER_CHECKED = "isCNOderChecked";
    public static final String REGISTER_EMAIL = "email";
    public static final String REGISTER_PHONE = "mobile";
    public static final String REGISTER_YOUYU_ID = "yyid";
    public static final String BROKER = "broker";
    public static final String WM = "wm";
    public static final String ESOP = "esop";
    public static final String A_CACHE = "ACache";
    public static final String HAS_ENTER_UN_USE_ASSERT_PAGE = "hasEnterUnUsePage";
    public static final int SEND_ACTIVATE_CODE_BY_EXPRESSAGE = 0;
    public static final int SEND_ACTIVATE_CODE_BY_EMAIL = 1;
    public static final String HAS_SHOW_SUCCESS_JUDGE_RISK = "hasShowSuccessJudgeRisk";
    public static final int HK_COMMON = 1;
    public static final int HK_VIP = 2;

    public static final String HAS_SELECT_ALL = "hasSelectAll";
    public static final String HAS_CANCEL_SELECT_ALL = "hasCancelSelectAll";
    public static final String HAS_CHANGE_SELECT_ITEM = "hasChangeSelectItem";

    /* IdType*/
    public static final int MAIN_LAND_ID = 1;
    public static final int HK_ID = 2;

    //账户类型
    public static final String CASH = "Cash";
    public static final String MARGIN = "Margin";
    public static final int VERIFY_LOGIN_PWD = 1;
    public static final int VERIFY_TRADE_PWD = 2;
    //风格
    public static final int BLACK_STYLE = 2;
    public static final int WHITE_STYLE = 1;
    public static final int NONE_STYLE = Integer.MIN_VALUE;

    public static final String CLOSE = "close_key";

    private Constants() {
    }
}
