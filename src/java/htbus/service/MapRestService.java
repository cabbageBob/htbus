package htbus.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapRestService {
	/**2018年3月9日上午10:46:27
	 *
	 * @author Jokki
	 *  
	 */
	
	@POST("admin/generateToken")
	@FormUrlEncoded
	Call<Map<String, Object>> generateToken(@Field("username") String username, @Field("password") String password,
			@Field("f") String f, @Field("client") String client, @Field("expiration") String expiration);
	
	@GET("admin/services/{path}")
	Call<Map<String, Object>> getMapServices(@Path("path") String path, @Query("f") String f, @Query("token") String token);
}
