package pk.shoplus.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.dao.EntityDao;
import pk.shoplus.model.Comment;
import pk.shoplus.model.Page;
import pk.shoplus.parameter.EnabledType;

public class CommentService {

	/**
	 * 获取数据库连接
	 */
	private EntityDao<Comment> commentDao = null;

	/**
	 * @param conn
	 */
	public CommentService(Connection conn) {
		commentDao = new EntityDao<Comment>(conn);
	}

	/**
	 * 
	 * @param Comment
	 * @throws Exception
	 */
	public Comment createComment(Comment comment) throws Exception {
		try {
			Long comment_id = commentDao.create(comment);
			if (comment_id > 0) {
				comment.comment_id = comment_id;
			} else {
				comment = null;
			}
			return comment;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据shop product id查询Comment list
	 * 
	 * @param page,
	 *            shop_product_id
	 * @return
	 * @throws Exception
	 */
	public Page getCommentListByShopProductIdPage(Page page, long shop_product_id) {
		try {
			StringBuilder fieldNames = new StringBuilder("");

			fieldNames.append(" *, (select content from comment as rc " + "where rc.reply_comment_id = a.comment_id)")
					.append(" as relay_content,")
					.append("	(select cover_img from product as p where p.product_id = a.product_id) as img,")
					.append(" (select username from user as ru " + "where ru.user_id = a.user_id) as customerName");

			StringBuilder tableName = new StringBuilder("");

			tableName.append(" (select r.product_id, c.root_id, c.comment_id, ")
					.append("c.user_id, c.reply_comment_id, c.content, c.updated_at, r.shop_id,")
					.append(" r.rate ,c.enabled from comment as c, rate_product as r ")
					.append("where c.comment_id = r.comment_id and r.shop_product_id = :p1").append(") as a");
			String orderBy = "updated_at desc";
			String whereCondition = "enabled = :p2";
			page = commentDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames.toString(), tableName.toString(),
					whereCondition, orderBy, new Object[] { shop_product_id, EnabledType.USED }, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 根据shop id查询Comment list
	 * 
	 * @param page,
	 *            shop_id
	 * @return
	 * @throws Exception
	 */
	public Page getCommentListByShopIdPage(Page page, long shop_id) {
		try {
			StringBuilder fieldNames = new StringBuilder("");

			fieldNames.append("*, (SELECT content FROM comment AS rc " + "WHERE rc.reply_comment_id = a.comment_id limit 1)")
					.append(" AS relay_content,")
					.append("	(SELECT cover_img FROM product AS p WHERE p.product_id = a.product_id) AS img,")
					.append(" (SELECT username FROM user as ru " + "WHERE ru.user_id = a.user_id) AS customerName");

			StringBuilder tableName = new StringBuilder("");

			tableName.append(" (select r.product_id, c.root_id, c.comment_id, ")
					.append("c.user_id, c.reply_comment_id, c.content, c.updated_at, r.shop_id,")
					.append(" r.rate ,c.enabled from comment as c, rate_product as r ")
					.append("where c.comment_id = r.comment_id and r.shop_id = ").append(shop_id).append(") as a");
			String orderBy = "updated_at desc";
			String groupBy = "comment_id";
			String whereCondition = "enabled = :p1";
			page = commentDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames.toString(), tableName.toString(),
					whereCondition, orderBy, new Object[] { EnabledType.USED }, null, groupBy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 根据user id查询Comment list
	 * 
	 * @param page,
	 *            shop_id
	 * @return
	 * @throws Exception
	 */
	public Page getCommentListByUserIdPage(Page page, long user_id) {
		try {
			String fieldNames = "* ";
			StringBuilder tableName = new StringBuilder("");
			tableName.append("(SELECT r.rate, c.comment_id , c.user_id, r.shop_id ,")
					.append(" c.reply_comment_id, c.updated_at, c.enabled").append(" ,c.content, r.product_id")
					.append(" FROM comment as c, rate_product AS r ")
					.append(" WHERE c.user_id =:p1 AND c.root_id = -1 AND r.comment_id")
					.append(" = c.comment_id) AS a LEFT JOIN ")
					.append(" (SELECT s.name , s.cover_img, s.product_id FROM product as s")
					.append(" ) AS b  ON a.product_id = b.product_id");
			String orderBy = "updated_at DESC";
			String whereCondition = "enabled = :p2";
			page = commentDao.getPageBySql(page.pageNumber, page.pageSize, fieldNames, tableName.toString(),
					whereCondition, orderBy, new Object[] { user_id, EnabledType.USED }, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * 根据comment id 获取 comment
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Comment getCommentById(Long id) throws Exception {
		try {
			Comment comment = null;
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("enabled", EnabledType.USED);
			condition.put("comment_id", id);

			List<Comment> list = this.commentDao.getByCondition(Comment.class, condition);

			for (Comment temp : list) {
				if (temp != null) {
					comment = temp;
					break;
				}
			}
			return comment;
		} catch (Exception e) {
			throw e;
		}
	}
}
