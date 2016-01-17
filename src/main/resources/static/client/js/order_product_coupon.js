window.onload = function(){
	$(".select-coupons .check").height($(".select-coupons .coupon").height());
}
$(function() {
	$(".select-coupons section").click(
		function() {
			var cssattr = $(this).find("div.check").attr("class");
			var id = $(this).attr("id");
			// 开启等待图标
			wait();
			if (cssattr.indexOf("checked") > 0) {
				$.ajax({
					url : "/order/operate/coupon",
					type : "post",
					timeout : 15000,
					data : {
						id : id,
						type : 1,
						status : 1
					},
					error:function(XMLHttpRequest, textStatus, errorThrown) {
						// 关闭等待图标
						close(1);
						warning("亲，您的网速不给力啊");
					},
					success(res){
						close(100);
						if(0 == res.status){
							history.go(0);
						}else{
							warning(res.message);
						}
					}
				});
			} else {
				$.ajax({
					url : "/order/operate/coupon",
					type : "post",
					timeout : 15000,
					data : {
						id : id,
						type : 1,
						status : 0
					},
					error:function(XMLHttpRequest, textStatus, errorThrown) {
						// 关闭等待图标
						close(1);
						warning("亲，您的网速不给力啊");
					},
					success(res){
						close(100);
						if(0 == res.status){
							history.go(0);
						}else{
							warning(res.message);
						}
					}
				});
			}
	});
});