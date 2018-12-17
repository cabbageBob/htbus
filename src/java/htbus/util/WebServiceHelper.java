package htbus.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nealmiao on 2016/7/27.
 */
public class WebServiceHelper {

    private String url;
    private String namespace;

    public WebServiceHelper(String url) {
        this.url = url;
        this.namespace = WsdlUtil.getNamespace(url+"?wsdl");
    }

    public static WebServiceHelper getInstance(String service) {
        return new WebServiceHelper(service);
    }

    public OMElement getOMMethod(String methodStr, String namespace, String tns, Map<String, String> params) {
        // 有抽象OM工厂获取OM工厂，创建request SOAP包
        OMFactory fac = OMAbstractFactory.getOMFactory();
        // 创建命名空间
        OMNamespace nms = fac.createOMNamespace(namespace, null == tns ? "" : tns);
        // 创建OMElement方法 元素，并指定其在nms指代的名称空间中
        OMElement method = fac.createOMElement(methodStr, nms);
        // 添加方法参数名和参数值
        if (null != params) {
            Iterator<String> keyIter = params.keySet().iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                // 创建方法参数OMElement元素
                OMElement param = fac.createOMElement(key, nms);
                // 设置键值对 参数值
                param.setText(params.get(key));
                // 讲方法元素 添加到method方法元素中
                method.addChild(param);
            }
        }
        return method;
    }

    public Options getClientOptions(String action) {
        // 端点引用 指接口位置
        EndpointReference targetEpr = new EndpointReference(url);
        // 创建request soap包 请求选项
        Options options = new Options();
        // 设置options的soapAction
        options.setAction(action);
        // 设置request soap包的端点引用(接口地址)
        options.setTo(targetEpr);
        // 如果报错提示Content-Length，请求内容长度
        options.setProperty(HTTPConstants.CHUNKED, "false");// 把chunk关掉后，会自动加上Content-Length。
        options.setTimeOutInMilliSeconds(100000);
        return options;
    }

    public OMElement getResult(String action, String methodStr, String namespace, String tns,
                               Map<String, String> params) {
        OMElement result = null;
        try {
            ServiceClient client = new ServiceClient();
            client.setOptions(getClientOptions(action));
            result = client.sendReceive(getOMMethod(methodStr, namespace, tns, params));
        } catch (AxisFault e) {
            e.printStackTrace();
        }
        return result;
    }

    public OMElement getResult(String action, String methodStr, String namespace, String tns) {
        return getResult(action, methodStr, namespace, tns, null);
    }

    public String getResultByMethod(String method) {
        return getResultByMethod(method, null);
    }

    public String getResultByMethod(String method, Map<String, String> params) {
        String action = (0 == namespace.length() ? "/" : namespace + "/")  + method;
        String tns = "";
        OMElement result = null;
        if (null == params) {
            result = getResult(action, method, namespace, tns);
        } else {
            result = getResult(action, method, namespace, tns, params);
        }
        return result.toString();
    }

    public OMElement getOMElementByMethod(String method, String namespace) {
        return getOMElementByMethod(method, namespace, null);
    }

    public OMElement getOMElementByMethod(String method, String namespace, Map<String, String> params) {
        String action = (0 == namespace.length() ? "/" : namespace) + method; //namespace + method; //TODO 注意修改,以后封装，根据wsdlsoapaction里面
        String tns = "";
        OMElement result = null;
        if (null == params) {
            result = getResult(action, method, namespace, tns);
        } else {
            result = getResult(action, method, namespace, tns, params);
        }
        return result;
    }

    public static void main(String[] args) {
        long st = System.currentTimeMillis();
        Map<String, String> params = new HashMap<>();
        //params.put("code", "0003");
        //params.put("list", "true");
        //params.put("loop", "true");
        params.put("UserAccount", "admin");
        //String s = WebServiceHelper.getInstance("http://115.236.68.206/dingtalk/profile.asmx").getResultByMethod("GetOU",  params);
        String s = WebServiceHelper.getInstance("http://115.236.68.206/zjriver/gateway/static/zjriverbpm.asmx").getResultByMethod("GetAppList",  params);
        
//        params.put("ty", "ir1");
//        String cloud = WebServiceHelper.getInstance("http://bak.qx121.com/chengg/chengg.asmx").getResultByMethod("cloud",  params);
        params.put("Path", "");
        params.put("ty", "pic");
        //String cloud = WebServiceHelper.getInstance("http://api.qgj.cn/webapi/profile.asmx").getResultByMethod("ListOrg",  params);
        //System.out.println(cloud);
        String weather = WebServiceHelper.getInstance("http://bak.qx121.com/chengg/chengg.asmx").getResultByMethod("cloud", params);
        System.out.println(weather);
        //String ws = "http://192.168.100.4:8080/shanhong_ws/services/wsReport";
        //String cloud = WebServiceHelper.getInstance(ws).getResultByMethod("check_log");
        //System.out.println(cloud);
        System.out.println("cost:" + (System.currentTimeMillis() - st));
    }
}
