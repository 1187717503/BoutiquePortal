package pk.shoplus.common.vo;

/**
 * Created by mingfly on 15/5/11.
 * 文件存储相应Vo
 *
 */
public class MediaStorangeRespVo {
    private String url;
    private String httpUrl;

    public MediaStorangeRespVo() {
    }

    public MediaStorangeRespVo(String url, String httpUrl) {
        this.url = url;
        this.httpUrl = httpUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

}
