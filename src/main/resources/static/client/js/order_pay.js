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
	var amount = $("#amount").val();

	// 判断是否输入了正确的预存款使用额度
	if ("" == amount) {
		amount = 0.00;
	}

	// 判断输入的预存款是否是个正确的数字
	if (!/^([1-9]\d{0,15}|0)(\.\d{1,2})?$/.test(amount)) {
		warning("亲，请输入正确的预存款使用额<br>（最多只能填写到小数点后两位）");
		return;
	}

	// 判断是否超额
	all_balance = $("#all_balance").html();
	if (amount * 1 > all_balance * 1) {
		warning("亲，您使用的预存款额<br>度超过了您的钱包余额");
		return;
	}

	// 判断预存款使用额度是否大于总款项
	order_total_price = $("#order_total_price").html();
	if (amount * 1 > order_total_price * 1) {
		warning("亲，您使用的预存款额度<br>已经超过了订单总金额");
		return;
	}

	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/check",
		type : "post",
		timeout : 10000,
		data:{
			amount:amount
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
			if(-1 == res.status){
				$("#buyNow").attr("href", "javascript:orderPay();")
			}
			if(0 == res.status){
				setTimeout(function(){
					window.location.href = "/order/pay";
				},1000)
			}
		}
	});
}