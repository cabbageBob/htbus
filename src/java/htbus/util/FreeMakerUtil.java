package htbus.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMakerUtil {

	public static void createDoc(String template_filename,Map<String,Object> dataMap,String output_filename) throws IOException {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_25);
		configuration.setDefaultEncoding("utf-8");
		//dataMap 要填入模本的数据文件
		//设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
		//这里我们的模板是放在template包下面
		configuration.setDirectoryForTemplateLoading(new File(ConfigUtil.config("freemaker_tpl")));
		Template t=null;
		try {
			//test.ftl为要装载的模板
			t = configuration.getTemplate(template_filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//输出文档路径及名称
		File outFile = new File(ConfigUtil.config("freemaker_output") + output_filename);
		Writer out = null;
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(outFile);
			OutputStreamWriter oWriter = new OutputStreamWriter(fos,"UTF-8");
			//这个地方对流的编码不可或缺，使用main（）单独调用时，应该可以，但是如果是web请求导出时导出后word文档就会打不开，并且包XML文件错误。主要是编码格式不正确，无法解析。
			//out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
			 out = new BufferedWriter(oWriter); 
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		 
        try {
			t.process(dataMap, out);
			out.close();
			fos.close();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //System.out.println("---------------------------");
	}
	
}
