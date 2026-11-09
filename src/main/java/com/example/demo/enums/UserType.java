package com.example.demo.enums;

public enum UserType {
    NORMAL(0, "管理员"),
    ADMIN(1, "普通用户"),
    VISITOR(2, "访客");

    private final Integer code;
    private final String desc;

    UserType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserType getByCode(Integer code) {

        // 遍历全部枚举
        for (UserType item : UserType.values()) {
            // 重点：拿传入的code去equals，防止空指针
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        return null;
    }
}
