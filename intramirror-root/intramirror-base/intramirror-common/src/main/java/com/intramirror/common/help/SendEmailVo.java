package com.intramirror.common.help;

/**
 * 
 * @author wzh
 *
 */
public class SendEmailVo {
	
	/** 设置邮件服务器地址 */
	private String host;

	/** 邮件的端口*/
	private String port;

	/** 邮件的用户名 */
	private String userName;

	/** 邮件的登录密码  或者授权码*/
	private String passWord;

	/** 收件人邮箱地址 */
	private String addressee;
	
	/** 邮件标题 */
	private String title;

	/** 邮件内容 */
	private String content;
	

	public String getAddressee() {
		return addressee;
	}

	public String getContent() {
		return content;
	}

	public String getHost() {
		return host;
	}

	public String getPassWord() {
		return passWord;
	}

	public String getUserName() {
		return userName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	


}
