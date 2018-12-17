/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.factory;

import cn.miao.framework.dao.BaseDao;

/**
 * Dao Factorty
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 28, 2012 3:22:01 PM
 * 
 * @see
 */
public class DaoFactory {

	/**
	 * 获取一个普通的Dao
	 * 
	 * @param alias
	 * @return BaseDao
	 * @since v 1.0
	 */
	public static BaseDao getDao(String alias) {
		return new BaseDao(alias);
	}
}
