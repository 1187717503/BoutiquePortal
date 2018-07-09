package pk.shoplus.common;

import java.util.ArrayList;
import java.util.List;



/**
 * @author  author :
 * @date created_at：2016年10月14日 下午12:06:21
 * @version 1.0 
*/
public class TreeNode {
	private		long	  	id = -1;
	private		long	 	parentId = -1;
	private     int			level = -1;
	private		String 		data;
	private		List<TreeNode> children =  new ArrayList<TreeNode>();
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	
	
	public long getId() {
		return id;
	}

	public long getParentId() {
		return parentId;
	}

	public String getData() {
		return data;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setData(String data) {
		this.data = data;
	}

	public TreeNode(long id, long parentId, String data) {
		this.id = id;
		this.parentId = parentId;
		this.data = data;
	}
	
	/**
	 * 添加子节点
	 * @param child
	 */
	public void addChild(TreeNode child) {
		child.setLevel(this.level + 1);
		this.children.add(child);
	}
	
	/**
	 * 是否叶子节点
	 */
	public boolean isLeave(){
		boolean ret = true;
		if(this.children.size()>0)
			ret = false;
		return ret;
	}
	
	/**
	 * 先序遍历，拼接JSON字符串
	 */
	public String toJsonString() {
		String result = "{" + 
						"\"id\" : " + this.id  + 
						", \"parentId\" : " + this.parentId +
						", \"level\" : " + this.level +
						", \"data\" : " + this.data;
		if (children.size() != 0) {
			result += ", \"children\" : [";
			for (int i = 0; i < children.size(); i++) {
				result += ((TreeNode) children.get(i)).toJsonString() + ",";   
			}
			result = result.substring(0, result.length() - 1);
			result += "]";
		} else {
			result += ", \"leaf\" : \"true\"";
		}
		return result + "}";
	 }
	
	/*
	public String leave2String() {
		String result = "";
		for(TreeNode item: children){
			if(item.children.size() == 0) {
				result += "{" +"id : " + item.getId() +
						", level : " + item.getLevel() +
						", parentId : " + item.getParentId() +
						", data : " + item.getData() + " } ,";
			}else {
				result += item.leave2String() + ",";
			}
		}
		if(result!="")
		{
			result = result.substring(0, result.length()-1);
			return "[" + result + "]";
		}
		return "";
	}
	*/

}
