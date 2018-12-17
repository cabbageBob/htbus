package htbus.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import htbus.entity.MatedataTree;
import net.sf.json.JSONArray;

public class TreeBuilder {
	/**2018年7月20日上午8:12:43
	 *
	 * @author Jokki
	 *  
	 */
	List<MatedataTree> nodes = new ArrayList<>();

	public TreeBuilder(){
	}
	
	public List<MatedataTree> buildTree(List<MatedataTree> nodes){
		TreeBuilder treeBuilder = new TreeBuilder(nodes);
        return treeBuilder.buildTree();
	}
	
	public TreeBuilder(List<MatedataTree> nodes){
		super();
		this.nodes = nodes;
	}
	
	// 构建JSON树形结构
    public String buildJSONTree() {
        List<MatedataTree> nodeTree = buildTree();
        JSONArray jsonArray = JSONArray.fromObject(nodeTree);
        return jsonArray.toString();
    }

    // 构建树形结构
    public List<MatedataTree> buildTree() {
        List<MatedataTree> treeNodes = new ArrayList<>();
        List<MatedataTree> rootNodes = getRootNodes();
        for (MatedataTree rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }

    // 递归子节点
    public void buildChildNodes(MatedataTree node) {
        List<MatedataTree> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (MatedataTree child : children) {
                buildChildNodes(child);
            }
            node.setChild(children);
        }
    }

    // 获取父节点下所有的子节点
    public List<MatedataTree> getChildNodes(MatedataTree pnode) {
        List<MatedataTree> childNodes = new ArrayList<>();
        for (MatedataTree n : nodes) {
            if (pnode.getCode().equals(n.getParent_code())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }
    
    // 判断是否为根节点
    public boolean rootNode(MatedataTree node) {
        boolean isRootNode = true;
        for (MatedataTree n : nodes) {
            if (node.getParent_code().equals(n.getCode())) {
                isRootNode = false;
                break;
            }
        }
        return isRootNode;
    }

    // 获取集合中所有的根节点
    public List<MatedataTree> getRootNodes() {
        List<MatedataTree> rootNodes = new ArrayList<>();
        for (MatedataTree n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }
}
