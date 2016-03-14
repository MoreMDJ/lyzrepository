package com.ynyes.lyz.controller.front;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value = "/pay")
public class TdPayController {

	@Autowired
	private TdOrderService tdOrderService;
	
	@Autowired
	private TdCommonService tdCommonService;

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
		if (null != type && type.longValue() == 1L) {
			subject = "乐易装电子钱包充值";
		}

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("out_trade_no", order.getOrderNumber());
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
		sParaTemp.put("show_url", "http://127.0.0.1:8080/user/order/0");
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", order.getTotalPrice() + "");
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		map.put("code", sHtmlText);
		return "/client/waiting_pay";
	}

	@RequestMapping(value = "/alipay/return")
	public String alipayReturn(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = req.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			try {
				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//

		String out_trade_no = null;
		String trade_no = null;
		String trade_status = null;
		try {
			// 商户订单号
			out_trade_no = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
			// 支付宝交易号
			trade_no = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
			// 交易状态
			trade_status = new String(req.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(params);
		verify_result = true;
		if (verify_result) {// 验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序

				// 如果是下单的情况
				if (out_trade_no.contains("XN")) {
					TdOrder order = tdOrderService.findByOrderNumber(out_trade_no);
					if (null != order) {
						req.getSession().setAttribute("order_temp", order);
						tdCommonService.dismantleOrder(req, username);
					}
				}

				// 如果是充值的情况下
				if (out_trade_no.contains("CZ")) {

				}

			}

		} else {
			// 该页面可做页面美工编辑
		}
		return "/client/pay_success";
	}

}
