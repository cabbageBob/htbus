package dbm.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.miao.framework.util.DateUtil;
import cn.miao.framework.util.PinyinUtil;

public class SortHelper implements Comparator<Map<String, Object>>{
	String defaultformat = "yyyy-MM-dd HH:mm:ss";
	String ascorder = "asc";
	String descorder = "desc";
	String defalutorder = ascorder;

	private String orderby_order = defalutorder;
	private String orderby_field;
	private String orderby_type = "0";//flag=0,string;flag=1,date;flag=2,double;
	
	public static void main(String[] args) {
		//int i = new SortHelper("", "").compare("张三", "李四");
		List<Map<String, Object>> list = new ArrayList<>();
		/*for( int i=0;i<10;i++){
			Map<String, Object> map = new HashMap<>();
			map.put("stcd", i);
			map.put("stnm", String.valueOf(generateCode(2)));
			list.add(map);
		}*/
		//list = new TestServiceImpl().test2();
		
		System.out.println(list);

	    Collections.sort(list,new SortHelper("desc","stnm"));
		System.out.println(list);
		
		
		//System.out.println(new SortHelper("asc","stnm").compare("白峰大浦河","白溪水文"));
	}
	
	public SortHelper(String orderby_order,String orderby_field){
		setOrderby_order(orderby_order);
		setOrderby_field(orderby_field);
	}
	
	/**
	 * @description
	 * @date 2016年9月5日下午12:24:06
	 * @author lxj
	 * @param orderby_order
	 * @param orderby_field
	 * @param orderby_type type=0,string;type=1,date;type=2,double;
	 */
	public SortHelper(String orderby_order,String orderby_field,String orderby_type){
		setOrderby_order(orderby_order);
		setOrderby_field(orderby_field);
		setOrderby_type(orderby_type);
	}
	
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		int result = 0 ;
		switch( orderby_type ){
			case "0":
				if( "tm".equals(orderby_field) || "time".equals(orderby_field)){
					Date d1 = (null==o1.get(orderby_field) || "".equals(o1.get(orderby_field)))
							?DateUtil.str2Date("1970-1-1", defaultformat):(Date) o1.get(orderby_field);
					Date d2 = (null==o2.get(orderby_field) || "".equals(o2.get(orderby_field)))
							?DateUtil.str2Date("1970-1-1", defaultformat):(Date) o2.get(orderby_field);
					result = isAsc()?compare(d1, d2):compare(d2, d1);
				}else {
					String str1 = (null==o1.get(orderby_field))?"":o1.get(orderby_field).toString();
					String str2 = (null==o2.get(orderby_field))?"":o2.get(orderby_field).toString();
					result = isAsc()?compare(str1, str2):compare(str2, str1);
				}
				break;
			case "1":
				Date d1 = (null==o1.get(orderby_field) || "".equals(o1.get(orderby_field)))
						?DateUtil.str2Date("1970-1-1", defaultformat):(Date) o1.get(orderby_field);
				Date d2 = (null==o2.get(orderby_field) || "".equals(o2.get(orderby_field)))
						?DateUtil.str2Date("1970-1-1", defaultformat):(Date) o2.get(orderby_field);
				result = isAsc()?compare(d1, d2):compare(d2, d1);		
				break;
			case "2":
				double v1 = (null==o1.get(orderby_field) || "".equals(o1.get(orderby_field)))
						?0:Double.parseDouble(o1.get(orderby_field).toString());
				double v2 = (null==o2.get(orderby_field) || "".equals(o2.get(orderby_field)))
						?0:Double.parseDouble(o2.get(orderby_field).toString());
				result = isAsc()?compare(v1, v2):compare(v2, v1);	
				break;
			default:
				if( "tm".equals(orderby_field) || "time".equals(orderby_field)){
					Date d3 = (Date) o1.get(orderby_field);
					Date d4 = (Date) o2.get(orderby_field);
					result = isAsc()?compare(d3, d4):compare(d4, d3);
				}else {
					String str1 = (null==o1.get(orderby_field))?"":o1.get(orderby_field).toString();
					String str2 = (null==o2.get(orderby_field))?"":o2.get(orderby_field).toString();
					result = isAsc()?compare(str1, str2):compare(str2, str1);
				}
				break;
		}
		return result;
	}
	
    public int compare(String o1, String o2) {
		String s1 = PinyinUtil.converterToPinYin(o1);
		String s2 = PinyinUtil.converterToPinYin(o2);
		/*String s1 = (String) o1;
		String s2 = (String) o2;*/
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min(len1, len2);
		char v1[] = s1.toCharArray();
		char v2[] = s2.toCharArray();
		int pos = 0;

		while (n-- != 0) {  
			char c1 = v1[pos];  
			char c2 = v2[pos];
			if (c1 != c2) {
				return ((c1 - c2)==0) ? 0 : ((c1 - c2)>0) ? 1 : -1; 
			}
			pos++;  
		}  
		return ((len1 - len2)==0) ? 0 : ((len1 - len2)>0) ? 1 : -1;
    }  

	public int compare(Integer o1, Integer o2) {  
		int val1 = o1.intValue();  
		int val2 = o2.intValue();  
		return (val1 == val2) ? 0 : (val1 > val2) ? 1 : -1;
    }
	
	public int compare(double o1, double o2) {  
		return (o1 == o2) ? 0 : (o1 > o2) ? 1 : -1;
    }
    
	public int compare(Date o1, Date o2) {  
		long val1 = o1.getTime();  
        long val2 = o2.getTime();
        return (val1 == val2) ? 0 : (val1 > val2) ? 1 : -1;
	}
	
	public boolean isAsc(){
		return orderby_order.equals(ascorder)?true:false;
	}
	
	public String getOrderby_order() {
		return orderby_order;
	}
	
	private void setOrderby_order(String orderby_order) {
		if( ascorder.equals(orderby_order) || descorder.equals(orderby_order))
			this.orderby_order = orderby_order;
		else{
			this.orderby_order = defalutorder;
		}
	}
	
	public String getOrderby_field() {
		return orderby_field;
	}
	
	private void setOrderby_field(String orderby_field) {
		this.orderby_field = orderby_field;
	}
	
	public String getOrderby_type() {
		return orderby_type;
	}
	
	private void setOrderby_type(String orderby_type) {
		this.orderby_type = orderby_type;
	}
	
	public static char[] generateCode(int cnt){
		String chars="0123456789abcdefghijkmlnopqrstuvwxyz";
		char[] rands = new char[cnt];
		for( int i=0;i<cnt;i++){
			int rand = (int)(Math.random()*36);
			rands[i] = chars.charAt(rand);
		}
		return rands;
	}
}
