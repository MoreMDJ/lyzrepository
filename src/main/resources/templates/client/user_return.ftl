<!DOCTYPE html>
<html>
	<head>
		<style type="text/css">
		</style>
		<meta name="keywords" content="">
		<meta name="description" content="">
		<meta name="copyright" content="" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
		<meta charset="utf-8">
		<title>申请退货</title>
		
		<link rel="stylesheet" type="text/css" href="/client/css/my_base.css"/>
		<link rel="stylesheet" type="text/css" href="/client/css/main.css"/>
		<link rel="stylesheet" type="text/css" href="/client/css/other.css"/>
		
		<script src="/client/js/jquery-1.11.0.js" type="text/javascript"></script>
		<script src="/client/js/rich_lee.js" type="text/javascript"></script>
	</head>
	<style type="text/css">
		.fen_goodbox{width: 100%;}
		.fen_goodbox dl:nth-of-type(1){
			margin: 0px;
		}
		.fen_goodbox dl{
			width: 90%;
			padding: 0 5%;
			background: white;
			height: 82px;
			margin-top: 10px;
			float: left;
		}
		.fen_goodbox dl dd{
			max-width: 78%;
			margin-left: 2%;
		}
		.fen_goodbox dl dd .fen_div01{
			float: left;
			width: 50%;
		}
		.fen_goodbox dl dd .fen_div01 a{
			float: left;
		}
		.fen_goodbox dl dd .fen_div01 a:nth-of-type(2){
			border-left: none;
			border-right:#DDDDDD 1px solid ;
			margin: 0px;
		}
		.fen_goodbox dl dd .fen_div01 a:nth-of-type(1){
			border-right: none;
			border-left: #DDDDDD 1px solid;
			margin: 0px;
		}
		.fen_goodbox dl dd .fen_div01 input{
			float: left;
		}
		.fen_goodbox dl dd .fen_div02{
			float: right;
			width: 50%;
			text-align: right;
		}
		.fen_goodbox dl dd .fen_div02 a{
			float: right;
		}
		.fen_goodbox dl dt{
			width: 50px;
			height: 50px;
		}
		.fen_goodbox dl dt a{
			width: 50px;
			height: 50px;
		}
		.fen_goodbox dl dt a img{
			width: 50px;
			height: 50px;
			margin: 0px;
		}
		.sub{
			width: 100%;
			height: 50px;
			background: rgb(204, 20, 33);
			color: white;
			font-size: 1.2em;
			border: none;
			position: fixed;
			left: 0px;
			bottom: 0px;
		}
	</style>
	<script type="text/javascript">
		window.onload = function(){
			(function(){
				var oHtml = document.getElementsByTagName('html')[0];
				var win_hi =document.documentElement.clientHeight;
				var doc_hi =document.documentElement.offsetHeight;
				if(doc_hi>=win_hi){
					oHtml.style.height = doc_hi + 'px';
				}else{
					oHtml.style.height = win_hi + 'px';
				};
			})();
		};
	</script>
	<body style="background: #f3f4f6;height: 100%;">
		<div class="sec_header">
			<a href="javascript:go(-1);" class="back"></a>
			<p>申请退货</p>	
			<a></a>
		</div>
	<div class="fen_goodbox">					
		<#if order??>
			<#if order.orderGoodsList??&&order.orderGoodsList?size gt 0>
				<#list order.orderGoodsList as item>
					<#if item??>
						<dl>
							<dt>
								<a>
									<img src="${item.goodsCoverImageUri!''}">
								</a>
							</dt>
							<dd>
								<p>${item.goodsTitle!''}</p>
								<div class="fen_div01">
									<a>-</a>
									<input type="text" name="" id="" value="${item.quantity!''}">
									<a>+</a>
								</div>
								<div class="fen_div02">
									<a>￥1700.00</a>
								</div>
							</dd>
						</dl>	
					</#if>
				</#list>
			</#if>
		</#if>
	<div style="float: left;width: 100%;height: 60px;"></div>
	<input class="sub" type="submit" name="" id="" value="去退货" />
</div>
	</body>
</html>
