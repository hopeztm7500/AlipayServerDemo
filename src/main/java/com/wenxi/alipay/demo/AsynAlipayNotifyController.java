package com.wenxi.alipay.demo;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.wenxi.alipay.bean.AlipayNotification;
import com.wenxi.alipay.util.AlipayNotify;
import com.wenxi.alipay.util.RequestUtils;

@Controller
@RequestMapping("store/thirdparty/pay/alipay")
public class AsynAlipayNotifyController {

	
	private static Logger logger = LoggerFactory.getLogger(AsynAlipayNotifyController.class);
		
	/**
	 * 异步接受支付宝支付结果 
	 * 支付宝服务器调用
	 * 
	 * @param request
	 * @param response
	 */

	@RequestMapping(value = "notify", method = RequestMethod.POST)
	public void receiveNotify(HttpServletRequest request, HttpServletResponse response) {
		
		
		Map<String, String> underScoreKeyMap = RequestUtils.getStringParams(request);
		Map<String, String> camelCaseKeyMap = RequestUtils.convertKeyToCamelCase(underScoreKeyMap);
		
		//首先验证调用是否来自支付宝
		boolean verifyResult = AlipayNotify.verify(underScoreKeyMap);
		
		try {
			
			String jsonString = JSON.toJSONString(camelCaseKeyMap);
			AlipayNotification notice = JSON.parseObject(jsonString, AlipayNotification.class);
			notice.setVerifyResult(verifyResult);
			
			String resultResponse = "success";
			PrintWriter printWriter = null;
			try {
				printWriter = response.getWriter();
				
				//do business
				if(verifyResult){
					
				}
				//fail due to verification error
				else{
					resultResponse = "fail";
				}
				
			} catch (Exception e) {
				logger.error("alipay notify error :", e);
				resultResponse = "fail";
				printWriter.close();
			}
			
			
			if (printWriter != null) {
				printWriter.print(resultResponse);
			}
			
		} catch (Exception e1) {
		
			e1.printStackTrace();
		} 
	}
}
