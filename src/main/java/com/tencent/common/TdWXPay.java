package com.tencent.common;

import org.springframework.ui.ModelMap;

import com.ynyes.lyz.entity.TdOrder;

public class TdWXPay {
	
	
	
	public static void sendUnifiedorder()
	{
		TdOrder order = new TdOrder();
		//统一支付接口
		String noncestr = RandomStringGenerator.getRandomStringByLength(32);
		ModelMap signMap = new ModelMap();
		signMap.addAttribute("appid", Configure.getAppid());
		signMap.addAttribute("attach", "订单支付");
		signMap.addAttribute("body", "支付订单" + order.getOrderNumber());
		signMap.addAttribute("mch_id", Configure.getMchid());
		signMap.addAttribute("nonce_str",noncestr);
		signMap.addAttribute("out_trade_no", order.getOrderNumber());
		signMap.addAttribute("total_fee", Math.round(order.getTotalPrice() * 100));
		signMap.addAttribute("spbill_create_ip", "116.55.230.178");
		signMap.addAttribute("notify_url", "http://www.cbs023.com/order/wx_notify");
		signMap.addAttribute("trade_type", "JSAPI");

		String mysign = Signature.getSign(signMap);

		String content = "<xml>\n"
				+ "<appid>" + Configure.getAppid() + "</appid>\n"
				+ "<attach>订单支付</attach>\n" 
				+ "<body>支付订单" + order.getOrderNumber() + "</body>\n"
				+ "<mch_id>" + Configure.getMchid() + "</mch_id>\n"
				+ "<nonce_str>" + noncestr + "</nonce_str>\n"
				+ "<notify_url>http://www.cbs023.com/order/wx_notify</notify_url>\n"
				+ "<out_trade_no>" + order.getOrderNumber() + "</out_trade_no>\n"
				+ "<spbill_create_ip>116.55.230.178</spbill_create_ip>\n"
				+ "<total_fee>" + Math.round(order.getTotalPrice() * 100) + "</total_fee>\n"
				+ "<trade_type>JSAPI</trade_type>\n"
				+ "<sign>" + mysign + "</sign>\n"
				+ "</xml>\n";
		System.out.print("MDJ: xml=" + content + "\n");
	}
}
