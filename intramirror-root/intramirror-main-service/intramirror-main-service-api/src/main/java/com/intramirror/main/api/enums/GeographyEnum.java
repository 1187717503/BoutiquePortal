package com.intramirror.main.api.enums;

/**
 * Created by 123 on 2018/4/12.
 */
public enum GeographyEnum {
    CHINA_MAINLAND("1","中国大陆","China Mainland"),
    HONGKONG("2","港澳地区","HongKong"),
    EUROPEAN_UNION("3","欧盟","European Union"),
    ASIA("4","亚洲","Asia"),
    OTHER("5","其他地区","Other");

    private String id;
    private String name;
    private String englishName;

    private GeographyEnum(String id, String name, String englishName) {
        this.id = id;
        this.name = name;
        this.englishName = englishName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
}
