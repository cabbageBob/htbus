package dbm.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.miao.framework.action.DoAction;
import cn.miao.framework.dao.BaseDao;
import cn.miao.framework.factory.DaoFactory;
import cn.miao.framework.util.DateUtil;
import dbm.helper.DBReorgHelper;
import dbm.helper.DBTransferUtil;

/**
 * @description  数据整编
 * @date 2016年11月16日下午2:48:32
 * @author lxj
 */
public class DBReorgImpl {

	public static void main(String[] args) throws Exception {
		/*List<Object[]> listo = new ArrayList<>();
		for(int i=0;i<5;i++){
			listo.add(new Object[]{i,"1","2016","test","1","1","1"});
		}
		BaseDao daoDbmReorg = DaoFactory.getDao("dbmreorganize");
		daoDbmReorg.executeSQL("{call insertResultSum(?,?,?,?,?,?,?)}",
				new Object[]{7,"1","2016","test","1","1","1"});
		daoDbmReorg.executeSQLBatch("{call insertResultSum(?,?,?,?,?,?,?)}", listo);*/
		
		//new DBReorgImpl().updateReorgResponse("1","1","批复通过，没问题");
		
		//System.out.println(new DoAction().parseJSON(new DBReorgImpl().scanReorgList("010300","2015")));
		
		//new DBReorgImpl().createReorgList("d041f90a-99c6-11e6-a17e-005056931899","010300","2015");

		new DBReorgImpl().createReorgReport("8");
		
		//new DBReorgImpl().importDB("7","15");
		
		System.exit(0);
	}

