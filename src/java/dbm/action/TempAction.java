package dbm.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.DataSource;

public class TempAction extends DoAction {
	public Responser getTask(){
		String sql = "select a.*,b.gname user from update_task a left join sys_group b on a.groupid=b.gid";
		List<Map<String, Object>> rtlist;
		try {
			rtlist = DataSource.sysdao().executeQuery(sql);
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtlist));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
	
	public Responser getSubTask(){
		String sql = "select * from update_file a WHERE taskcode='20160101'";
		List<Map<String, Object>> rtlist;
		try {
			rtlist = DataSource.sysdao().executeQuery(sql);
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtlist));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
	
	public Responser addTask(){//新建水文整编任务：添加三行记录到update_task   添加几行记录到update_file
		String sql = "insert into update_task(taskcode,createtm,taskname,groupid,status"
				+ " ) values (?,?,?,?,?)";
		try {
		DataSource.sysdao().executeSQL(sql,new Object[]{"20160101","2016-10-21 09:27:35","长委水文局2016年度水文整编任务",
				"1","未提交"});
		DataSource.sysdao().executeSQL(sql,new Object[]{"20160102","2016-10-21 09:27:35","长委水文局2016年度水文整编任务",
				"2","未提交"});
			DataSource.sysdao().executeSQL(sql,new Object[]{"20160103","2016-10-21 09:27:35","长委水文局2016年度水文整编任务",
					"3","未提交"});
		//
		String sql2 = "insert into update_file(taskcode,filename,isup"
				+ " ) values (?,?,?)";
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","小港站-年水位整编","未提交"});
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","西横河上站-年水位整编","未提交"});
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","低塘上站-年水位整编","未提交"});
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","柴桥站-年水位整编","未提交"});
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","符家汇站-年水位整编","未提交"});
		DataSource.sysdao().executeSQL(sql2,new Object[]{"20160101","江东内河站-年水位整编","未提交"});
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
	
	public Responser receiveFile(){//数据接收： update_task标记为  已接收,待提交质量保证书
		String sql = "UPDATE update_task set status=? WHERE taskcode=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{"已接收,待提交质量保证书","20160101"});
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	} 
	public Responser inDatabase(){//数据入库： update_task标记为  已入库
		String sql = "UPDATE update_task set status=? WHERE taskcode=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{"已入库","20160101"});
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	} 
	public Responser done(){//批复完成： update_task标记为  已完成
		String sql = "UPDATE update_task set status=? WHERE taskcode=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{"已完成","20160101"});
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	} 
	
	
	//=============================== 以上是中心用户相关接口，以下是分局用户相关接口
	
	public Responser getsTask(){
		String sql = "select * from update_task where taskcode='20160101'";
		List<Map<String, Object>> rtlist;
		try {
			rtlist = DataSource.sysdao().executeQuery(sql);
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtlist));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
	public Responser uploadDataFile(){//提交数据文件：update_file标记为  已提交   update_task标记为已提交,待接收
		String sql = "UPDATE update_task set status=? WHERE taskcode=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{"已提交，待接收","20160101"});
		String sql2 = "UPDATE update_file set isup='已提交'  WHERE taskcode='20160101' ";
		DataSource.sysdao().executeSQL(sql2);
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
	public Responser uploadPromiseFile(){//提交质量保证书： update_task标记为  已提交保证书,待批复
		String sql = "UPDATE update_task set status=?,promise=? WHERE taskcode=?";
		try {
			DataSource.sysdao().executeSQL(sql,new Object[]{"已提交保证书,待批复","20160101保证书.pdf","20160101"});
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("result", "success");
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(rtMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responser;
	}
}
