package pk.shoplus.data;

/**
 * Created by chone on 2017/4/12.
 */
public class Thumbnail {

    private String url;


    public Thumbnail(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }


    public Thumbnail setUrl(String url) {
        this.url = url;
        return this;
    }


}
