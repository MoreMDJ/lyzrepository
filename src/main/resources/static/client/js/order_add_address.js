function changeDistrict() {
	var districtId = $("#district").val();
	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/change/district",
		type : "post",
		timeout : 10000,
		data : {
			districtId : districtId
		},
		error : function() {
			// 关闭等待图标
			close(1);
			warning("亲，您的网速不给力啊");
		},
		success : function(res) {
			// 关闭等待图标
			close(100);
			$("#subdistrict").html(res);
		}
	});
}

/**
 * 确认提交的方法
 * 
 * @author dengxiao
 */
function saveAddress() {
	var receiveName = $("#receiveName").val();
	var receiveMobile = $("#receiveMobile").val();
	var district = $("#district").val();
	var subdistrict = $("#subdistrictId").val();
	var detail = $("#detail").val();

	if (null==receiveName||"" == receiveName) {
		warning("请填写收货人的姓名");
		return;
	}

	if (null==receiveMobile||"" == receiveMobile) {
		warning("请填写收货人的联系电话");
		return;
	}

	if ( null==district||"" == district) {
		warning("请选择收货人所在的行政区划");
		return;
	}

	if (null==subdistrict||"" == subdistrict) {
		warning("请选择收货人所在的行政街道");
		return;
	}

	if (null==detail||"" == detail) {
		warning("请填写收货人的详细地址");
		return;
	}
	
	// 开启等待图标
	wait();

	// 发送异步请求
	$.ajax({
		url : "/order/new/address",
		type : "post",
		timeout : 10000,
		data : {
			receiveName : receiveName,
			receiveMobile : receiveMobile,
			district : district,
			subdistrict : subdistrict,
			detail : detail
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
					window.location.href = "/order";
				}, 1000);
			}
		}
	});

//	window.location.href = "/order/new/address?receiveName=" + receiveName
//			+ "&receiveMobile=" + receiveMobile + "&district=" + district
//			+ "&subdistrict=" + subdistrict + "&detail=" + detail;
}
