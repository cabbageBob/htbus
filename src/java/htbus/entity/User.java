package htbus.entity;

public class User {
	/**2017年11月26日下午7:08:58
	 *
	 * @author Jokki
	 *  
	 */
	private String uid;

    private String name;

    private String mobile;

    private String tel;

    private String smobile;
    
    private String ipphone;
    
    private String mail;
    
    private String sex;
    
    private String photo;

    public String getIpphone() {
        return ipphone;
    }

    public String getMail() {
        return mail;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSmobile() {
        return smobile;
    }

    public String getTel() {
        return tel;
    }

    public String getUid() {
        return uid;
    }

    public void setIpphone(String ipphone) {
        this.ipphone = ipphone;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSmobile(String smobile) {
        this.smobile = smobile;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
