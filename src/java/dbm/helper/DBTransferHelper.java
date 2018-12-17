package dbm.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.miao.framework.dao.BaseDao;

public class DBTransferHelper {
	
	public Map<String, Object> getTaskDetail(String taskid){
		List<Map<String, Object>> dbList = DBTransferUtil.getDataFromDbTrans(
				"{call getTaskDetail(?)}",new Object[]{taskid});
		if( null==dbList || dbList.size()==0 )
			return null;
		return dbList.get(0);
	}

	/**
	 * 默认高级（表、默认字段）设置
	 * 获取目标数据库类型、连接上后经查询获取表中字段类型
	 */
	public Map<String, String> setUpDefaultTB(String taskid,String sdbid, String tdbid, String tbnames) {
		Map<String, String> rtMap = new HashMap<String, String>();
		String srcTBID = "";
		String tarTBID = "";
		try {
			String [] tbnamestr = tbnames.split(",");
			for( String tbnm:tbnamestr ){
				//setUpSrcTb  源表、字段设置
				int recordcnt = DBTransferUtil.getTbRecordCnt(sdbid,tbnm);
				List<Map<String, Object>> tbFieldTypeList = executeDBsql(sdbid,4,tbnm);
				
				String fieldnames = formatListValue(tbFieldTypeList, "fieldname") ;
				String fieldtypes = formatListValue(tbFieldTypeList, "fieldtype") ;
				
				List<Map<String, Object>> stblist = DBTransferUtil.getDataFromDbTrans("{call setUpSrcTB(?,?,?,?,?,?,?,?,?,?)}",
						new Object[]{sdbid ,taskid,tbnm,recordcnt,recordcnt,fieldnames,fieldtypes,fieldnames,fieldtypes,""});
				//setUpTarTb  目标表、字段设置
				List<Map<String, Object>> ttblist = DBTransferUtil.getDataFromDbTrans("{call setUpTarTB(?,?,?,?,?,?,?,?,?,?)}",
						new Object[]{tdbid ,taskid,tbnm,recordcnt,recordcnt,fieldnames,fieldtypes,fieldnames,fieldtypes,""});
			
				//绑定源表、目标表
				if( null!=stblist && stblist.size()>0 && null!=ttblist && ttblist.size()>0 ){
					String stbid = stblist.get(0).get("source_tbid").toString();
					String ttbid = ttblist.get(0).get("target_tbid").toString();
					List<Map<String, Object>> srctarTbList = DBTransferUtil.getDataFromDbTrans("{call bindSrcTarTB(?,?,?)}", 
							new Object[]{taskid,stbid,ttbid});
					//拼装srcTBID、tarTBID
					srcTBID = (srcTBID.length()==0)?stbid:(","+stbid);
					tarTBID = (tarTBID.length()==0)?ttbid:(","+ttbid);
					
					//设置默认字段
					setUpDefaultField(taskid,sdbid,tdbid,stbid,ttbid,tbFieldTypeList);				
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		rtMap.put("source_tbids", srcTBID);rtMap.put("target_tbids", tarTBID);
		return rtMap;
	}

	/**
	 * @description 设置默认字段对应关系
	 * @date 2016年11月14日上午11:10:49
	 * @author lxj
	 * @param taskid
	 * @param sdbid
	 * @param tdbid
	 * @param stbid
	 * @param ttbid
	 * @param tbFieldTypeList
	 */
	private Map<String, String> setUpDefaultField(String taskid, String sdbid, String tdbid, String stbid, String ttbid,
			List<Map<String, Object>> tbFieldTypeList) {
		Map<String, String> rtMap = new HashMap<String, String>();
		String srcFID = "";
		String tarFID = "";
		for( Map<String, Object> map:tbFieldTypeList ){
			String sfname = map.get("fieldname").toString() ;
			String sftype = map.get("fieldtype").toString();
			int sflen = (null==map.get("fieldlength"))?0:Integer.parseInt(map.get("fieldlength").toString());
			String sfcom = map.get("fieldcomment").toString();
			String sfnull = map.get("fieldnullable").toString();
			int sfpre = (null==map.get("fieldprecision"))?0:Integer.parseInt(map.get("fieldprecision").toString());
			int sfscale = (null==map.get("fieldscale"))?0:Integer.parseInt(map.get("fieldscale").toString());
			String siskey = map.get("iskey").toString();
			//Map<String, Object> sftypeMap = getDefaultFiled(sftype,sdbid,tdbid);//字段设置
			String sfflname = createFFullName(sdbid,sdbid,sfname,sftype,sflen,sfpre,sfscale,sfcom,sfnull);
			//sftype = sfflname.split(" ")[1];
			//设置源字段
			List<Map<String, Object>> sflist = DBTransferUtil.getDataFromDbTrans("{call setUpSrcField(?,?,?,?,?,?,?,?,?,?,?,?)}",
					new Object[]{taskid,sdbid,stbid,sfname,sfflname,sftype,sflen,sfpre,sfscale,sfnull,siskey,sfcom});
			
			//设置目标字段
			//String tftype = sftypeMap.get("field_type_default").toString().split(",")[0];//获取目标字段类型
			//Map<String, Object> tftypeMap = getDefaultFiled(tftype,tdbid,sdbid);//字段设置
			String tfflname = createFFullName(sdbid,tdbid,sfname,sftype,sflen,sfpre,sfscale,sfcom,sfnull);
			String tftype = tfflname.split(" ")[1];
			List<Map<String, Object>> tflist = DBTransferUtil.getDataFromDbTrans("{call setUpTarField(?,?,?,?,?,?,?,?,?,?,?,?)}",
					new Object[]{taskid,tdbid,ttbid,sfname,tfflname,tftype,sflen,sfpre,sfscale,sfnull,siskey,sfcom});
			//绑定源表、目标字段
			if( null!=sflist && sflist.size()>0 && null!=tflist && tflist.size()>0 ){
				String sfid = sflist.get(0).get("source_field_id").toString();
				String tfid = tflist.get(0).get("target_field_id").toString();
				List<Map<String, Object>> srctarTbList = DBTransferUtil.getDataFromDbTrans("{call bindSrcTarField(?,?,?)}", 
						new Object[]{taskid,sfid,tfid});
				//拼装srcFID、tarFID
				srcFID += (srcFID.length()==0)?sfid:(","+sfid);
				tarFID += (tarFID.length()==0)?tfid:(","+tfid);			
			}
			
		}
		rtMap.put("source_field_ids", srcFID);rtMap.put("target_field_ids", tarFID);
		return rtMap;
	}

	/**
	 * @description 获取数据库中所有表、视图、存储过程、某表字段
	 * @date 2016年11月11日上午11:23:46
	 * @author lxj
	 * @param dbid
	 * @param type 1 所有表   2所有视图  3所有存储过程  4某表中所有字段
	 * @param tbnm  type=4 时，才需要传此参数
	 * @return
	 */
	public List<Map<String, Object>> executeDBsql(String dbid,int type, String tbnm) {
		List<Map<String, Object>> rtList = new ArrayList<Map<String,Object>>();
		String sqlstament = "";
		switch( type ){
			case 1:
				sqlstament = "tb_statement";break;
			case 2:
				sqlstament = "view_statement";break;
			case 3:
				sqlstament = "pro_statement";break;
			case 4:
				sqlstament = "field_statement";break;
		}
		try {
			Map<String, Object> dbsmap = DBTransferUtil.getDaoMapByDbid(dbid);
			String fieldsql = dbsmap.get(sqlstament).toString()
						.replace("dbname", dbsmap.get("dbname").toString())
						.replace("tbname", (null==tbnm)?"tbname":tbnm);
			BaseDao daosdb = DBTransferUtil.getDaoByDbid(dbid);
			//String sql = "select * from sysobjects where name='HT_GATE'";
			rtList = daosdb.executeQuery(fieldsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}

	/**
	 * 逗号分隔 List中key对应的 value值,顺序
	 */
	public String formatListValue(List<Map<String, Object>> list,String key){
		String result = "";
		if( null==list || list.size()==0 || key.length()==0 ) return result;
		try {
			for( int i=0,len=list.size();i<len;i++ ){
				Map<String, Object> map = list.get(i);
				result += (result.length()==0)?(null==map.get(key))?"":map.get(key):","+ ((null==map.get(key))?"":map.get(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//typeMap中存放字段设置
	//由于sqlserver 备注复杂，字段备注暂时不考虑
	//字段自增暂不考虑 mssql: f2 int identity(1,1) not null;   mysql: f4 smallint(255) not null auto_increment comment '主键1',
	//mysql information_schema.columns 表中有一个字段 COLUMN_TYPE 存储了 字段类型详细(如:varchar(32))
	//20161115 这个方法应该把 sdbid、tdbid 、sftype、tftype 都带入进来，然后进行转换
	public String createFFullName(String sdbid,String tdbid,String fname,String sftype,int flen,int fprec,
			int fscal,String fcom,String isnull){
		int sdbtypeid = DBTransferUtil.getDbTypeByDbid(sdbid);
		int tdbtypeid = DBTransferUtil.getDbTypeByDbid(tdbid);
		
		String tftype = DBTransferUtil.getTarFiled(sdbtypeid,tdbtypeid,
				sftype,flen,fprec,fscal);//获取目标字段字段类型、字段长度属性
		
		return DBTransferUtil.createFFullName(tdbtypeid,fname,tftype,fcom,isnull);
	}
	
	public Map<String, Object> runTask(String taskid) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("task_status", "");rtMap.put("task_status_des", "");
		//获取任务详情
		Map<String, Object> taskdetailMap =  getTaskDetail(taskid);
		if( null==taskdetailMap ) return rtMap;
		try {
			if( taskdetailMap.get("task_status").toString().equals("-1") ){//未开始执行的任务才能开始执行，否则不能执行
				//记录任务开始执行时间
				boolean updateTaskTm = DBTransferUtil.executeSqlByDbTrans("{call updateTaskTm(?,?)}",new Object[]{taskid,1});
				//更新任务状态
				boolean updateTaskStatus = DBTransferUtil.executeSqlByDbTrans("{call updateTaskStatus(?,?)}",new Object[]{taskid,"2"});
				if( updateTaskTm && updateTaskStatus ){
					//String taskid = taskdetailMap.get("").toString();
					String sdbid = taskdetailMap.get("source_dbid").toString();
					String tdbid = taskdetailMap.get("target_dbid").toString();
					String ruleidcreate = taskdetailMap.get("ruleid_create").toString();
				    String ruleidinsert = taskdetailMap.get("ruleid_insert").toString();
					String ruleiddrror = taskdetailMap.get("ruleid_error").toString();
					
					BaseDao sdbdao = DBTransferUtil.getDaoByDbid(sdbid);//源库
					BaseDao tdbdao = DBTransferUtil.getDaoByDbid(tdbid);//目标库
					
					//先不管那么多，在目标表已经建立情况下直接导入数据
					//20161114 新增 创建表结构
					List<Map<String, Object>> stbList = DBTransferUtil.getDataFromDbTrans(
							"{call getSrcTB(?,?)}",new Object[]{taskid,sdbid});
					List<Map<String, Object>> ttbList = DBTransferUtil.getDataFromDbTrans(
							"{call getTarTB(?,?)}",new Object[]{taskid,tdbid});
		
					for( int i=0,stblistlen=stbList.size();i<stblistlen;i++ ){
						Map<String, Object> stbMap = stbList.get(i);
						String stbname = stbMap.get("source_tb").toString();
						String stbid = stbMap.get("source_tbid").toString();
						String sttbid = stbMap.get("source_target_tbid").toString();
		
						for( int j=0,ttblistlen=ttbList.size();j<ttblistlen;j++ ){
							Map<String, Object> ttbMap = ttbList.get(j);
							if( ttbMap.get("target_tbid").toString().equals(sttbid) ){//sttbid=ttbid
								String ttbname = ttbMap.get("target_tb").toString();
		
								List<Map<String, Object>> stbFieldList = DBTransferUtil.getDataFromDbTrans(
										"{call getSrcField(?,?,?)}",new Object[]{taskid,sdbid,stbid});
								List<Map<String, Object>> ttbFieldList = DBTransferUtil.getDataFromDbTrans(
										"{call getTarField(?,?,?)}",new Object[]{taskid,tdbid,sttbid});
								
								Map<String,String> sqlmap = createInsertStr(tdbid,ttbname,stbFieldList,ttbFieldList);
								String sqlinsert = sqlmap.get("insertsql").toString();
								String [] srcfields = sqlmap.get("sfnmstrs").toString().split(",");
								List<Object[]> listObject = new ArrayList<Object[]>();
								DBTransferUtil.logger.info("开始获取源表："+stbname+" 记录数");
								List<Map<String, Object>> tblist = sdbdao.executeQuery("select * from "+stbname);//源表中所有记录
								for( int k=0,len1=tblist.size();k<len1;k++ ){
									Map<String, Object> map = tblist.get(k);
									Object[] objects = new Object[srcfields.length];
									for( int n=0;n<srcfields.length;n++ ){
										objects[n] = map.get(srcfields[n]);
									}
									listObject.add(objects);
								}
								DBTransferUtil.logger.info("获取源表："+stbname+"总 "+tblist.size()+" 条记录成功");
		
								String createTbStr = createTbStr(ruleidcreate,tdbid,ttbname,ttbFieldList);
								DBTransferUtil.logger.info("开始创建目标表："+ttbname+"："+createTbStr);
								//tdbdao.executeSQL(createTbStr);//创建目标表,转换为多句sql语句执行
								String [] createTbStrs = createTbStr.split(";");
								for( int k=0,cTbSlen=createTbStrs.length;k<cTbSlen;k++ ){
									String createsql = createTbStrs[k];
									if( null!=createsql && createsql.length()>0 ){
										tdbdao.executeSQL(createsql);
									}
								}
								DBTransferUtil.logger.info("创建目标表："+ttbname+" 成功");
								
								DBTransferUtil.logger.info("开始数据插入...");
								tdbdao.executeSQLBatch(sqlinsert, listObject);//记录导入目标表
								DBTransferUtil.logger.info("插入目标表："+ttbname+" 总记录数："+tblist.size()+" 条记录成功");
								
								break;
							}
						}
					}
					
					/*
						//根据详情中源库配置获取源库中表、视图、函数
						
						
						//根据详情中遇错处理方式、插入方式，对每张表进行数据插入
						 
						*/
					//记录任务开始执行时间
					boolean updateTaskTm2 = DBTransferUtil.executeSqlByDbTrans("{call updateTaskTm(?,?)}",new Object[]{taskid,0});
					//更新任务状态，成功
					boolean updateTaskStatus2 = DBTransferUtil.executeSqlByDbTrans("{call updateTaskStatus(?,?)}",new Object[]{taskid,"1"});
				}
			}
		} catch (Exception e) {
			try {//更新任务状态,失败
				boolean updateTaskStatus2 = DBTransferUtil.executeSqlByDbTrans("{call updateTaskStatus(?,?)}",new Object[]{taskid,"0"});
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		taskdetailMap = getTaskDetail(taskid);
		rtMap.put("task_status", taskdetailMap.get("task_status"));
		rtMap.put("task_status_des", taskdetailMap.get("task_status_des"));
		return rtMap;
	}

	private Map<String, String> createInsertStr(String tdbid, String ttbname,
			List<Map<String, Object>> stbFieldList,List<Map<String, Object>> ttbFieldList) {
		Map<String, String> rtMap = new HashMap<String,String>();
		String sql1 = "insert into ";
		String sql2 = "(";
		String sql3 = ")values(";
		String sql4 = ")";
		String commas="",tfnmstrs = "",sfnmstrs="";
		int dbtypdid = DBTransferUtil.getDbTypeByDbid(tdbid);
		for( int k=0,tflistlen=ttbFieldList.size();k<tflistlen;k++ ){
			Map<String, Object> ttbfMap = ttbFieldList.get(k);
			if( dbtypdid==2 ){
				tfnmstrs += (tfnmstrs.length()==0)?"\""+ttbfMap.get("field_name")+"\""
						:",\""+ttbfMap.get("field_name")+"\"";
			}else{
				tfnmstrs += (tfnmstrs.length()==0)?ttbfMap.get("field_name")
						:","+ttbfMap.get("field_name");
			}
			commas += (commas.length()==0)?"?":",?";
			for( Map<String, Object> stbfMap:stbFieldList ){
				if( ttbfMap.get("target_field_id").equals(stbfMap.get("source_target_field_id")) ){
					sfnmstrs += (sfnmstrs.length()==0)?stbfMap.get("field_name"):","+stbfMap.get("field_name");
					break;
				}
			}
		}
		rtMap.put("insertsql", sql1 + ((dbtypdid==2)?"\""+ttbname+"\"":ttbname) + sql2 + tfnmstrs + sql3 + commas + sql4);
		rtMap.put("sfnmstrs", sfnmstrs);
		return rtMap;
	}

	/**
	 * @description 表语句创建,根据详情中创建方式、目标库配置创建目标库中表、视图、函数
	 * @date 2016年11月14日下午10:22:08
	 * @author lxj
	 * @param ruleidcreate
	 * @param ttbname
	 * @param ttbFieldList
	 * @return
	 */
	private String createTbStr(String ruleidcreate,String tdbid, String ttbname, List<Map<String, Object>> ttbFieldList) {
		String dropstrs="",createstrs="",alterstrs="";
		int dbtype = DBTransferUtil.getDbTypeByDbid(tdbid);

		if( dbtype==2 ){
			ttbname = "\""+ttbname+"\"";
		}
		
		if( ruleidcreate.equals("6") ){//创建对象前不删除对象
			dropstrs = "";
		}else if( ruleidcreate.equals("5") ){//创建对象前删除对象
			switch(dbtype){
				case 1:dropstrs = "if exists(select * from sysobjects where type='U' and name='tbname') begin drop table tbname end;";
					   dropstrs = dropstrs.replace("tbname", ttbname);break;
				case 2:dropstrs = "drop table " + ttbname + ";";break;//20161114 oracle 待增加删除表之前的判断
				case 3:dropstrs = "drop table if exists " + ttbname + ";";break;
			}
		}
		
		String tfstrs = "",ktfstrs="";
		for( Map<String, Object> tfMap:ttbFieldList ){
			tfstrs += (tfstrs.length()==0)?tfMap.get("field_fullname"):","+tfMap.get("field_fullname");
			if( tfMap.get("field_iskey").equals("1") ){
				if( dbtype==2 ){
					ktfstrs += (ktfstrs.length()==0)?"\""+tfMap.get("field_name")+"\"":",\""+tfMap.get("field_name")+"\"";
				}else{
					ktfstrs += (ktfstrs.length()==0)?tfMap.get("field_name"):","+tfMap.get("field_name");
				}
			}
		}
		createstrs = "create table "+ttbname+"("+tfstrs+");";
		
		if( ktfstrs.length()>0 ){
			alterstrs = "alter table "+ttbname+ " add primary key("+ktfstrs+");";
		}

		return dropstrs + createstrs + alterstrs;
	}
}
