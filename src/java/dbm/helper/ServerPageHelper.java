package dbm.helper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页
 * @author lxj
 * @date 2015年12月1日 
 * @example 
 * 		PageHelper<T> pm = new PageHelper<T>(list,0);
        list = pm.getObjects(2);//获取 list 分页后的第二页
        boolean page_flag,int page_cnt,int page_index
 */
public class ServerPageHelper<T> {
    private int page = 1; // 当前页
    public int totalPages = 0; // 总页数
    private int pageRecorders;// 默认每页10条数据，也可自定义数量
    private int totalRows = 0; // 总数据数
    private int pageStartRow = 0;// 每页的起始数
    private int pageEndRow = 0; // 每页显示数据的终止数
    private boolean hasNextPage = false; // 是否有下一页
    private boolean hasPreviousPage = false; // 是否有前一页
    private List<T> list;

    /**
     * 按照自定义方式分页：每页 pageRecorders 条记录
     * @param list
     * @param pageRecorders 为0表示默认每页10条记录
     */
    public ServerPageHelper(List<T> list,int pageRecorders) {
    	init(list, (pageRecorders<=0)?10:pageRecorders);// 通过对象集，记录总数划分
    }
    
    /**
     * 初始化list，并告之该list每页的记录数
     * @param list
     * @param pageRecorders
     */
    public void init(List<T> list, int pageRecorders) {
    	if( list == null )
    		return;

        this.pageRecorders = pageRecorders;
        this.list = list;
        totalRows = list.size();

        hasPreviousPage = false;

        if ((totalRows % pageRecorders) == 0) {
            totalPages = totalRows / pageRecorders;
        } else {
            totalPages = totalRows / pageRecorders + 1;
        }

        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }

