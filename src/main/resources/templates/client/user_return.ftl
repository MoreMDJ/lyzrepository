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
		<script src="/client/js/angular.js" type="text/javascript"></script>
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
		var data = "";
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
		var app = angular.module("app",[]);
		var ctrl = app.controller("ctrl",function($scope,$http){
			$scope.goods = [
				<#if order.orderGoodsList??&&order.orderGoodsList?size gt 0>
					<#list order.orderGoodsList as item>
						<#if item??>
							{
								id : ${item.goodsId?c},
								title : "${item.goodsTitle!''}",
								img : "${item.goodsCoverImageUri!''}",
								quantity : ${item.quantity!''},
								reQuantity : ${item.quantity!''},
								<#if item.goodsId??&&("price"+item.goodsId?c)?eval??>
									price : ${("price"+item.goodsId?c)?eval?string("0.00")},
								<#else>
									price : 0.00,
								</#if>
								<#if item.goodsId??&&("unit"+item.goodsId?c)?eval??>
									unit : ${("unit"+item.goodsId?c)?eval?string("0.00")},
									total : this.quantity * this.unit
								<#else>
									unit : 0.00,
									total : 0.00
								</#if>
							}
							<#-- 判断是否添加逗号 -->
							<#if item_index!=order.orderGoodsList?size-1>
								,
							</#if>
						</#if>
					</#list>
				</#if>
			]
					
			$scope.delete = function(index){
				var number = this.goods[index].reQuantity;
				if(number > 0){
					this.goods[index].reQuantity -= 1;
					this.goods[index].total -= this.goods[index].unit;
				}
			}
			$scope.add = function(index){
				var number = this.goods[index].reQuantity;
				if(number < this.goods[index].quantity){
					this.goods[index].reQuantity += 1;
					this.goods[index].total += this.goods[index].unit;
				}
			}
			$scope.send = function(){
				data = "";
				for(var i = 0; i < this.goods.length; i++){
					if(0 !== this.goods[i].reQuantity){
						data += (this.goods[i].id + "-" + this.goods[i].reQuantity + "-" + this.goods[i].unit + "-" + this.goods[i].price + ",");
					}
				}
				if("" === data){
					return;
				}
				wait();
				$.ajax({
					type:"post",
					url:"/user/return/check",
					data:{
						orderId : ${order.id?c},
						infos : data
					},
					error:function(){
						close(1);
						warning("亲，您的网速不给力啊");
					},
					success:function(res){
						if(0 === res.status){
							window.location.href="/user/order/0";
						}
					}
				});
			}
		});
		
	</script>
	<body style="background: #f3f4f6;height: 100%;" ng-app="app">
		<#-- 引入警告提示样式 -->
        <#include "/client/common_warn.ftl">
        <#-- 引入等待提示样式 -->
       	<#include "/client/common_wait.ftl">  
		<div class="sec_header">
			<a href="javascript:history.go(-1);" class="back"></a>
			<p>申请退货</p>	
			<a></a>
		</div>
		<div class="fen_goodbox" ng-controller="ctrl">					
			<dl ng-repeat="item in goods">
				<dt>
					<a>
						<img src="{{item.img}}">
					</a>
				</dt>
				<dd>
					<p>{{item.title}}</p>
					<div class="fen_div01">
						<a ng-click="delete($index);">-</a>
						<input type="text" name="" id="{{$index}}" ng-model="item.reQuantity">
						<a ng-click="add($index);">+</a>
					</div>
					<div class="fen_div02">
							<a>￥{{item.unit}}</a>
					</div>
				</dd>
			</dl>	
			<div style="float: left;width: 100%;height: 60px;"></div>
			<input class="sub" type="button" ng-click="send();" value="去退货" />
		</div>
	</body>
</html>
