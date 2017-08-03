package pk.shoplus.thirdpart.telenor;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "corpsms")
public class CorpSms {
	private String command;
	private String data;
	private String response;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
