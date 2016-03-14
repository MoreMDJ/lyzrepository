window.onload = function() {
	var oBox = document.getElementById('win_box');
	var oBtn = document.getElementById('add_btn');
	var oBack = document.getElementById('back');
	oBox.children[1].style.height = window.screen.height - 50 + 'px';
	oBox.children[1].style.background = 'rgba(0,0,0,0.5)';
	oBox.style.webkitTransition = '0';
}

/**
 * 获取用户城市下所有的行政区划的方法
 * 
 * @author dengxiao
 */
function getDistrict() {
	var oBox = document.getElementById('win_box');
	var oBtn = document.getElementById('add_btn');
	var oBack = document.getElementById('back');

	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/user/address/get",
		type : "post",
		timeout : 10000,
		data : {
			type : 0
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// 关闭等待图标
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			// 关闭等待图标
			close(100);
			$("#win_box").html(res);
			oBox.children[1].style.height = window.screen.height - 50 + 'px';
			oBox.children[1].style.background = 'rgba(0,0,0,0.5)';
			oBox.style.webkitTransition = '1s';
			oBox.style.webkitTransform = 'translateX(0)';
		}
	});
}

/**
 * 选择行政区划或行政街道的方法
 * 
 * @author dengxiao
 */
function getRegion(id, name, status) {
	var oBox = document.getElementById('win_box');
	var oBtn = document.getElementById('add_btn');
	var oBack = document.getElementById('back');
	var city = $("#user_city").val();
	// 开启等待图标
	wait();
	if (1 == status) {
		$.ajax({
			url : "/user/address/get",
			type : "post",
			timeout : 10000,
			data : {
				type : 1,
				id : id
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// 关闭等待图标
				close(1);
				warning("亲，您的网速不给力啊");
			},
			success : function(res) {
				// 关闭等待图标
				close(100);
				$("#win_box").html(res);
				oBox.children[1].style.height = window.screen.height - 50
						+ 'px';
				oBox.children[1].style.background = 'rgba(0,0,0,0.5)';
				oBox.style.webkitTransition = '1s';
				oBox.style.webkitTransform = 'translateX(0)';
				$("#add_btn").html(city + name);
			}
		});
	} else if (2 == status) {
		$.ajax({
			url : "/user/address/get",
			type : "post",
			timeout : 10000,
			data : {
				type : 2,
				id : id
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// 关闭等待图标
				close(1);
				warning("亲，您的网速不给力啊");
			},
			success : function(res) {
				var info = $("#add_btn").html();
				$("#add_btn").html(info + name);
				// 关闭等待图标
				close(100);
				windowHide();
			}
		});
	}
}

/**
 * 关闭弹窗的方法
 * 
 * @author dengxiao
 */
function windowHide() {
	var oBox = document.getElementById('win_box');
	var oBtn = document.getElementById('add_btn');
	var oBack = document.getElementById('back');
	oBox.style.webkitTransition = '1s';
	oBox.style.webkitTransform = 'translateX(100%)';
}

/**
 * 提交新的收货地址的方法
 * 
 * @author dengxiao
 */
function saveAddress() {
	var receiverName = $("#receiverName").val();
	var receiverMobile = $("#receiverMobile").val();
	var detailAddress = $("#detailAddress").val();

	var operation_type = $("#operation_type").val();
	var addressId = $("#address_id").val();
	
	if(!/^1\d{10}$/.test(receiverMobile)){
		warning("请输入正确的手机号码");
		return;
	}
	
	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/user/address/add/save",
		type : "post",
		timeout : 10000,
		data : {
			receiver : receiverName,
			receiverMobile : receiverMobile,
			detailAddress : detailAddress,
			operation : operation_type,
			addressId : addressId
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// 关闭等待图标
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			// 未登陆的情况下直接跳转到登陆页面
			if (-2 == res.status) {
				window.location.href = "/login";
				return;
			}
			// 关闭等待图标
			close(100);
			if (-1 == res.status) {
				warning(res.message);
			}
			if (0 == res.status) {
				warning("收货地址保存成功");
				setTimeout(function() {
					window.location.href = "/user/address";
				}, 1000);
			}
			if (1 == res.status) {
				warning("收货地址保存成功");
				setTimeout(function() {
					window.location.href = "/order/change/address";
				}, 1000);
			}
		}
	});
}
