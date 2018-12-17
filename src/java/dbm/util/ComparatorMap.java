package dbm.util;

import java.util.Comparator;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;

public class ComparatorMap implements Comparator<Map<String, Object>> {
	@Override
	public int compare(Map<String, Object>m1, Map<String, Object>m2){
		String s1 = m1.get("stnm").toString();
		String s2 = m2.get("stnm").toString();
		return ToPinYinString(s1).compareTo(ToPinYinString(s2));
	}
	private String ToPinYinString(String str){  
        
        StringBuilder sb=new StringBuilder();  
        String[] arr=null;  
           
        for(int i=0;i<str.length();i++){  
            arr=PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));  
            if(arr!=null && arr.length>0){  
                for (String string : arr) {  
                    sb.append(string);  
                }  
            }  
        }  
           
        return sb.toString();  
    }  
}
