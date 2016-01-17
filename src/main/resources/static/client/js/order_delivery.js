function changeTime() {
	var time = $("#theTime").val();
	$("#date").val(time);
}

function submit() {
	// 获取所有的隐藏标签的值
	var type = $("#type").val();
	var date = $("#date").val();
	var detailTime = $("#detailTime").val();
	var diySite = $("#diySite").val();
	var limitDay = $("#limitDay").val();
	var limitId = $("#limitId").val();
	var limitTime = $("#limitTime").val();
	
	// 选择的日期不能少于最小日期
	var time = new Date(date.replace("-", "/").replace("-", "/"));
	var limit = new Date(limitDay.replace("-", "/").replace("-", "/"));

	if (time < limit) {
		warning("您能选择的最早时间为<br>" + limitTime);
		return;
	}
	
	if(time == limit){
		if (parseInt(detailTime) < parseInt(limitId)) {
			warning("您能选择的最早时间为<br>" + limitTime);
			return;
		}
	}

	window.location.href = "/order/delivery/save?type=" + type + "&date="
			+ date + "&detailTime=" + detailTime + "&diySite=" + diySite;
}