        if (totalRows < pageRecorders) {
            this.pageStartRow = 0;
            this.pageEndRow = totalRows;
        } else {
            this.pageStartRow = 0;
            this.pageEndRow = pageRecorders;
        }
    }

    // 判断要不要分页
    public boolean isNext() {
        return list.size() > 10;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    /**
     * 
     * @return
     */
    public List<T> getNextPage() {
        page = page + 1;
        disposePage();
        return getObjects(page);
    }

    /**
     * 处理分页
     */
    private void disposePage() {
        if (page == 0) {
            page = 1;
        }
        if ((page - 1) > 0) {
            hasPreviousPage = true;
        } else {
            hasPreviousPage = false;
        }

        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }
    }

    /**
     * 上一页
     * 
     * @return
     */
    public List<T> getPreviousPage() {
        page = page - 1;

        if ((page - 1) > 0) {
            hasPreviousPage = true;
        } else {
            hasPreviousPage = false;
        }
        if (page >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }
        return getObjects(page);
    }

    /**
     * 获取第几页的内容
     * 
     * @param page
     * @return
     */
    public List<T> getObjects(int page) {
        if (page == 0) {
            this.setPage(1);
            page = 1;
        } else {
            this.setPage(page);
        }

        this.disposePage();

        if (page * pageRecorders < totalRows) {// 判断是否为最后一页
            pageEndRow = page * pageRecorders;
            pageStartRow = pageEndRow - pageRecorders;
        } else {
            pageEndRow = totalRows;
            pageStartRow = pageRecorders * (totalPages - 1);
        }

        List<T> objects = null;
        if (!list.isEmpty()) {
            objects = list.subList(pageStartRow, pageEndRow);
        }
        return objects;
    }

    /**
     * 第一页
     * 
     * @return
     */
    public List<T> getFistPage() {
        if (this.isNext()) {
            return list.subList(0, pageRecorders);
        } else {
            return list;
        }
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages
     *            the totalPages to set
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return the pageRecorders
     */
    public int getPageRecorders() {
        return pageRecorders;
    }

    /**
     * @param pageRecorders
     *            the pageRecorders to set
     */
    public void setPageRecorders(int pageRecorders) {
        this.pageRecorders = pageRecorders;
    }

    /**
     * @return the totalRows
     */
    public int getTotalRows() {
        return totalRows;
    }

    /**
     * @param totalRows
     *            the totalRows to set
     */
    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    /**
     * @return the pageStartRow
     */
    public int getPageStartRow() {
        return pageStartRow;
    }

    /**
     * @param pageStartRow
     *            the pageStartRow to set
     */
    public void setPageStartRow(int pageStartRow) {
        this.pageStartRow = pageStartRow;
    }

    /**
     * @return the pageEndRow
     */
    public int getPageEndRow() {
        return pageEndRow;
    }

    /**
     * @param pageEndRow
     *            the pageEndRow to set
     */
    public void setPageEndRow(int pageEndRow) {
        this.pageEndRow = pageEndRow;
    }

    /**
     * @return the hasNextPage
     */
    public boolean isHasNextPage() {
        return hasNextPage;
    }

    /**
     * @param hasNextPage
     *            the hasNextPage to set
     */
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    /**
     * @return the list
     */
    public List<T> getList() {
        return list;
    }

    /**
     * @param list
     *            the list to set
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * @return the hasPreviousPage
     */
    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }


    /**
     * 分页函数
     * @author lxj
     * @date 2015年12月7日
     * @param page_flag 接收是否分页 "true"、"false" 字符串
     * @param page_cnt 接收每页数量
     * @param page_index 接收分页后的页码（从第一页开始）
     */
    public static List<Map<String, Object>> getListFromPage(String page_flag,
    		String page_cnt,String page_index,List<Map<String, Object>> list){
    	try {
    		String orderby_field ="";
    		String orderby_order ="";
    		if( null!=orderby_field && !"".equals(orderby_field) ){
    			list = getOrderList(list,orderby_field,orderby_order);
    		}
	    	if( page_flag.equals("true") ){
	    		if( null==page_cnt || "".equals(page_cnt) ){
	    			 ServerPageHelper<Map<String, Object>> pm = new ServerPageHelper<Map<String, Object>>(
	    					 list,0);
	    			 if( null==page_index || "".equals(page_index) ){
	    				return pm.getObjects(1);
	    			 }
	    			 else {
						return pm.getObjects(Integer.parseInt(page_index));
					}
	    		}
	    		else{
	    			ServerPageHelper<Map<String, Object>> pm = new ServerPageHelper<Map<String, Object>>(
	    					list,Integer.parseInt(page_cnt));
	   			 	if( null==page_index || "".equals(page_index) ){
	   			 		return pm.getObjects(1);
	   			 	}
	   			 	else {
						return pm.getObjects(Integer.parseInt(page_index));
					}
	    		}
	    	}
	     	else //if( page_flag==null || page_flag.equals("") || page_flag.equals("false") ){
	    		return list;
    	
	    } catch (Exception e) {
	    	//logger.info("PageHelper参数（page_cnt、page_index）不能转换为整数");
	    	return list;
	    }
    }
    
    /**
     * 分页函数
     * @author lxj
     * @date 2015年12月7日
     * @param page_flag 接收是否分页 "true"、"false" 字符串
     * @param page_cnt 接收每页数量
     * @param page_index 接收分页后的页码（从第一页开始）
     */
    public static List<Map<String, Object>> getListFromPage(String page_flag,
    		String page_cnt,String page_index,String orderby_field,String orderby_order,
    		List<Map<String, Object>> list){
    	try {
	    	if( page_flag.equals("true") ){
	    		if( null!=orderby_field && !"".equals(orderby_field) ){
	    			list = getOrderList(list,orderby_field,orderby_order);
	    		}
	    		if( null==page_cnt || "".equals(page_cnt) ){
	    			 ServerPageHelper<Map<String, Object>> pm = new ServerPageHelper<Map<String, Object>>(
	    					 list,0);
	    			 if( null==page_index || "".equals(page_index) ){
	    				return pm.getObjects(1);
	    			 }
	    			 else {
						return pm.getObjects(Integer.parseInt(page_index));
					}
	    		}
	    		else{
	    			ServerPageHelper<Map<String, Object>> pm = new ServerPageHelper<Map<String, Object>>(
	    					list,Integer.parseInt(page_cnt));
	   			 	if( null==page_index || "".equals(page_index) ){
	   			 		return pm.getObjects(1);
	   			 	}
	   			 	else {
						return pm.getObjects(Integer.parseInt(page_index));
					}
	    		}
	    	}
	     	else //if( page_flag==null || page_flag.equals("") || page_flag.equals("false") ){
	    		return list;
    	
	    } catch (Exception e) {
	    	//logger.info("PageHelper参数（page_cnt、page_index）不能转换为整数");
	    	return list;
	    }
    }
    
    private static List<Map<String, Object>> getOrderList(List<Map<String, Object>> list,
    		String orderby_field,String orderby_order) {
    	if( null==orderby_field || "".equals(orderby_field) )
    		return list;
    	if( null==list || list.isEmpty() )
    		return list;
    	if( !list.get(0).containsKey(orderby_field) ){
    		//EhCache.logger.info("results don't contains field: "+orderby_field );
    		return list;
    	}
    	//以下需要对 list 中的 orderby_field 字段进行排序处理
    	 Collections.sort(list,new SortHelper(orderby_order,orderby_field));
    	
		return list;
	}
    

	/*
     * 
     * @param args
     */
    public static void main(String[] args) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for(int i=1;i<50;i++){
		    Map<String, Object> tmpMap = new HashMap<String, Object>();
		    tmpMap.put("a","a"+i);
		    tmpMap.put("b", "b"+(100-i));
		    list.add(tmpMap);
        }
        System.out.println(list.size());

        ServerPageHelper<Map<String, Object>> pm = new ServerPageHelper<Map<String, Object>>(list,0);// 每页显示条数

        System.out.println(pm.getObjects(7));

    }

}