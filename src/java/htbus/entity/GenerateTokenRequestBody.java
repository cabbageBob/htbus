package htbus.entity;

import java.io.Serializable;

public class GenerateTokenRequestBody implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**2018年3月9日上午10:54:24
	 *
	 * @author Jokki
	 *  
	 */
	
	private String username;
	
	private String password;
	
	private String f = "json";
	
	private String client = "requestip";
	
	private String expiration = "";
	
	public String getUsername() {
        return username;
    }

    public String getClient() {
        return client;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getF() {
        return f;
    }

    public String getPassword() {
        return password;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public void setF(String f) {
        this.f = f;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
