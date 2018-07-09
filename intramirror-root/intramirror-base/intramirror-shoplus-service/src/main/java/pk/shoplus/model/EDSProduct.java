package pk.shoplus.model;

import java.util.List;

/**
 * product 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
public class EDSProduct {
    public String product_id;

    public String product_reference;

    public String color_reference;

    public String first_category;

    public String second_category;

    public String gender;

    public String brand;

    public String item_name;

    public String item_intro;

    public String item_description;

    public String color;

    public String season_year;

    public String season_reference;

    public String made_in;

    public List<SuiTable> suitable;

    public List<TechnicalInfo> technical_info;

    public String price;

    public String currency;

    public List<Variants> variants;

    public List<ItemImages> item_images;

    public String retail_price;

    public String getProduct_id() {
        return product_id;
    }

    public String getProduct_reference() {
        return product_reference;
    }

    public String getColor_reference() {
        return color_reference;
    }

    public String getFirst_category() {
        return first_category;
    }

    public String getSecond_category() {
        return second_category;
    }

    public String getGender() {
        return gender;
    }

    public String getBrand() {
        return brand;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_intro() {
        return item_intro;
    }

    public String getItem_description() {
        return item_description;
    }

    public String getColor() {
        return color;
    }

    public String getSeason_year() {
        return season_year;
    }

    public String getSeason_reference() {
        return season_reference;
    }

    public String getMade_in() {
        return made_in;
    }

    public List<SuiTable> getSuitable() {
        return suitable;
    }

    public List<TechnicalInfo> getTechnical_info() {
        return technical_info;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public List<Variants> getVariants() {
        return variants;
    }

    public List<ItemImages> getItem_images() {
        return item_images;
    }

    public String getRetail_price() {
        return retail_price;
    }
}

