package dbm.action;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.entity.Responser;
import dbm.impl.DBReorgImpl;

/**
 * @description	数据整编
 * @date 2016年11月16日下午2:49:26
 * @author lxj
 */
public class DBReorgAction extends DoAction{
	
	DBReorgImpl dbReOrgImpl = new DBReorgImpl();
	
	//获取用户分局  用户-分局对应表
	public Responser getBranchUser(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getBranchUser(params.getParam("userid"))));
		return responser;
	}
	
	//获取勘测局分局  勘测局-分局对应表
	public Responser getBranchKCJ(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getBranchKCJ(params.getParam("groupid"))));
		return responser;
	}
	
	//查询整编清单 用户-整编清单表，yyyy为空 返回该用户所有整编清单
	public Responser getReorgList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgList(
				params.getParam("userid"),params.getParam("yyyy"))));
		return responser;
	}
	
	//整编清单预览
	public Responser scanReorgList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.scanReorgList(params.getParam("gid"),params.getParam("yyyy")+"")));
		return responser;
	}
	
	//创建用户整编清单  返回boolean值
	//gid 用户下属分局编码 若 gid=ALL，则一次性创建用户所有整编清单
	public Responser createReorgList(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.createReorgList(params.getParam("userid"),
				params.getParam("gid"),params.getParam("yyyy"))+"");
		return responser;
	}
	
	//判断整编清单是否已创建，已创建返回清单编码  listid。
	//总共返回2个字段  exists：boolean值    listid：清单编码
	public Responser checkReorgList(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.checkReorgList(
				params.getParam("gid"),params.getParam("yyyy"))));
		return responser;
	}

	//删除整编清单  返回boolean值
	public Responser deleteReorgList(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.deleteReorgList(params.getParam("listid"))+"");
		return responser;
	}
	
	//批复整编清单  返回boolean值
	public Responser updateReorgResponse(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateReorgResponse(params.getParam("listid"),
				params.getParam("resp"),params.getParam("respdes"))+"");
		return responser;
	}
	
	//下发整编清单  返回boolean值
	public Responser updateReorgIssue(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateReorgIssue(params.getParam("listid"),
				params.getParam("issue"),params.getParam("issuedes"))+"");
		return responser;
	}
	
	//获取整编清单资料概况
	public Responser getReorgSumInfo(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgSumInfo(params.getParam("listid"))));
		return responser;
	}
	
	//获取整编清单资料详情 
	public Responser getReorgDetailInfo(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgDetailInfo(params.getParam("listid"),
				params.getParam("tbid"))));
		return responser;
	}
	
	//产生整编资料报告
	public Responser createReorgReport(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.createReorgReport(params.getParam("listid"))));
		/*responser.setRtString(dbReOrgImpl.createReorgReport(params.getParam("listid"),
				params.getParam("yyyy"),params.getParam("dbid"))+"");*/
		return responser;
	}

	//获取测站信息
	public Responser getStation(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getStationByGid(params.getParam("groupid"),params.getParam("yyyy"))));
		return responser;
	}
	
	//测站管理，修改测站信息
	public Responser updateStation(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateStation(params.getParam("stcd"),
				params.getParam("groupid"),params.getParam("stnm"),
				params.getParam("stct"),params.getParam("obitmcd"),params.getParam("yyyy"))+"");
		return responser;
	}
	
	//测站管理，新增测站信息
	public Responser addStation(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateStation(params.getParam("stcd"),
				params.getParam("groupid"),params.getParam("stnm"),
				params.getParam("stct"),params.getParam("obitmcd"),params.getParam("yyyy"))+"");
		return responser;
	}
	
	//测站管理，删除测站信息
	public Responser deleteStation(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.deleteStation(params.getParam("stcd"),
				params.getParam("yyyy"),params.getParam("groupid"))+"");
		return responser;
	}
	
	//数据预览
	public Responser scanData(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.scanData(params.getParam("dbid"),
				params.getParam("sql"))));
		return responser;
	}
	
	//填写数据不完整性说明
	public Responser updateReprgListDes(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateReprgListDes(params.getParam("listid")
				,params.getParam("listdes"))+"");
		return responser;
	}
	
	//数据入库  返回 result（成功true\失败false） \ msg 两个字段
	public Responser importDB(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.importDB(params.getParam("listid"),
				params.getParam("tbid"))));
		return responser;
	}
	
	//获取计划内详情
	public Responser getReorgDetailInInfo(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgDetailInInfo(params.getParam("listid"),
				params.getParam("tbid"))));
		return responser;
	}
	
	//获取计划外详情
	public Responser getReorgDetailOutInfo(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgDetailOutInfo(params.getParam("listid"),
				params.getParam("stcd"))));
		return responser;
	}

	//收录、取消收录（计划内）
	public Responser updateDetailInIsimp(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateDetailInIsimp(params.getParam("listid"),
				params.getParam("stcd"),params.getParam("isimp"))+"");
		return responser;
	}
	
	//收录、取消收录（计划外）
	public Responser updateDetailOutIsimp(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateDetailOutIsimp(params.getParam("listid"),
				params.getParam("stcd"),params.getParam("isimp"))+"");
		return responser;
	}
	
	//获取计划外测站列表
	public Responser getReorgDetailOutStaInfo(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getReorgDetailOutStaInfo(params.getParam("listid"))));
		return responser;
	}
	
	//获取施测项目列表
	public Responser getObitmcdlist(){
		responser.setRtType(JSON);
		responser.setRtString(parseJSON(dbReOrgImpl.getObitmcdlist()));
		return responser;
	}
	
	//新增施测项目
	public Responser addObitmcd(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.addObitmcd(params.getParam("id"),
				params.getParam("value"),params.getParam("name"))+"");
		return responser;
	}
	
	//修改施测项目
	public Responser updateObitmcd(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.updateObitmcd(params.getParam("id"),
				params.getParam("value"),params.getParam("name"))+"");
		return responser;
	}
	
	//删除施测项目
	public Responser delObitmcd(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.delObitmcd(params.getParam("id"),
				params.getParam("value"),params.getParam("name"))+"");
		return responser;
	}
	//把某年的测站列表复制到另一年
	public Responser copyStationToYear(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.copyStationToYear(params.getParam("gid"),
				params.getParam("year1"),params.getParam("year2"))+"");
		return responser;
	}
	//删除整编任务
	public Responser delReorgTask(){
		responser.setRtType(JSON);
		responser.setRtString(dbReOrgImpl.delReorgTask(params.getParam("listid"))+"");
		return responser;
	}
	public static void main(String[] args) {
		System.out.println(true+"");
		new DoAction().parseJSON(true+"");
	}
}
