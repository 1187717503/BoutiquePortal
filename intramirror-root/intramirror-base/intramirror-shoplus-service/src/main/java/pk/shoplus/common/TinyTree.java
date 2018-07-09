package pk.shoplus.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  author :
 * @date created_at：2016年10月14日 下午3:38:57
 * @version 1.0 
*/
public class TinyTree {
	
	// 节点列表（映射表，用于存储节点对象）
	
	private HashMap<Long, TreeNode> nodeList = new HashMap<Long, TreeNode>();
	// 根节点
	private TreeNode  root = null;
	
	/**
	 * 插入节点
	 * @param parentId   父节点
	 * @param node		 待插入节点
	 * @return true - 成功, false - 失败
	 */
	public boolean insertNode(TreeNode node){
		boolean ret = true;
		if(  node.getParentId()<1 ){
			if(null == this.root) {
				node.setLevel(1);
				this.root = node;
				this.nodeList.put(node.getId(),node);
			}
			else
				return false;
		}else{
			TreeNode parentNode = (TreeNode)nodeList.get(node.getParentId());
			if(parentNode!=null){
				node.setLevel(parentNode.getLevel()+1);
				parentNode.addChild(node);
				nodeList.put(node.getId(),node);
			}else
				ret = false;
		}
			
		return ret;
	}
	
	/**
	 * 取所有节点的json格式字符串
	 * @return String  json格式
	 * ex:	{"id" : 1, "parentId" : 0, "level" : 1, "data" : {"name": "one"}, "children" : [
	 * 				{"id" : 2, "parentId" : 1, "level" : 2, "data" : {"name": "two"}, "leaf" : "true"},
	 * 				{"id" : 3, "parentId" : 1, "level" : 2, "data" : {"name": "three"}, "children" : [
	 * 					{"id" : 4, "parentId" : 3, "level" : 3, "data" : {"name": "four"}, "leaf" : "true"},
	 * 					{"id" : 5, "parentId" : 3, "level" : 3, "data" : {"name": "five"}, "leaf" : "true"}]},
	 * 				{"id" : 6, "parentId" : 1, "level" : 2, "data" : {"name": "six"}, "leaf" : "true"}]}
	 */
	public String getAllNodes2JsonString() {
		String result = "";
		if(root != null){
			result = root.toJsonString();
		}
		return result;
	}
	
	/**
	 * 取所有叶节点的json格式字符串
	 * @return String  json格式
	 * ex:[{id : 2, level : 2, parentId : 1, data : {name: 'two'}},
	 * 		{id : 4, level : 3, parentId : 3, data : {name: 'four'}},
	 * 		{id : 5, level : 3, parentId : 3, data : {name: 'five'}},
	 * 		{id : 6, level : 2, parentId : 1, data : {name: 'six'}}]
	 */
	@SuppressWarnings("rawtypes")
	public String getAllLeave2JsonString() {
		String result = "";
		for(Map.Entry item: this.nodeList.entrySet()){
			TreeNode node = (TreeNode)item.getValue();
			if(node.isLeave()){
				result += "{" + "\"id\" : " + node.getId() + 
								", \"parentId\" : " + node.getParentId() +
								", \"level\" : " + node.getLevel() +
								", \"data\" : " + node.getData() + "}," ;
			}
		}
		if(result!="")
			result = "[" + result.substring(0, result.length()-1) + "]";
		return result;
	}
	
	public TreeNode getTreeNodeById(long id) {
		TreeNode node = null;
		node = this.nodeList.get(id);
		return node;
	}
	
	public List<Long> getAllNodesId() {
		List<Long> idList = new ArrayList<Long>();
		idList.addAll(nodeList.keySet());
		return idList;
	}
	
	public List<Long> getAllLeaveId() {
		List<Long> idList = new ArrayList<Long>();
		for(Map.Entry<Long, TreeNode> entry:nodeList.entrySet()){
			if(entry.getValue().isLeave())
				idList.add(entry.getKey());
		}
		return idList;
	}
	
	public Long getRootId() {
		return root.getId();
	}

}
