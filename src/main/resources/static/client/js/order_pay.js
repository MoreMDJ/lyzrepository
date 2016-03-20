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
 * 获取本店所有会员信息的方法
 * 
 * @author DengXiao
 */
function getUserInfo() {
	wait();
	$.ajax({
		url : "/order/get/user/infomation",
		timeout : 20000,
		type : "post",
		error : function() {
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			close(1);
			$("#changeInfo").html(res);
			win_yes();
		}
	});
}

/**
 * 去支付的方法
 * 
 * @author DengXiao
 */
function pay() {
	wait();
	// 发起异步请求验证结算
	$.ajax({
		type : "post",
		timeout : 20000,
		url : "/order/check",
		error : function() {
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			close(1);
			if (-1 == res.status) {
				warning(res.message);
				return;
			}
			if (0 == res.status) {
				window.location.href = "/order/pay";
				return;
			}
			if (3 == res.status) {
				if ("支付宝" == res.title) {
					window.location.href = "/pay/alipay?id=" + res.order_id +"&type=0";
				}
				else if(res.title == "微信支付")
				{
					document.location = "WXAppPay:WX:" + res.order_id;
				}else if("银行卡" === res.title){
					window.location.href = "/pay/union?id=" +res.order_id + "&type=0";
				}
			}
		}
	});
}
