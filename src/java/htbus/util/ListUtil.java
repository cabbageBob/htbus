package htbus.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtil {
	/**
	 * 将list map 转为以其中一个字符字段的值作为key的map，此key的值不重复
	 * @param list
	 * @param key
	 * @return
	 */
	public static Map<String,Map<String,Object>> list2Map(List<Map<String,Object>> list,String key){
		Map<String, Map<String, Object>> map = new HashMap<String,Map<String, Object>>();
		for(Map<String,Object> m : list){
			map.put(m.get(key).toString(), m);
		}
		return map;
	}
	
	/**
	 * 将两个list合并，取出list2中pors指定的字段给list1
	 * @param list1
	 * @param list2
	 * @param pors
	 * @return
	 */
	public static List<Map<String,Object>> concatList(List<Map<String,Object>> list1,List<Map<String,Object>> list2,String key,String[] pors){
		Map<String,Map<String, Object>> source = list2Map(list2,key);
		for(Map<String,Object> map : list1){
			for(String p:pors){
				map.put(p, source.get(key).get(p));
			}
		}
		return list1;
	}
	
	/**
	 * 将字符串数组按拼接符拼接起来
	 * @param join
	 * @param strAry
	 * @return
	 */
	public static String join(String join,String[] strAry){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<strAry.length;i++){
             if(i==(strAry.length-1)){
                 sb.append(strAry[i]);
             }else{
                 sb.append(strAry[i]).append(join);
             }
        }
        
        return new String(sb);
    }
}
