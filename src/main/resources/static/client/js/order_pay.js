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
	console.debug(classes);
	if(classes.indexOf("active") != -1){
		userCash = true;
	}
	
	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/check",
		type : "post",
		timeout : 10000,
		data:{
			userCash:userCash
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