package pk.shoplus.model;

import java.util.List;

/**
 * product 实体类
 * Thu Dec 01 18:07:15 CST 2016
 */
public class Variants {
    public String sizerange;

    public String sizeposition;

    public String size;

    public String quantity;

    public List<String> barcodes;

    public String getSizerange() {
        return sizerange;
    }

    public String getSizeposition() {
        return sizeposition;
    }

    public String getSize() {
        return size;
    }

    public String getQuantity() {
        return quantity;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }
}

