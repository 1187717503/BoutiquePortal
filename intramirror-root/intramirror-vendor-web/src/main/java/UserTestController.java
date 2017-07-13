import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.intramirror.user.api.apimq.IApiMqService;
import com.intramirror.user.api.model.UserApiMq;

@Controller
@RequestMapping("/usertest")
public class UserTestController {
	
	@Resource(name="userApiMqService")
	private IApiMqService iApiMqService;
	
	@RequestMapping("/test03")
	@ResponseBody
	public String test03(){
		UserApiMq apiMq = iApiMqService.getApiMq();
		return new Gson().toJson(apiMq);
	}
}
