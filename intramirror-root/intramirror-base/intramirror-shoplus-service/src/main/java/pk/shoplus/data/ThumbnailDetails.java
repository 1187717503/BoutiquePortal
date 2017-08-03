package pk.shoplus.data;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by chone on 2017/4/12.
 */
public class ThumbnailDetails extends java.util.ArrayList<Thumbnail> {


    public static ThumbnailDetails fromString(String str) {
        ThumbnailDetails thumbnails = new ThumbnailDetails();
        if (str != "") {
            Gson gson = new Gson();
            String[] imgs = gson.fromJson(str, String[].class);
            if (imgs != null) {
                for (String img : imgs) {
                    Thumbnail thumbnail = new Thumbnail(img);
                    thumbnails.add(thumbnail);
                }
            }
        }
        return  thumbnails;
    }

}
