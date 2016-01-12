function clickLevelTwo(elementId) {
	// 点击左变切换
	$('.fen_testtop ul li a').css({
		background : '#e8e8e8',
		color : '#333333'
	});
	// 是被点击的元素变颜色
	$("#" + elementId).css({
		background : '#ffaa00',
		color : 'white'
	});

	// 显示出正确的商品栏
	$(".ctrlGoods").css("display", "none");
	$("#goods" + elementId).css("display", "block");
}

function change(level_one_id) {
	var level_two = document.getElementById("level_two" + level_one_id);
	var li_arry = level_two.getElementsByTagName("li");
	var li = li_arry[0].getElementsByTagName("a")[0];
	li.click();
}
