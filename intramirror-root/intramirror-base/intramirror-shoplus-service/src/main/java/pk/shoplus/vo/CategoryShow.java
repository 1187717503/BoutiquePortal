package pk.shoplus.vo;

import java.awt.List;

import pk.shoplus.model.Category;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月4日 下午4:37:41
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class CategoryShow {

	public Category category;
	public List children;

	public Category getCategory() {
		return category;
	}

	public List getChildren() {
		return children;
	}

}
