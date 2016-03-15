package com.tencent.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.ui.ModelMap;

import com.sun.mail.imap.protocol.BODY;
import com.sun.tools.jxc.gen.config.Config;
import com.ynyes.lyz.entity.TdOrder;

public class TdWXPay {
	
	private static String notify_url_str = "www.leyizhuang.com.cn/order/wx_notify";
	
	private static String trade_type_str = "APP";
	
	/**
	 * 统一下单所需要的XML
	 * @param order
	 * @return
	 */
	public static String getUnifiedorderXML(TdOrder order)
	{
		/*
		 *  app调起WX支付必要参数如下：
		 * 
		 * appid         ：微信开放平台审核通过的应用APPID
		 * mch_id        ：微信支付分配的商户号
		 * nonce_str      ：随机字符串，不长于32位。
		 * body           ：商品或支付单简要描述
		 * out_trade_no   ：商户系统内部的订单号,32个字符内、可包含字母
		 * total_fee       ：订单总金额，单位为分
		 * spbill_create_ip ：用户端实际ip
		 * notify_url       ：接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
		 * trade_type      ：支付类型
		 * sign            ：签名
		 * 
		 * ------> end
		 */
		
		//统一下单的签名XML
		String body = "支付订单" + order.getOrderNumber();
		String out_trade_no = order.getOrderNumber();
		Long total_fee = Math.round(order.getAllTotalPay() * 100);
		String nonce_str = RandomStringGenerator.getRandomStringByLength(32);
		
		ModelMap signMap = new ModelMap();
		signMap.addAttribute("appid", Configure.getAppid());
		signMap.addAttribute("mch_id", Configure.getMchid());
		signMap.addAttribute("nonce_str",nonce_str);
		signMap.addAttribute("body", body);
		signMap.addAttribute("out_trade_no", out_trade_no);
		signMap.addAttribute("total_fee", total_fee);
		signMap.addAttribute("spbill_create_ip", Configure.getIP());
		signMap.addAttribute("notify_url", notify_url_str);
		signMap.addAttribute("trade_type", trade_type_str);

		String sign = Signature.getSign(signMap);

		String content = "<xml>\n"
					+ "<appid>" + Configure.getAppid() + "</appid>\n"
					+ "<mch_id>" + Configure.getMchid() + "</mch_id>\n"
					+ "<nonce_str>" + nonce_str + "</nonce_str>\n"
					+ "<body>支付订单" + body + "</body>\n"
					+ "<out_trade_no>" + out_trade_no + "</out_trade_no>\n"
					+ "<total_fee>" + total_fee + "</total_fee>\n"
					+ "<spbill_create_ip>"+ Configure.getIP() +"</spbill_create_ip>\n"
					+ "<notify_url>"+ notify_url_str +"</notify_url>\n"
					+ "<trade_type>"+ trade_type_str +"</trade_type>\n"
					+ "<sign>" + sign + "</sign>\n"
					+ "</xml>\n";
//		System.out.print("MDJ: xml=" + content + "\n");
		return content;
	}
	
	/**
	 * 统一下单请求获取 prepay_id
	 * @param requestXML
	 */
	public static void sendUnifiedorderRequest(String requestXML)
	{
		
		String return_code = null;
		String prepay_id = null;
		String result_code = null;
		String line = null;
		
		if (requestXML == null)
		{
			return;
		}
		try 
		{
			HttpsURLConnection urlConection = null;
			urlConection = (HttpsURLConnection) (new URL(Configure.getUNIFIED_ORDER_API())).openConnection();
			urlConection.setDoInput(true);
			urlConection.setDoOutput(true);
			urlConection.setRequestMethod("POST");
			urlConection.setRequestProperty("Content-Length",String.valueOf(requestXML.getBytes().length));
			urlConection.setUseCaches(false);
			// 设置为gbk可以解决服务器接收时读取的数据中文乱码问题
			urlConection.getOutputStream().write(requestXML.getBytes("utf-8"));
			urlConection.getOutputStream().flush();
			urlConection.getOutputStream().close();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConection.getInputStream()));

			while ((line = in.readLine()) != null)
			{
				//			System.out.println(": rline: " + line);
				if (line.contains("<return_code>"))
				{
					return_code = line.replaceAll("<xml><return_code><\\!\\[CDATA\\[", "").replaceAll("\\]\\]></return_code>", "");
				} 
				else if (line.contains("<prepay_id>")) 
				{
					prepay_id = line.replaceAll("<prepay_id><\\!\\[CDATA\\[","").replaceAll("\\]\\]></prepay_id>", "");
				}
				else if (line.contains("<result_code>"))
				{
					result_code = line.replaceAll("<result_code><\\!\\[CDATA\\[", "").replaceAll("\\]\\]></result_code>", "");
				}
			}

			//		System.out.println("MDJ: return_code: " + return_code + "\n");
			//		System.out.println("MDJ: prepay_id: " + prepay_id + "\n");
			//		System.out.println("MDJ: result_code: " + result_code + "\n");

			if ("SUCCESS".equalsIgnoreCase(return_code) && "SUCCESS".equalsIgnoreCase(result_code) && null != prepay_id)
			{
				String nonce_str = RandomStringGenerator.getRandomStringByLength(32);

				String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
				String packageString = "prepay_id=" + prepay_id;
				String signType = "MD5";
				ModelMap returnsignmap = new ModelMap();
				returnsignmap.addAttribute("appId", Configure.getAppid());
				returnsignmap.addAttribute("timeStamp", timeStamp);
				returnsignmap.addAttribute("nonceStr", nonce_str);
				returnsignmap.addAttribute("package", packageString);
				returnsignmap.addAttribute("signType", signType);


				String returnsign = Signature.getSign(returnsignmap);
				requestXML = "<xml>\n" 
							+ "<appid>" + Configure.getAppid() + "</appid>\n"
							+ "<timeStamp>" + timeStamp + "</timeStamp>\n" 
							+ "<nonceStr>" + nonce_str + "</nonceStr>\n" 
							+ "<package>" + packageString + "</package>\n" 
							+ "<signType>" + signType + "</signType>\n"
							+ "<signType>" + returnsign + "</signType>\n"
							+ "</xml>\n";

				System.out.print(": returnPayData xml=" + requestXML);
				ModelMap map = new ModelMap();
				map.addAttribute("appId", Configure.getAppid());
				map.addAttribute("timeStamp", timeStamp);
				map.addAttribute("nonceStr", nonce_str);
				map.addAttribute("package", packageString);
				map.addAttribute("signType", signType);
				map.addAttribute("paySign", returnsign);
				map.addAttribute("orderId", "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	
	}
}
