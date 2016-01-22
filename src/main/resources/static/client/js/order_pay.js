/**
 * 填写备注留言之后存储留言的方法（失去焦点事件）
 * 
 * @author dengxiao
 */
function userRemark(old_remark) {
	var remark = $("#remark").val();
	// 如果没有填写备注留言，则不需要存储其信息
	if ("" == remark.trim()) {
		return;
	}
	// 如果跟上一次一样，也不需要存储
	if (old_remark == remark.trim()) {
		return;
	}

	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/remark/save",
		timeout : 10000,
		type : "post",
		data : {
			remark : remark
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// 关闭等待响应的图标
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			// 关闭等待图标
			close(100);
			if (0 == res.status) {
				warning("已保存");
				$("#remark")
						.attr("onblur", "userRemark('" + res.remark + "');")
			} else {
				warning("亲，您的网速不给力啊");
			}
		}
	});
}

/**
 * 去支付的方法
 * 
 * @author dengxiao
 */
function orderPay() {
	var userCash = false;
	var isUserCash = $("#isUserCash");
	var classes = isUserCash.attr("class");
	if (classes.indexOf("active") != -1) {
		userCash = true;
	}

	var reg = /^[0-9]+([.]{1}[0-9]{1,2})?$/;

	// 获取当前用户使用的预存款额度
	var usedBalance = $("#usedBalance").val();
	
	// add by Shawn 如果取消使用预存款，则不使用
	if (false == userCash)
	{
		usedBalance = 0;
	}

	// 获取当前用户的余额
	var userBalance = $("#userBalance").val();

	if (!reg.test(usedBalance)) {
		warning("亲，预存款一栏中输入正确的数字（必须为大于或等于0的数字且不超过小数点后2位）");
		return;
	}

	if (parseFloat(usedBalance) > parseFloat(userBalance)) {
		warning("亲，您输入的预存款使<br>用额大于了您的存款总额");
		return;
	}

	// 获取订单总额
	var order_total_price = $("#order_total_price").html();
	console.debug(order_total_price);

	if (parseFloat(usedBalance) > parseFloat(order_total_price)) {
		warning("亲，您输入的预存款使<br>用额大于了订单总额");
		return;
	}

	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/check",
		type : "post",
		timeout : 10000,
		data : {
			userCash : userCash,
			userUsed : usedBalance
		},
		error : function() {
			// 关闭等待图标
			close(1);
			warning("您的网速不给力啊");
		},
		success : function(res) {
			// 关闭等待图标
			close(100);
			$("#buyNow").attr("href", "javascript:void(0);")
			warning(res.message);
			if (-1 == res.status) {
				$("#buyNow").attr("href", "javascript:orderPay();")
			}
			if (0 == res.status) {
				setTimeout(function() {
					window.location.href = "/order/pay";
				}, 1000)
			}
		}
	});
}
