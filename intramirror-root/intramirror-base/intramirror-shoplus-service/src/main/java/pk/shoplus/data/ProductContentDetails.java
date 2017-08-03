package pk.shoplus.data;

import java.math.BigDecimal;


/**
 * Created by chone on 2017/4/13.
 */
public class
ProductContentDetails {


    private ProductContentRating contentRating;


    private ThumbnailDetails thumbnails;


    private BigDecimal width;


    private BigDecimal height;


    private BigDecimal length;


    private BigDecimal weight;


    private BigDecimal size;


    private String sizeFit;


    private String sizeCountry;


    private String colorCode;


    private String colorDescription;


    private String madeIn;


    private String composition;


    private String carryOver;


    /**
     * Real brand id, given by real boutique.
     */
    private String brandCode;


    private String seasonCode;


    private int bestSellerNumber;


    private int newThisWeekNumber;


    private int recommendNumber;


    public int getRecommendNumber() {
        return recommendNumber;
    }


    public ProductContentDetails setRecommendNumber(int number) {
        recommendNumber = number;
        return this;
    }


    public int getNewThisWeekNumber() {
        return newThisWeekNumber;
    }


    public ProductContentDetails setNewThisWeekNumber(int number) {
        newThisWeekNumber = number;
        return this;
    }


    public int getBestSellerNumber() {
        return bestSellerNumber;
    }


    public ProductContentDetails setBestSellerNumber(int number) {
        bestSellerNumber = number;
        return this;
    }


    public String getSizeCountry() {
        return sizeCountry;
    }


    public ProductContentDetails setSizeCountry(String sizeCountry) {
        this.sizeCountry = sizeCountry;
        return this;
    }


    public String getSizeFit() {
        return sizeFit;
    }


    public ProductContentDetails setSizeFit(String sizeFit) {
        this.sizeFit = sizeFit;
        return this;
    }


    public String getComposition() {
        return composition;
    }


    public ProductContentDetails setComposition(String composition) {
        this.composition = composition;
        return this;
    }


    public String getCarryOver() {
        return carryOver;
    }


    public ProductContentDetails setCarryOver(String carryOver) {
        this.carryOver = carryOver;
        return this;
    }


    public ThumbnailDetails getThumbnails() {
        return thumbnails;
    }


    public ProductContentDetails setThumbnails(ThumbnailDetails thumbnails) {
        this.thumbnails = thumbnails;
        return this;
    }


    public String getMadeIn() {
        return madeIn;
    }


    public ProductContentDetails setMadeIn(String madeIn) {
        this.madeIn = madeIn;
        return this;
    }

    public String getColorDescription() {
        return colorDescription;
    }


    public ProductContentDetails setColorDescription(String colorDescription) {
        this.colorDescription = colorDescription;
        return this;
    }

    public String getSeasonCode() {
        return seasonCode;
    }


    public ProductContentDetails setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
        return this;
    }


    public String getBrandCode() {
        return brandCode;
    }


    public ProductContentDetails setBrandCode(String brandCode) {
        this.brandCode = brandCode;
        return this;
    }


    public String getColorCode() {
        return colorCode;
    }


    public ProductContentDetails setColorCode(String colorCode) {
        this.colorCode = colorCode;
        return this;
    }


    public BigDecimal getSize() {
        return size;
    }


    public ProductContentDetails setSize(BigDecimal size) {
        this.size = size;
        return this;
    }


    public BigDecimal getLength() {
        return length;
    }


    public ProductContentDetails setLength(BigDecimal length) {
        this.length = length;
        return this;
    }


    public BigDecimal getWeight() {
        return weight;
    }


    public ProductContentDetails setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }


    public BigDecimal getHeight() {
        return height;
    }


    public ProductContentDetails setHeight(BigDecimal height) {
        this.height = height;
        return this;
    }


    public BigDecimal getWidth() {
        return width;
    }


    public ProductContentDetails setWidth(BigDecimal width) {
        this.width = width;
        return this;
    }


    public ProductContentRating getContentRating() {
        return contentRating;
    }


    public ProductContentDetails setContentRating(ProductContentRating contentRating) {
        this.contentRating = contentRating;
        return this;
    }


}
