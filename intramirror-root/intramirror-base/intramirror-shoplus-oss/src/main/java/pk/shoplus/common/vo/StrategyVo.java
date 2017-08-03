package pk.shoplus.common.vo;

/**
 * Created by mingfly on 15/5/11.
 * 裁剪策略实体
 */
public class StrategyVo {
    private Integer width;
    private Integer height;
    private Double quality=1.0D;

    public StrategyVo() {
    }

    public StrategyVo(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    public StrategyVo(Integer width, Integer height, Double quality) {
        this.width = width;
        this.height = height;
        this.quality = quality;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }
}
