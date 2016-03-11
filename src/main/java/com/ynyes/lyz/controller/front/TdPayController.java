package com.ynyes.lyz.controller.front;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value = "/pay")
public class TdPayController {

	@Autowired
	private TdOrderService tdOrderService;

	@RequestMapping(value = "/alipay")
	public String alipay(HttpServletRequest req, ModelMap map, Long id, Long type) {
		String username = (String) req.getSession().getAttribute("username");
		if (null == username) {
			return "redirect:/login";
		}
		System.err.println(id);
		// // -----请求参数-----
		//
		// 支付类型
		String payment_type = "1";

		// 获取需要支付的订单
		TdOrder order = tdOrderService.findOne(id);

		// 页面跳转同步通知页面路径
		String return_url = SiteMagConstant.alipayReturnUrl;

		String subject = null;

		if (null != type && type.longValue() == 0L) {
			subject = "乐易装交易线上支付";
		}

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("out_trade_no", order.getOrderNumber());
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
		sParaTemp.put("show_url", "127.0.0.1:8080/user/order/0");
		sParaTemp.put("subject", "乐易装商品线上支付");
		sParaTemp.put("total_fee", order.getTotalPrice() + "");
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		map.put("code", sHtmlText);
		return "/client/waiting_pay";
	}

}
