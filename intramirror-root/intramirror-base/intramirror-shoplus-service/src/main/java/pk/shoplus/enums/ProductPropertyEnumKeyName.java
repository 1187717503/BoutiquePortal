package pk.shoplus.enums;

/**
 * @Author: DingYiFan
 * @Description:
 * @Date: Create in 14:21 2017/5/25
 * @Modified By:
 */
public enum ProductPropertyEnumKeyName {

    Composition("Composition",""),
    CarryOver("CarryOver",""),
    ColorDescription("ColorDescription",""),
    MadeIn("MadeIn",""),
    SizeFit("SizeFit",""),
    ColorCode("ColorCode",""),
    BrandID("BrandID",""),
    SeasonCode("SeasonCode","");

    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    ProductPropertyEnumKeyName(String code, String value) {
        this.value = value;
        this.code = code;
    }
}
