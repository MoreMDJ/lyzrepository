package com.ynyes.lyz.controller.front;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;
import com.tencent.common.TdWXPay;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.service.TdCommonService;
import com.ynyes.lyz.service.TdCouponService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.util.SiteMagConstant;

@Controller
@RequestMapping(value = "/pay")
public class TdPayController {

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdCommonService tdCommonService;

	@Autowired
	private TdCouponService tdCouponService;

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

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)

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
				TdOrder order = tdOrderService.findByOrderNumber(out_trade_no);
				order.setActualPay(order.getTotalPrice());
				if (null != order) {
					req.getSession().setAttribute("order_temp", order);
					String cashCouponId = order.getCashCouponId();
					String productCouponId = order.getProductCouponId();
					// 将所使用的优惠券设置为已使用状态
					if (null != cashCouponId) {
						String[] cashs = cashCouponId.split(",");
						if (null != cashs) {
							for (String sId : cashs) {
								if (null != sId && !"".equals(sId)) {
									Long coupon_id = Long.valueOf(sId);
									TdCoupon coupon = tdCouponService.findOne(coupon_id);
									if (null != coupon) {
										coupon.setIsUsed(true);
										coupon.setUseTime(new Date());
										tdCouponService.save(coupon);
									}
								}
							}
						}
					}

					if (null != productCouponId) {
						String[] products = productCouponId.split(",");
						if (null != products) {
							for (String sId : products) {
								if (null != sId && !"".equals(sId)) {
									Long coupon_id = Long.valueOf(sId);
									TdCoupon coupon = tdCouponService.findOne(coupon_id);
									if (null != coupon) {
										coupon.setIsUsed(true);
										coupon.setUseTime(new Date());
										tdCouponService.save(coupon);
									}
								}
							}
						}
					}
					// 虚拟订单需要分单
					if (out_trade_no.contains("XN")) {
						tdCommonService.dismantleOrder(req, username);
					}
				}
			}
		}
		return "/client/pay_success";
	}

	@RequestMapping(value = "/union")
	public String unionPay(HttpServletRequest req, ModelMap map, Long id, Long type) {
		// 判断用户是否登录
		String username = (String) req.getSession().getAttribute("username");
		if (null == username) {
			return "redirect:/login";
		}

		TdOrder order = tdOrderService.findOne(id);

		/*
		 * 重新更改订单号
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date now = new Date();
		String sDate = sdf.format(now);
		Random random = new Random();
		Integer suiji = random.nextInt(900) + 100;
		String orderNum = sDate + suiji;
		order.setOrderNumber(orderNum);
		tdOrderService.save(order);

		// 开始组合参数
		String MERCHANTID = "105510148160146";
		String POSID = "632776177";
		String BRANCHID = "510000000";
		String ORDERID = order.getOrderNumber();
		String PAYMENT = order.getTotalPrice() + "";
		String CURCODE = "01";
		String TXCODE = "520100";
		String TYPE = "1";
		String PUB = "30819c300d06092a864886f70d010101050003818a00308186028180756c3ad19960d52e9932c000bbbfa13c98726cba9c6117c0ab42391dd2c20fbe750fedffe3ab972f6f98d47d9d048ffb26d7fdfe804bc99e36db9233d6affb1e248faf997b488cdc560ca4548f6722222b924ec239e68d204536220f5d1913d0842a996e83837d328494a729e1b66aaa28fb7149ca35c6e2b0deed7800fe5fa1020111";

		String PUB32 = PUB.substring(0, 30);
		String PUB32TR2 = PUB.substring(PUB.length() - 30);

		map.addAttribute("MERCHANTID", MERCHANTID);
		map.addAttribute("POSID", POSID);
		map.addAttribute("BRANCHID", BRANCHID);
		map.addAttribute("ORDERID", ORDERID);
		map.addAttribute("PAYMENT", PAYMENT);
		map.addAttribute("CURCODE", CURCODE);
		map.addAttribute("TXCODE", TXCODE);
		map.addAttribute("TYPE", TYPE);
		map.addAttribute("PUB32", PUB32);
		map.addAttribute("PUB32TR2", PUB32TR2);

		return "/client/union_pay";
	}

	@RequestMapping(value = "/union/return")
	public String unionReturn(HttpServletRequest req, ModelMap map, String ORDERID, Double PAYMENT, Character SUCCESS) {
		String username = (String) req.getSession().getAttribute("username");
		// 根据指定的订单号查找订单
		TdOrder order = tdOrderService.findByOrderNumber(ORDERID);
		// 在能查询到具体订单的情况下进行业务逻辑处理
		if (null != order) {
			Double totalPrice = order.getTotalPrice();
			// 在支付金额和实际金额相匹配的情况下再进行业务逻辑的处理
			if (null != totalPrice && totalPrice.longValue() == PAYMENT.longValue()) {
				// 判断是否支付成功
				if (null != SUCCESS && SUCCESS.charValue() == 'Y') {
					if (null != order) {
						req.getSession().setAttribute("order_temp", order);
						String cashCouponId = order.getCashCouponId();
						String productCouponId = order.getProductCouponId();
						// 将所使用的优惠券设置为已使用状态
						if (null != cashCouponId) {
							String[] cashs = cashCouponId.split(",");
							if (null != cashs) {
								for (String sId : cashs) {
									if (null != sId && !"".equals(sId)) {
										Long coupon_id = Long.valueOf(sId);
										TdCoupon coupon = tdCouponService.findOne(coupon_id);
										if (null != coupon) {
											coupon.setIsUsed(true);
											coupon.setUseTime(new Date());
											tdCouponService.save(coupon);
										}
									}
								}
							}
						}

						if (null != productCouponId) {
							String[] products = productCouponId.split(",");
							if (null != products) {
								for (String sId : products) {
									if (null != sId && !"".equals(sId)) {
										Long coupon_id = Long.valueOf(sId);
										TdCoupon coupon = tdCouponService.findOne(coupon_id);
										if (null != coupon) {
											coupon.setIsUsed(true);
											coupon.setUseTime(new Date());
											tdCouponService.save(coupon);
										}
									}
								}
							}
						}
						if (ORDERID.contains("XN")) {
							tdCommonService.dismantleOrder(req, username);
						}
					}
				}
			}
		}
		return "/client/pay_success";
	}

	@RequestMapping(value = "/wx/sign")
	@ResponseBody
	public Map<String, Object> WxPayReturnSign(Long orderId) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("code", 0);
		if (orderId == null) {
			resultMap.put("msg", "订单Id不存在");
			return resultMap;
		}
		TdOrder tdOrder = tdOrderService.findOne(orderId);
		if (tdOrder == null) {
			resultMap.put("msg", "订单不存在，单号Id：" + orderId);
			return resultMap;
		}
		String xml = TdWXPay.getUnifiedorderXML(tdOrder);
		ModelMap modelMap = TdWXPay.sendUnifiedorderRequest(xml);

		if (modelMap != null) {
			resultMap.put("sign", modelMap);
		} else {
			resultMap.put("msg", "签名出错");
			return resultMap;
		}
		resultMap.put("code", 1);
		return resultMap;
	}

	@RequestMapping(value = "/wx_notify")
	public void wxPayNotify(HttpServletResponse response, HttpServletRequest request) throws IOException, Exception {
		Map<String, String> map = TdWXPay.parseXml(request);
		String return_code = map.get("return_code");
		if (return_code != null && return_code.contains("SUCCESS")) {
			String out_trade_no = map.get("out_trade_no");
			System.out.println(out_trade_no);
		}
	}
}