	public List<Map<String, Object>> getBranchUser(String userid) {
		List<Map<String, Object>> rtList = null;
		try {
			rtList = DBTransferUtil.getDataFromDbm(
					"{call getBranchUser(?)}",new Object[]{userid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}

	public List<Map<String, Object>> getBranchKCJ(String groupid) {
		List<Map<String, Object>> rtList = null;
		try {
			rtList = DBTransferUtil.getDataFromDbm(
					"{call getBranchKCJ(?)}",new Object[]{groupid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}

	public List<Map<String, Object>> getReorgList(String userid,String yyyy) {
		return DBReorgHelper.callProByReorg(
				"{call getReorgList(?,?)}",new Object[]{userid,yyyy});
	}

	/**
	 * @description 预览整编清单
	 * @date 2016年12月7日下午2:19:16
	 * @author lxj
	 * @param gid
	 * @param yyyy
	 * @return
	 */
	public List<Map<String, Object>> scanReorgList(String gid, String yyyy) {
		List<Map<String, Object>> stList = getStationByGid(gid,yyyy);//组织中拥有的测站数目
		List<Map<String, Object>> sumOList = new ArrayList<>();
		for( Map<String, Object> cTbMap:DBReorgHelper.checkTbList ){
			String tbid = cTbMap.get("tbid").toString();
			String tbnm = cTbMap.get("tbnm").toString();
			String tbdes = cTbMap.get("tbdes").toString();
			String obitmcd = cTbMap.get("obitmcd").toString();
			String tbtype = cTbMap.get("tbtype").toString().toUpperCase();
			int tarstcnt = 0,tarrcdcnt = 0,sdscnt=0;
			int rulercdcnt = DBReorgHelper.getRuleRcdCnt(tbtype);//日、旬、月、年每个站点必须有的记录数目
			List<Map<String, Object>> tstlist =getTarStCnt(obitmcd,stList);
			tarstcnt = tstlist.size();
			if( tbnm.equals("HY_STSC_A") ){
				tarrcdcnt = 1*tarstcnt;
			}
			else if(tbnm.equals("HY_MTPDDB_E")||tbnm.equals("HY_YRPDDB_F")){
				tarrcdcnt = 0;
			}
			else if(obitmcd.contains("A")||obitmcd.contains("C")||obitmcd.contains("U")||obitmcd.contains("V")
					||obitmcd.contains("F")||obitmcd.contains("J")) {		
				for(Map<String, Object> tstmap: tstlist){
					sdscnt = sdsCnt(tstmap.get("obitmcd").toString(),obitmcd);
					tarrcdcnt += rulercdcnt*sdscnt;
				}
				}
			else{
				tarrcdcnt = rulercdcnt*tarstcnt;
			}
			Map<String, Object> tmpmap = new HashMap<>();
			tmpmap.put("tbid", tbid);
			//tmpmap.put("listid", listid);
			tmpmap.put("yyyy", yyyy);
			tmpmap.put("tbnm", tbnm);
			tmpmap.put("tbdes", tbdes);
			tmpmap.put("tar_stcnt", tarstcnt);
			tmpmap.put("tar_rcdcnt", tarrcdcnt);
			sumOList.add(tmpmap);
		}
		return sumOList;
	}
	
	/**
	 * @description 创建整编清单，创建完成即要形成整编清单的部分概况信息
	 * @date 2016年11月16日下午3:01:42
	 * @author lxj
	 * @param userids
	 * @param yyyy
	 * @return
	 */
	public boolean createReorgList(String userid,String gid, String yyyy) {
		boolean result = false;
		try {
			List<Map<String, Object>> rtList = DBReorgHelper.callProByReorg(
					"{call createReorgList(?,?,?)}",new Object[]{userid,gid.toUpperCase(),yyyy});
			if( null!=rtList && rtList.size()>0 ){//创建完成即要形成整编清单的部分概况信息
				for( Map<String, Object> rtMap:rtList ){
					String listid = rtMap.get("listid").toString();
					String list_to_gid = rtMap.get("list_to_group_id").toString();
					insertResultSum(listid, list_to_gid, yyyy);
				}
				result =true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String, Object> checkReorgList(String gid,String yyyy){
		Map<String, Object> rtmap = new HashMap<>();
		rtmap.put("exists", false);
		rtmap.put("listid", "");
		try {
			List<Map<String, Object>> list = DBReorgHelper.callProByReorg(
					"{call checkReorgList(?,?)}",new Object[]{gid,yyyy});
			if( null!=list && list.size()>0 ){
				rtmap.put("exists", true);
				rtmap.put("listid", list.get(0).get("listid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtmap;
	}
	
	public boolean deleteReorgList(String listid) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg(
					"{call deleteReorgList(?)}",new Object[]{listid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean insertResultSum(String listid,String list_to_gid,String yyyy){
		boolean result = false;
		try {
			List<Object[]> sumOList = new ArrayList<>();
			List<Map<String, Object>> stList = getStationByGid(list_to_gid,yyyy);//组织中拥有的测站数目
			for( Map<String, Object> cTbMap:DBReorgHelper.checkTbList ){
				String tbid = cTbMap.get("tbid").toString();
				String tbnm = cTbMap.get("tbnm").toString();
				String obitmcd = cTbMap.get("obitmcd").toString();
				String tbtype = cTbMap.get("tbtype").toString().toUpperCase();
				int tarstcnt = 0,tarrcdcnt = 0,sdscnt=0;
				int rulercdcnt = DBReorgHelper.getRuleRcdCnt(tbtype);//日、旬、月、年每个站点必须有的记录数目
				List<Map<String, Object>> tstlist = getTarStCnt(obitmcd,stList);
				tarstcnt = tstlist.size();
				if( tbnm.equals("HY_STSC_A") ){
					tarrcdcnt = 1*tarstcnt;
				}
				else if(tbnm.equals("HY_MTPDDB_E")||tbnm.equals("HY_YRPDDB_F")){
					tarrcdcnt = 0;
				}
				else if(obitmcd.contains("A")||obitmcd.contains("C")||obitmcd.contains("U")||obitmcd.contains("V")
						||obitmcd.contains("F")||obitmcd.contains("J")) {		
					for(Map<String, Object> tstmap: tstlist){
						sdscnt = sdsCnt(tstmap.get("obitmcd").toString(),obitmcd);
						tarrcdcnt += rulercdcnt*sdscnt;
					}
					}
				else{
					tarrcdcnt = rulercdcnt*tarstcnt;
				}
				sumOList.add(new Object[]{tbid,listid,yyyy,tbnm,tarstcnt,tarrcdcnt,
						null,null,null});
			}
			DBReorgHelper.executeSQLBatch("{call insertResultSum(?,?,?,?,?,?,?,?,?)}", sumOList);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<Map<String, Object>> getReorgSumInfo(String listid) {
		return DBReorgHelper.callProByReorg(
				"{call getReorgSumInfo(?)}",new Object[]{listid});
	}
	
	public List<Map<String, Object>> getReorgDetailInfo(String listid,String tbid) {
		return DBReorgHelper.callProByReorg(
				"{call getReorgDetailInfo(?,?)}",new Object[]{listid,tbid});
	}

	public Map<String, Object> createReorgReport(String listid) {
		Map<String, Object> rtMap = new HashMap<>();
		rtMap.put("result", false);rtMap.put("msg", "");
		//整编外站点数据入库
		List<Map<String, Object>> reorglist = DBReorgHelper.callProByReorg(
				"{call getReorgListByListid(?)}",new Object[]{listid});
		if( null==reorglist || reorglist.size()==0 ) {
			rtMap.put("msg", "整编分析失败，清单不存在");
			DBReorgHelper.logger.info("整编分析失败，清单不存在");
			return rtMap;
		}
		Map<String,Object> reorgMap = reorglist.get(0);
		String yyyy = reorgMap.get("list_year").toString();
		String dbid = (null==reorgMap.get("dbid"))?"":reorgMap.get("dbid").toString();
		String list_to_gid = reorgMap.get("list_to_group_id").toString();
		if( dbid.length()==0 ){
			rtMap.put("msg", "整编分析失败，数据库文件未提交");
			DBReorgHelper.logger.info("整编分析失败，数据库文件未提交");
			return rtMap;
		}
		BaseDao dbdao = DBReorgHelper.getDaoByDbid(dbid);//mssql
		if( null==dbdao ){
			rtMap.put("msg", "整编分析失败，附加数据库连接失败");
			DBReorgHelper.logger.info("整编分析失败，附加数据库连接失败");
			return rtMap;
		}
		try {
			
			DBReorgHelper.logger.info("整编 "+yyyy+" 年清单 LISTID="+listid+" 开始");
			//删除计划内、计划外清单列表
			if( deleteResultDetailInOut(listid) ){
				DBReorgHelper.logger.info("删除 "+yyyy+" 年清单 LISTID="+listid+" 计划内、计划外详情成功");
			}
			List<Map<String, Object>> mdfstList = dbdao.executeQuery(
	        		DBReorgHelper.diffStRcdCntSql.replace("tbname", "hy_stsc_a"));//mdf提交的站点列表
			//List<Map<String, Object>> sumRcdList = dbdao.executeQuery(DBReorgHelper.sumRcdSql);
			List<Object[]> sumOList = new ArrayList<>();
			//List<Map<String, Object>> stList = getStationByGid(list_to_gid,yyyy);//组织中拥有的测站数目
			for( Map<String, Object> cTbMap:DBReorgHelper.checkTbList ){
				String tbid = cTbMap.get("tbid").toString();
				String tbnm = cTbMap.get("tbnm").toString();
				String obitmcd = cTbMap.get("obitmcd").toString();
				DBReorgHelper.logger.info("表 "+tbnm+" 整编分析开始");
				String tbtype = cTbMap.get("tbtype").toString().toUpperCase();
				String tb_check_type = cTbMap.get("tb_check_type").toString();
				int diffstcnt = 0;
				int sumrcdcnt = 0;

				int rulercdcnt = DBReorgHelper.getRuleRcdCnt(tbtype);//日、旬、月、年每个站点必须有的记录数目
				int prostcnt = 0;//计划内问题站点数目
				String standard = "",detail = "";//计划内是否达标、达标详情
				Map<String, Object> detailMap = null;
				List<Map<String, Object>> stList = getStationByGid(list_to_gid, yyyy);
				detailMap = insertResultDetail(dbdao,mdfstList,listid,yyyy,tbid,tbtype,tb_check_type,
						obitmcd,tbnm,rulercdcnt,stList);
				diffstcnt = Integer.parseInt(detailMap.get("diffstcnt").toString());
				sumrcdcnt = Integer.parseInt(detailMap.get("sumrcdcnt").toString());
				prostcnt = Integer.parseInt(detailMap.get("prostcnt").toString());
				standard = detailMap.get("standard").toString();
				detail = detailMap.get("detail").toString();
				sumOList.add(new Object[]{tbid,listid,diffstcnt,prostcnt,sumrcdcnt,standard,detail});
				DBReorgHelper.logger.info("表 "+tbnm+" 整编分析结束");
			}
			DBReorgHelper.executeSQLBatch("{call updateResultSum(?,?,?,?,?,?,?)}", sumOList);

			//计划外整编存储数据库入库 
			rtMap.put("result", true);rtMap.put("msg", "整编分析 "+yyyy+" 年清单 "+listid+" 结束");
		} catch (Exception e) {
			rtMap.put("msg", "整编分析 "+yyyy+" 年清单 "+listid+" 失败："+e.toString());
			e.printStackTrace();
		}
		DBReorgHelper.logger.info("整编分析 "+yyyy+" 年清单 "+listid+" 结束");
		return rtMap;
	}

	/**
	 * @description 返回问题站点数量
	 * @date 2016年11月18日下午1:38:52
	 * @author lxj
	 * @param dbdao
	 * @param listid
	 * @param yyyy
	 * @param tbid
	 * @param tbtype
	 * @param tbnm
	 * @param rulercdcnt
	 * @return
	 */
	private Map<String, Object> insertResultDetail(BaseDao dbdao,List<Map<String, Object>>mdfstList,
			String listid, String yyyy,String tbid,String tbtype,String tb_check_type,String obitmcd,
			String tbnm,int rulercdcnt,List<Map<String, Object>> stList) {
		Map<String, Object> rtMap = new HashMap<>();
		rtMap.put("prostcnt", 0);//计划内问题站点数
		rtMap.put("standard", "1");//计划内是否达标
		rtMap.put("detail", "完全达标");//计划内达标详情
		rtMap.put("diffstcnt", "0");//计划内提交的站点总数
		rtMap.put("sumrcdcnt", "0");//计划内提交的记录总数
		int prostcnt = 0;//if(!tbnm.equals("HY_DCCS_D")) return 0;
		List<Object[]> detailOList = new ArrayList<>();
		String serial = null;//连续性
		String missdate = null;//缺失日期
		String stcd = null;//stcd
		String sdtp = null;//泥沙类型
		String zcnt = null;//水位记录数
		String qcnt = null;//流量记录数
		String scnt = null;//含沙记录数
		String lacktp = null;//缺少的泥沙类型
		String stobitmcd = null;
		int rtrcdcnt = 0;
		int lacktpcnt = 0;
		try {
			List<Map<String, Object>> diffStRcdCntList = dbdao.executeQuery(
					DBReorgHelper.diffStRcdCntSql.replace("tbname", tbnm));
			List<Map<String, Object>> instList = getTarStCnt(obitmcd, stList);//应收站点列表
			if( null!=diffStRcdCntList && !diffStRcdCntList.isEmpty() ){
				for( Map<String, Object> diffStRcdCntMap:diffStRcdCntList ){
					stcd = diffStRcdCntMap.get("stcd").toString();
					rtrcdcnt = Integer.parseInt(diffStRcdCntMap.get("strcdcnt").toString());
					if( obitmcd.equals("1")|| obitmcd.equals("0") ){//ht_stsc_a表
						prostcnt = 0;serial = DBReorgHelper.noSerial;missdate = null;
						sdtp = null;zcnt = null;qcnt = null;scnt = null;
					}else if( tb_check_type.equals("3") ){//hy_fdheex_b表
						prostcnt = 0;serial = DBReorgHelper.noSerial;missdate = null;sdtp = null;
						List<Map<String, Object>> zCntList = dbdao.executeQuery(DBReorgHelper.zCntSql.replace("tbname", tbnm));
						List<Map<String, Object>> qCntList = dbdao.executeQuery(DBReorgHelper.qCntSql.replace("tbname", tbnm));
						List<Map<String, Object>> sCntList = dbdao.executeQuery(DBReorgHelper.sCntSql.replace("tbname", tbnm));
						zcnt = getZqsCnt(stcd, zCntList)+"";
						qcnt = getZqsCnt(stcd, qCntList)+"";
						scnt = getZqsCnt(stcd, sCntList)+"";
					}else{
						switch( tbtype ){
						case "G4":
							prostcnt = 0;serial = DBReorgHelper.noSerial;missdate = null;
							sdtp = getSdtp(dbdao,yyyy,tbnm,stcd);
							stobitmcd = getObitmcdByStcdYear(stcd,yyyy);
							lacktp = lackSdType(stobitmcd,obitmcd, sdtp).get("lacktp");
							if(lacktp!=null&&lacktp!=""){
								sdtp +=" 缺少"+lacktp;
								lacktpcnt++;
							}
							zcnt = null;qcnt = null;scnt = null;
							break;
						case "A":case "B":case "G":
							prostcnt = 0;serial = DBReorgHelper.noSerial;missdate = null;
							sdtp = null;zcnt = null;qcnt = null;scnt = null;
							break;
						case "C":case "D":case "E":case "F":
							missdate = getMissDate(dbdao,yyyy,tbnm,stcd,tbtype,rulercdcnt,rtrcdcnt);//缺失日期
							if( null==missdate || missdate.length()==0 ){
								serial = DBReorgHelper.serialY;
							}else{
								serial = DBReorgHelper.serialN;
								prostcnt ++;
							}
							sdtp = null;zcnt = null;qcnt = null;scnt = null;
							break;
						case "C4":case "D4":case "E4":case "F4":
							missdate = getMissDate(dbdao,yyyy,tbnm,stcd,tbtype,rulercdcnt,rtrcdcnt);//缺失日期
							if( null==missdate || missdate.length()==0 ){
								serial = DBReorgHelper.serialY;
							}else{
								serial = DBReorgHelper.serialN;
								prostcnt ++;
							}
							sdtp = getSdtp(dbdao,yyyy,tbnm,stcd);
							stobitmcd = getObitmcdByStcdYear(stcd,yyyy);
							lacktp = lackSdType(stobitmcd,obitmcd, sdtp).get("lacktp");
							if(lacktp!=null&&!"".equals(lacktp)){
								sdtp +=" 缺少"+lacktp;
								lacktpcnt++;
							}
							zcnt = null;qcnt = null;scnt = null;
							break;
						}
					}
					detailOList.add(new Object[]{stcd,tbid,listid,yyyy,tbnm,
							DBReorgHelper.getStnm(stcd),rtrcdcnt,serial,missdate,zcnt,qcnt,scnt,sdtp});
				}
				DBReorgHelper.executeSQLBatch("{call insertResultDetail(?,?,?,?,?,?,?,?,?,?,?,?,?)}", 
					detailOList);//统计mdf中数据
			}
			//统计计划内、计划外数据
            if( (instList==null || instList.isEmpty())&&(!obitmcd.equals("0")) ){
            	return rtMap;
            	}
            List<Object[]> inOList = new ArrayList<>();
            List<Object[]> outOList = new ArrayList<>();
    	    int serialNcnt = 0,diffstcnt=0,sumrcdcnt=0;
            for( Object[] detailO:detailOList ){
            	String stcdO = detailO[0].toString();
        	    String serialO = detailO[7].toString();
        	    boolean flag = false;
        	    if(obitmcd.equals("0")){
        	    	flag = true;
        	    }
                for(Map<String, Object>inmap:instList){
            	    if(inmap.get("stcd").toString().equals(stcdO) ){
            		    flag = true;
            		    break;
            	    }
                }
                if( flag ){
                	diffstcnt ++;
                	sumrcdcnt += Integer.parseInt(detailO[6].toString());
                	if(serialO.equals(DBReorgHelper.serialN)){
                		serialNcnt++;
            	    }
            	    inOList.add(new Object[]{detailO[0],detailO[1],detailO[2],detailO[3],detailO[4],
            			    detailO[5],detailO[6],detailO[7],detailO[8],detailO[9],detailO[10],detailO[11],detailO[12],"0"});//非缺失
                }else{
                	boolean flag1 = false;
                    for(Map<String, Object>mdfmap:mdfstList){
                	    if(mdfmap.get("stcd").toString().equals(stcdO) ){
                	    	flag1 = true;
                		    break;
                	    }
                    }
                    if( flag1 ){
                    	outOList.add(detailO);
                    }
                }
            }
            List<Object[]> inOList1 = new ArrayList<>();
      	    List<String> probst = new ArrayList<String>();
            for(Map<String, Object>inmap:instList){
        	    boolean flag = false;
        	    for( Object[] inO:inOList ){
        		    if(inmap.get("stcd").toString().equals(inO[0].toString()) ){
        		    	flag = true;
            		    break;
            	    }
        	    }
        	    if( !flag ){
        	    	probst.add(inmap.get("stcd").toString()+"("+DBReorgHelper.getStnm(inmap.get("stcd").toString())+")");
        		    inOList1.add(new Object[]{inmap.get("stcd").toString(),tbid,listid,yyyy,tbnm,
						 DBReorgHelper.getStnm(inmap.get("stcd").toString()),0,DBReorgHelper.serialN,"",0,0,0,"","1"});//缺失
        	    }
            }
            inOList.addAll(inOList1);
            DBReorgHelper.executeSQLBatch("{call insertResultDetailIn(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}", 
        		    inOList);//计划内
            DBReorgHelper.executeSQLBatch("{call insertResultDetailOut(?,?,?,?,?,?,?,?,?,?,?,?,?)}", 
        		    outOList);//计划外
           
            //统计计划内问题站点数目、达标情况、达标详情
            int prob = serialNcnt+inOList1.size();
            int standard= 0;
            String stmt = "";
            if( prob==0 && lacktpcnt==0){
        	    standard = 1;
        	    stmt = "完全达标";
            }else {
        	    stmt = "问题测站总数："+prob+"。";
        	    if( inOList1.size()!=0 ){
        		    stmt += "未上报测站个数："+inOList1.size()+"。";
				    //stmt = "缺少测站{"+StringUtils.join(probst.toArray(),",")+"}";
        	    }
                if( serialNcnt!=0 ){
            	    stmt += "上报测站日期不连续个数："+serialNcnt;
                }
            }  
            rtMap.put("prostcnt", prob);//计划内问题站点数
            rtMap.put("standard", standard);//计划内是否达标
            rtMap.put("detail", stmt);//计划内达标详情
            rtMap.put("diffstcnt", diffstcnt);//计划内提交的站点数目
            rtMap.put("sumrcdcnt", sumrcdcnt);//计划内提交的总记录数目
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtMap;
	}

	private String getSdtp(BaseDao dbdao, String yyyy, String tbnm, String stcd) {
		String sdtp = "";
		try {

			String sql = DBReorgHelper.diffSdtpSql.replace("tbname", tbnm);
			List<Map<String,Object>> diffSdtpList = dbdao.executeQuery(sql,new Object[]{stcd});
			for( Map<String, Object>map:diffSdtpList ){
				String tmpsdtp = (null==map.get("sdtp"))?"":map.get("sdtp").toString();
				sdtp += tmpsdtp.length()==0?"":
							(sdtp.length()==0)?tmpsdtp:
								(sdtp.contains(tmpsdtp)?"":","+tmpsdtp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdtp;
	}

	private int getZqsCnt(String stcd, List<Map<String, Object>> qCntList) {
		int cnt = 0;
		for( Map<String, Object> qCntMap:qCntList ){
			if( stcd.equals(qCntMap.get("stcd").toString()) ){
				cnt = Integer.parseInt(qCntMap.get("strcdcnt").toString());
				break;
			}
		}
		return cnt;
	}

	private String getMissDate(BaseDao dbdao,String yyyy, String tbnm, String stcd, String tbtype, int rulercdcnt, int rtrcdcnt) {
		String result = "";
		try {
			switch( tbtype ){
				case "C":case "C4":
					List<Map<String, Object>> daylist = dbdao.executeQuery(
							DBReorgHelper.daySql.replace("tbname", tbnm),new Object[]{stcd});
					Calendar cald = Calendar.getInstance();
					Date stdt = DateUtil.str2Date(yyyy+"-1-1","yyyy-MM-dd");
					Date enddt = DateUtil.str2Date(yyyy+"-12-31","yyyy-MM-dd");
					Date tmpdt = stdt;
					while( enddt.getTime()-tmpdt.getTime()>=0 ){
						boolean flag = false;//是否存在
						String c1 = DateUtil.date2Str(tmpdt, "yyyy-MM-dd");
						for( Map<String, Object> dayMap:daylist ){
							String c2 = dayMap.get("tm").toString().substring(0,10);
							if( c1.equals(c2) ){
								flag = true;break;
							}
						}
						if( !flag ){
							result += (result.length()==0)?c1.substring(5):","+c1.substring(5);
						}
						cald.setTime(tmpdt);
						cald.add(Calendar.DAY_OF_YEAR, 1);
						tmpdt = cald.getTime();
					}					
					break;
				case "D":case "D4":
					List<Map<String, Object>> tenlist = dbdao.executeQuery(
							DBReorgHelper.tenSql.replace("tbname", tbnm),new Object[]{stcd});
					int mm1 = 12,tmpmm1 = 1;
					while( tmpmm1<=mm1 ){
						String uten = DateUtil.date2Str(DateUtil.str2Date(yyyy+"-"+tmpmm1+"-1","yyyy-MM-dd"),"yyyy-MM-dd");
						String mten = DateUtil.date2Str(DateUtil.str2Date(yyyy+"-"+tmpmm1+"-11","yyyy-MM-dd"),"yyyy-MM-dd");
						String dten = DateUtil.date2Str(DateUtil.str2Date(yyyy+"-"+tmpmm1+"-21","yyyy-MM-dd"),"yyyy-MM-dd");
						boolean uflag = false;//是否存在
						boolean mflag = false;
						boolean dflag = false;
						for( Map<String, Object> tenMap:tenlist ){
							String c2 = tenMap.get("tm").toString().substring(0,10);
							if( uten.equals(c2) ){
								uflag = true;
							}
							if( mten.equals(c2) ){
								mflag = true;
							}
							if( dten.equals(c2) ){
								dflag = true;
							}
						}
						if( !uflag ){
							result += (result.length()==0)?uten.substring(5):","+uten.substring(5);
						}
						if( !mflag ){
							result += (result.length()==0)?mten.substring(5):","+mten.substring(5);
						}
						if( !dflag ){
							result += (result.length()==0)?dten.substring(5):","+dten.substring(5);
						}
						tmpmm1++;
					}
					break;
				case "E":case "E4":
					List<Map<String, Object>> mmlist = dbdao.executeQuery(
							DBReorgHelper.mmSql.replace("tbname", tbnm),new Object[]{stcd});
					int mm = 12,tmpmm = 1;
					while( tmpmm<=mm ){
						boolean flag = false;//是否存在
						for( Map<String, Object> mmMap:mmlist ){
							if( yyyy.equals(mmMap.get("yyyy").toString()) 
									&& (tmpmm+"").equals(mmMap.get("mm").toString()) ){
								flag = true;break;
							}
						}
						if( !flag ){
							result += (result.length()==0)?tmpmm+"":","+tmpmm+"";
						}
						tmpmm++;
					} 
					break;
				case "F":case "F4":
					List<Map<String, Object>> yyyylist = dbdao.executeQuery(
							DBReorgHelper.yyyySql.replace("tbname", tbnm),new Object[]{stcd});
					boolean flag = false;//是否存在
					for( Map<String, Object> yyyyMap:yyyylist ){
						if( yyyy.equals(yyyyMap.get("yyyy").toString())){
							flag = true;break;
						}
					}
					if( !flag ){
						result = yyyy;
					}
					break;
			}
		}
		catch( Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private int getSumCnt(String tbnm, List<Map<String, Object>> sumRcdList) {
		int result = 0;
		for( Map<String, Object> sumRcdMap:sumRcdList ){//mssql 不区分表名大小写
			if( sumRcdMap.get("tbnm").toString().toLowerCase().equals(tbnm.toLowerCase()) ){
				result = Integer.parseInt(sumRcdMap.get("rowcnt").toString());
				break;
			}
		}
		return result;
	}

	public boolean updateReorgResponse(String listid, String resp, String respdes) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg(
					"{call updateReorgResponse(?,?,?)}",new Object[]{listid,resp,respdes});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean updateReorgIssue(String listid, String issue, String issuedes) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg(
					"{call updateReorgIssue(?,?,?)}",new Object[]{listid,issue,issuedes});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> getStationByGid(String groupid,String yyyy) {
		return DBReorgHelper.getDataFromReorg("{call getStation(?,?)}",new Object[]{groupid,yyyy});
	}

	public boolean updateStation(String stcd,String groupid,String stnm,String stct,String obitmcd,String yyyy) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateStation(?,?,?,?,?,?)}",
				new Object[]{stcd,groupid,stnm,stct,obitmcd,yyyy});
			refreshResultSum(groupid,yyyy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void refreshResultSum(String groupid, String yyyy) {
		DBReorgHelper.refreshList();
		List<Map<String, Object>> list = DBReorgHelper.callProByReorg(
				"{call getReorgListByGidYYYY(?,?)}", new Object[]{groupid,yyyy});
		if( null==list || list.isEmpty() ) return;
		insertResultSum(list.get(0).get("listid").toString(),groupid,yyyy);
	}

	public boolean addStation(String stcd,String groupid,String stnm,String stct,String obitmcd,String yyyy) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateStation(?,?,?,?,?,?)}",
				new Object[]{stcd,groupid,stnm,stct,obitmcd,yyyy});
			refreshResultSum(groupid,yyyy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean deleteStation(String stcd,String yyyy,String groupid) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call deleteStation(?,?)}",new Object[]{stcd,yyyy});
			refreshResultSum(groupid,yyyy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public List<Map<String,Object>> scanData(String dbid, String sql) {
		List<Map<String,Object>> rtList = null;
		try {
			BaseDao dbdao = DBReorgHelper.getDaoByDbid(dbid);
			rtList = dbdao.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtList;
	}

	/**
	 * 插入mdf\ldf 记录，mdf \ ldf 可以不同名
	 */
	public boolean insertAttachMdf(String dbid,String listid,String mdfname,String mdffname,
			String mdfpath,String mdflink,String ldfname,String ldffname,
			String ldfpath,String ldflink) {
		boolean result = false;
		try {
			List<Map<String, Object>> alist = DBReorgHelper.getDataFromReorg(
					"{call insertAttachMdf(?,?,?,?,?,?,?,?,?,?)}",
					new Object[]{dbid,listid,mdfname,mdfpath,mdflink,mdffname,
							ldfname,ldfpath,ldflink,ldffname});
			if( null!=alist && alist.size()>0 ){
				String mdfid = alist.get(0).get("mdfid").toString();
				result = DBReorgHelper.executeSqlByReorg(
					"{call updateReorgMdf(?,?)}",new Object[]{listid,mdfid});		
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *  记录保证书
	 * @param listid
	 * @param path
	 * @return
	 */
	public boolean insertAttachGua(String listid, String guaname,String gualink,String guapath,String guafname) {
		boolean result = false;
		try {
			List<Map<String, Object>> alist = DBReorgHelper.getDataFromReorg(
					"{call insertAttachGua(?,?,?,?,?)}",
					new Object[]{listid,guaname,gualink,guapath,guafname});
			if( null!=alist && alist.size()>0 ){
				String guaid = alist.get(0).get("guaid").toString();
				result = DBReorgHelper.executeSqlByReorg(
					"{call updateReorgGua(?,?)}",new Object[]{listid,guaid});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean updateReprgListDes(String listid,String listdes) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateReprgListDes(?,?)}",
				new Object[]{listid,listdes});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean updateReprgSumListDes(String listid,String tbid,String listdes) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateReprgSumListDes(?,?,?)}",
				new Object[]{listid,tbid,listdes});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @description 数据入库 当前只入库 CDEF
	 * @date 2016年12月2日下午4:01:01
	 * @author lxj
	 * @param listid
	 * @return
	 */
	public Map<String, Object> importDB(String listid,String tbid) {
		Map<String, Object> rtMap = new HashMap<>();
		rtMap.put("result", false);
		rtMap.put("msg", "");
		String tbnm = DBReorgHelper.getCheckTbnm(tbid);
		try {
			List<Map<String, Object>> reorglist = DBReorgHelper.callProByReorg(
				"{call getReorgListByListid(?)}",new Object[]{listid});
			
			if( null==reorglist || reorglist.size()==0 ) {
				rtMap.put("msg", "清单id "+listid+" 不存在，请检查");
				DBReorgHelper.logger.info("清单id "+listid+" 不存在，请检查");
				return rtMap;
			}
			Map<String,Object> reorgMap = reorglist.get(0);
			String status = reorgMap.get("list_status").toString();
			if( !status.equals("1") ){//检查listid 状态是否为1（已完成未入库），为1的时候才能进行入库操作
				rtMap.put("msg", "清单状态不为：已完成未入库，请检查");
				DBReorgHelper.logger.info("清单状态不为：已完成未入库，请检查");
				return rtMap;
			}
			
			List<Map<String, Object>> sumtblist = DBReorgHelper.callProByReorg(
					"{call getReorgSumInfoByListTB(?,?)}",new Object[]{listid,tbid});
			if( null==sumtblist || sumtblist.size()==0 ) {
				rtMap.put("msg", "清单id "+listid+" 不存在，请检查");
				DBReorgHelper.logger.info("清单id "+listid+" 不存在，请检查");
				return rtMap;
			}
			Map<String,Object> sumtbMap = sumtblist.get(0);
			
			String isimp = sumtbMap.get("tb_isimp").toString();
			if( isimp.equals("1") ){//表是否已经入库
				rtMap.put("msg", "表已入库:"+tbnm+" ，不能重复入库");
				DBReorgHelper.logger.info("表已入库:"+tbnm+" ，不能重复入库");
				return rtMap;
			}
			
			String dbid = reorgMap.get("dbid").toString();
			//String yyyy = reorgMap.get("list_year").toString();
			BaseDao sdbdao = DBReorgHelper.getDaoByDbid(dbid);//mssql
			String wherestr = getWhereStr(listid,tbid);//入库条件
			List<Map<String, Object>> stblist = sdbdao.executeQuery(
					DBReorgHelper.alllistSql.replace("tbname", tbnm)+wherestr);
			if( null==stblist ) {
				rtMap.put("msg", "源表数据获取失败："+tbnm);
				DBReorgHelper.logger.info("源表数据获取失败："+tbnm);
				return rtMap;
			}
			if( stblist.isEmpty() ) {
				rtMap.put("msg", "源表数据为空："+tbnm);
				DBReorgHelper.logger.info("源表数据为空："+tbnm);
				DBReorgHelper.executeSqlByReorg("{call updateResultSumIsimp(?,?)}",new Object[]{listid,tbid});
				DBReorgHelper.executeSqlByReorg("{call updateImpCnt(?,?,?)}",new Object[]{listid,tbid,"0"});
				return rtMap;
			}
			BaseDao tdbdao = DaoFactory.getDao("localhost");//mssql
			List<Map<String, Object>> destlist = tdbdao.executeQuery(DBReorgHelper.destiSql.replace("tbname", tbnm));
			Set<String> srcfield = stblist.get(0).keySet();
			List<String> srcfieldlist = new ArrayList<String>();
			srcfieldlist.addAll(srcfield);
			Set<String> destfield = destlist.get(0).keySet();
			for(int a=0;a<srcfieldlist.size();a++){
				String field = srcfieldlist.get(a);
				if(!destfield.contains(field)){
					srcfield.remove(field);
				}
			}
			String [] srcfields = new String[srcfield.size()];
			int j=0;
			for( String field:srcfield ){
				srcfields[j] = field;
				j++;
			}
			String sql = "";String sql1="";
			for( int k=0;k<srcfields.length;k++ ){
				if(k==0){
					sql1 += "?";
					sql += srcfields[k];
				}
				else{
					sql1 += ","+"?";
					sql += ","+srcfields[k];
				}
			}
			sql = "insert into "+tbnm+"("+sql+")values("+sql1+")";
			List<Object[]> listObject = new ArrayList<Object[]>();
			for( int k=0,len1=stblist.size();k<len1;k++ ){
				Map<String, Object> map = stblist.get(k);
				Object[] objects = new Object[srcfields.length];
				for( int n=0;n<srcfields.length;n++ ){
					objects[n] = map.get(srcfields[n]);
				}
				listObject.add(objects);
			}
			
			tdbdao.executeSQLBatch(sql, listObject);
			rtMap.put("result", true);
			DBReorgHelper.executeSqlByReorg("{call updateResultSumIsimp(?,?)}",new Object[]{listid,tbid});
			rtMap.put("msg", "表 "+tbnm+" 中 "+listObject.size()+" 条数据入库成功");
			DBReorgHelper.logger.info("表 "+tbnm+" 中 "+listObject.size()+" 条数据入库成功");
			DBReorgHelper.executeSqlByReorg("{call updateImpCnt(?,?,?)}",new Object[]{listid,tbid,listObject.size()});
		} catch (Exception e) {
			DBReorgHelper.logger.info("插入数据失败："+tbnm);
		}
		
		return rtMap;
	}

	private String getWhereStr(String listid, String tbid) {
		String result = "";
		List<Map<String, Object>> instlist = getReorgDetailInInfo(listid, tbid);
		//计划内需要入库站点
		for( Map<String, Object> instmap:instlist ){
			if( instmap.get("isimp").toString().equals(DBReorgHelper.isimpY) ){
				result += (result.length()==0?"'"+instmap.get("stcd").toString()+"'":",'"+instmap.get("stcd").toString()+"'");
			}
		}
		//计划外需要入库的站点
		List<Map<String, Object>> outstlist = getReorgDetailOutInfo_(listid, tbid);
		for( Map<String, Object> outstmap:outstlist ){
			if( outstmap.get("isimp").toString().equals(DBReorgHelper.isimpY) ){
				result += (result.length()==0?"'"+outstmap.get("stcd").toString()+"'":",'"+outstmap.get("stcd").toString()+"'");
			}
		}
		result = result.length()==0?result:" where stcd in ("+result+")";
		return result;//入库
	}

	private List<Map<String, Object>> getTarStCnt(String obitmcd, List<Map<String, Object>> stList) {
		List<Map<String, Object>> rtList = new ArrayList<>();
		if( obitmcd.equals("DA|DC") ){
			for( Map<String, Object> map:stList ){
				String tmpobitmcd = map.get("obitmcd").toString().toUpperCase();
				if( tmpobitmcd.contains("D") && (tmpobitmcd.contains("A") || tmpobitmcd.contains("C") )){
					if(!rtList.contains(map)){
						rtList.add(map);
					}
				}
			}
		}else if( obitmcd.equals("0") ){
			
		}else if( obitmcd.equals("1") ){
			rtList = stList;
		}
		else{
			int len = obitmcd.length();
			for( Map<String, Object> map:stList ){
				for( int i=0;i<len;i++ ){
					String ob = obitmcd.substring(i,i+1);
					String tmpobitmcd = map.get("obitmcd").toString().toUpperCase();
					if( tmpobitmcd.contains(ob) ){
						if(!rtList.contains(map)){
							rtList.add(map);
						}
					}
				}
			}
		}
		return rtList;
	}

	/**
	 * @description 根据 listid\tbid 获取计划内站点详情信息
	 * @date 2016年12月6日上午11:03:05
	 * @author lxj
	 * @param listid
	 * @param tbid
	 * @return
	 */
	public List<Map<String, Object>> getReorgDetailInInfo(String listid, String tbid) {
		return DBReorgHelper.callProByReorg("{call getReorgDetailInInfo(?,?)}", new Object[]{listid,tbid});
	}
	
	/**
	 * @description 根据 listid\tbid 获取计划外站点详情信息
	 * @date 2016年12月6日上午11:03:05
	 * @author lxj
	 * @param listid
	 * @param tbid
	 * @return
	 */
	public List<Map<String, Object>> getReorgDetailOutInfo_(String listid, String tbid) {
		return DBReorgHelper.callProByReorg("{call getReorgDetailOutInfo_(?,?)}", new Object[]{listid,tbid});
	}

	/**
	 * @description 根据 listid\stcd 获取计划外站点详情信息
	 * @date 2016年12月6日上午11:03:31
	 * @author lxj
	 * @param listid
	 * @param stcd
	 * @return
	 */
	public List<Map<String, Object>> getReorgDetailOutInfo(String listid, String stcd) {
		return DBReorgHelper.callProByReorg("{call getReorgDetailOutInfo(?,?)}", new Object[]{listid,stcd});
	}

	/**
	 * @description 更新计划内站点是否需要入库状态
	 * @date 2016年12月6日上午11:04:23
	 * @author lxj
	 * @param listid
	 * @param stcd
	 * @param isimp  1、0
	 * @return
	 */
	public boolean updateDetailInIsimp(String listid, String stcd, String isimp) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateDetailInIsimp(?,?,?)}",
					new Object[]{listid,stcd,isimp});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @description 更新计划外站点是否需要入库状态
	 * @date 2016年12月6日上午11:04:23
	 * @author lxj
	 * @param listid
	 * @param stcd
	 * @param isimp  1、0
	 * @return
	 */
	public boolean updateDetailOutIsimp(String listid, String stcd, String isimp) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateDetailOutIsimp(?,?,?)}",
					new Object[]{listid,stcd,isimp});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> getReorgDetailOutStaInfo(String listid) {
		return DBReorgHelper.callProByReorg("{call getReorgDetailOutStaInfo(?)}", new Object[]{listid});
	}
	
	public List<Map<String, Object>> getObitmcdlist() {
		return DBReorgHelper.callProByReorg("{call getObitmcdlist()}", null);
	}
	
	public boolean addObitmcd(String id,String value,String name) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call insertObitmcd(?,?,?)}",
				new Object[]{id,value,name});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean updateObitmcd(String id,String value,String name) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call updateObitmcd(?,?,?)}",
				new Object[]{id,value,name});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean delObitmcd(String id,String value,String name) {
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call delObitmcd(?,?,?)}",
				new Object[]{id,value,name});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean deleteResultDetailInOut(String listid){
		boolean result = false;
		try {
			result = DBReorgHelper.executeSqlByReorg("{call deleteResultDetailInOut(?)}",
					new Object[]{listid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public boolean copyStationToYear(String gid,String year1,String year2){
		boolean result = false;
		try{
			result = DBReorgHelper.executeSqlByReorg("{call copyStationToYear(?,?,?)}",
					new Object[]{gid,year1,year2});
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public boolean delReorgTask(String listid){
		boolean result = false;
		try{
			result = DBReorgHelper.executeSqlByReorg("{call delReorgList(?)}",
					new Object[]{listid});
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public Map<String, String> lackSdType(String stobitmcd,String tabobitmcd,String sdtp){
		Map<String, String> rtMap = new HashMap<String, String>();
		try{
			int len = stobitmcd.length();
			String bigob = stobitmcd.toUpperCase();
			String bigob2 = tabobitmcd.toUpperCase();
			String obstr = "";
			String[] sdarry = sdtp.split(",");
			String ob = "";
			List<String> oblist=new ArrayList<String>();
			for(int i=0;i<len;i++){
				ob = bigob.substring(i,i+1);
				if(bigob2.contains(ob)){
				switch(ob){
				case "A":oblist.add("悬移质");break;
				case "C":oblist.add("推移质");break;
				case "U":oblist.add("沙推移质");break;
				case "V":oblist.add("卵石推移质");break;
				case "F":oblist.add("床沙");break;
				case "J":oblist.add("颗分");break;
				}
				}
			}
			for(int i=0;i<sdarry.length;i++){
				oblist.remove(sdarry[i]);
				if(sdarry[i].equals("悬移质")){
					oblist.remove("颗分");
				}
			}
			for(int i=0;i<oblist.size();i++){
				obstr = (i==(oblist.size()-1))?obstr+oblist.get(i):obstr+oblist.get(i)+",";
			}
			rtMap.put("lacktp", obstr);
		}catch(Exception e){
			e.printStackTrace();
		}
		return rtMap;
	}
	public int sdsCnt(String stobitmcd , String tabobitmcd){
		int cnt = 0;
		int len = stobitmcd.length();
		String bigob = stobitmcd.toUpperCase();
		String bigob2 = tabobitmcd.toUpperCase();
		String ob = "";
		for(int i=0;i<len;i++){
			ob = bigob.substring(i,i+1);
			if("ACUVFJ".contains(ob)&&bigob2.contains(ob)){
				cnt++;
			}
		}
		return cnt;
	}
	public String getObitmcdByStcdYear(String stcd, String year){
		String sql = "select obitmcd from dbm_reorganize.sys_reorg_stinfo where stcd=? and yyyy=? and deleted='0';";
		String result = "";
		try{
			Map<String, Object> obmap = DBReorgHelper.getDataFromReorg(sql,new Object[]{stcd,year}).get(0);
			if(obmap!=null){
				result = obmap.get("obitmcd").toString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
