package pk.shoplus.service;

import java.util.List;
import java.util.Map;
import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.BoutiqueImage;

public class BoutiqueImageService {

    /**
     * 获取数据库连接
     */
    private EntityDao<BoutiqueImage> boutiqueImageDao = null;

    /**
     * @param conn
     */
    public BoutiqueImageService(Connection conn) {
        boutiqueImageDao = new EntityDao<BoutiqueImage>(conn);
    }

    public BoutiqueImage getBoutiqueImageByCondition(Map<String, Object> condition) throws Exception {
        List<BoutiqueImage> data = boutiqueImageDao.getByCondition(BoutiqueImage.class, "*", condition);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }

    public void updateBoutiqueImage(BoutiqueImage boutiqueImage) throws Exception {
        boutiqueImageDao.updateById(boutiqueImage);
    }

    public void createBoutiqueImage(BoutiqueImage boutiqueImage) throws Exception {
        boutiqueImageDao.create(boutiqueImage);
    }
}
