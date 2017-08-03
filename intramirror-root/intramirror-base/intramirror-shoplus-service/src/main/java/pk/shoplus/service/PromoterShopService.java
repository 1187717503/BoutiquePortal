package pk.shoplus.service;

import org.sql2o.Connection;
import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.PromoterShop;
import pk.shoplus.parameter.EnabledType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoterShopService {
	private EntityDao<PromoterShop> promoterShopDao;

	public PromoterShopService(Connection conn) {
		promoterShopDao = new EntityDao<>(conn);
	}

	public PromoterShop getPromoterShopByShopId(Long shop_id) throws Exception {
		Map<String, Object> condition = new HashMap<>();
		condition.put("enabled", EnabledType.USED);
		condition.put("shop_id", shop_id);
		List<PromoterShop> list = promoterShopDao.getByCondition(PromoterShop.class, condition);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public PromoterShop createPromoterShop(PromoterShop promoterShop) throws Exception {
		long promoter_shop_id = promoterShopDao.create(promoterShop);
		if (promoter_shop_id > 0) {
			promoterShop.promoter_shop_id = promoter_shop_id;
		} else {
			throw new Exception("Creation failed");
		}
		return promoterShop;
	}

	public void updatePromoterShop(PromoterShop promoterShop) throws Exception {
		promoterShopDao.updateById(promoterShop);
	}
}
