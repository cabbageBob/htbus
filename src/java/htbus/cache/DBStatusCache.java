package htbus.cache;

import java.net.URL;
import java.util.List;


import htbus.service.DataService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
public class DBStatusCache {
	static URL url = DBStatusCache.class.getResource("/ehcache.xml");
	static CacheManager cacheManager = CacheManager.create(url);
	static Cache cache = cacheManager.getCache("DBStatusCache");
	public static void setListCache( String name, List<?> list) {
		Element element = new Element(name, list);
		cache.put(element);
	}
	
	public static void refreshCache() {
		setListCache("dbstatus",DataService.getInstanceStatus());
	}
	
	public static Cache getCache() {
//	        Cache cache = cacheManager.getCache(cacheName);
	        return cache;
	    }
}
