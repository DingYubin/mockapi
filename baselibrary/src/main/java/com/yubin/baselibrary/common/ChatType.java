package com.yubin.baselibrary.common;

public enum ChatType {
    P(Constant.CHAT_TYPE_P),
    G(Constant.CHAT_TYPE_G),
    R(Constant.CHAT_TYPE_ROBOT),
    UNKNOWN(Constant.CHAT_TYPE_UNKNOWN);

    private int value;

    ChatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ChatType valueOf(int value) {
        switch (value) {
            case Constant.CHAT_TYPE_P:
                return P;
            case Constant.CHAT_TYPE_G:
                return G;
            case Constant.CHAT_TYPE_ROBOT:
                return R;
            default:
                return UNKNOWN;
        }
    }
}
