package htbus.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.miao.framework.action.DoAction;

public class WsdlUtil {
	/**
	 * 解析wsdl文档
	 * @param wsdl webservice的说明文档地址url
	 * @return 方法列表
	 * @throws DocumentException 
	 */
	public static List<Map<String,String>> parserWsdl(String wsdl) throws DocumentException 
	{
		List<Map<String,String>> result =new ArrayList<Map<String,String>>();
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(wsdl);
		Element root = document.getRootElement();
		List<Element> method_element_list = root.element("types").element("schema").elements("element");
	    List<Element> portType_element_list = root.elements("portType").get(0).elements();
		
		//把method_root下的所有节点hash一下
		Map<String,Element> method_hashtable = new HashMap<String,Element>();
		for(Element e : method_element_list){
			method_hashtable.put(e.attributeValue("name"), e);
		}
		
		for(Element e : portType_element_list)
		{
			Map<String,String> map = new HashMap<String,String>();
			String method= e.attributeValue("name");
			String inparams = getParamsFromElement(method_hashtable.get(method));
			String outparams = getParamsFromElement(method_hashtable.get(method+"Response"));
			String return_type  = getReturntypeFromElement(method_hashtable.get(method+"Response"));
			String des = "";
			if(e.element("documentation")!=null){
				des = e.element("documentation").getText();
			}else{
				des ="无";
			}
			
			map.put("method", method);
			map.put("inparam",inparams);
			map.put("outparam",outparams);
			map.put("return_type", return_type);
			map.put("description", des);
			result.add(map);
		}
		
		//System.out.println(new DoAction().parseJSON(result));
		return result;
	}

	
	
	private static String getParamsFromElement(Element element)
	{
		String result = "";
		try{
		if(element.element("complexType").element("sequence")!=null){
			List<Element> elelist = element.element("complexType").element("sequence").elements("element"); 
			if(elelist.size()>1){
				String s ="";
				for( int i=0; i< (elelist.size()-1);i++){
					s += elelist.get(i).attributeValue("name")+"|";
				}
                result = s + elelist.get(elelist.size()-1).attributeValue("name");
			}else{
				Element ele = elelist.get(0);
		        result = ele.attributeValue("name") ;
			}
			
		}else{
			 result = "无";
		}
		
		
		
		}catch (Exception e) {
			 e.printStackTrace();
		}
		
		return result ;
	}
	
	
	
	
	
	private static String getReturntypeFromElement(Element element)
	{
		String result ="";
		try{
			if(element.element("complexType").element("sequence")!=null){
				String type = element.element("complexType").element("sequence").element("element").attributeValue("type");
				if(type==null || type.equals("")){
					result = "无";
				}else{
				result = type.replaceAll("tns:", "").replaceAll("xs:", "").replace("s:", "");
				}
			}else{
				 result = "无";
			}	
		}
		catch (Exception e) {
			 e.printStackTrace();
		}
		return result;
	}
	
	public static String getNamespace(String wsdl){
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(wsdl);
			Element root = document.getRootElement();
			return root.attributeValue("targetNamespace");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) throws DocumentException {
		getNamespace("http://192.168.100.4:8080/shanhong_ws/services/wsReport?wsdl");
		//WsdlUtil.parserWsdl("http://192.168.100.4:8080/shanhong_ws/services/wsReport?wsdl");
		System.exit(0);
	}
	
}
