<!DOCTYPE html>
<html>
	<head>
		<meta name="keywords" content="">
		<meta name="description" content="">
		<meta name="copyright" content="" />
		<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
		<meta charset="utf-8">
		<title>乐易装首页</title>
		
		<link rel="stylesheet" type="text/css" href="/client/css/my_base.css"/>
		<link rel="stylesheet" type="text/css" href="/client/css/main.css"/>
		<link rel="stylesheet" type="text/css" href="/client/css/other.css"/>
		
		<script src="/client/js/goods_list.js" type="text/javascript"></script>
		<script src="/client/js/jquery-1.11.0.js" type="text/javascript"></script>
		<script src="/client/js/rich_lee.js" type="text/javascript"></script>
	</head>
	<style>
	   .isCollectTrue{
	       background: url(/client/images/sec_footericon03.png) no-repeat center;
	   }
	   .isCollectFalse{
	       background: url(/client/images/sec_footericon02.png) no-repeat center;
	   }
	</style>
	<script type="text/javascript">
		$(function(){
			turn_hei($('.det_banner'),0.8) 
			$('.det_banner img').width($(window).width())
			win_colo($('.det_list .li03 p'),${goods.id?c});//颜色调理窗口窗口
		});
	</script>
	<body>
		<div>
            <#-- 引入警告提示样式 -->
            <#include "/client/common_warn.ftl">
            <#-- 引入等待提示样式 -->
            <#include "/client/common_wait.ftl">
            
            <div id="color_package_select">
                <#include "/client/color_package.ftl">            
            </div>
			
			<div class="det_banner">
			    <#if goods??&&goods.showPictures??>
    				<div class="det_scroll">
    				    <#list goods.showPictures?split(",") as pic>
    					   <img src="${pic!''}">
    					</#list>
    				</div>
				</#if>
    			<div class="det_title">
    				<span onclick="javascript:history.go(-1);"></span>
    				<span></span>
    			</div>
    			<#if goods??&&goods.showPictures??>
    				<div class="dtbanner_btn">
    					<ul>
    					   <#list goods.showPictures?split(",") as pic>
						      <li></li>
    					   </#list>
    					</ul>
    				</div>
				</#if>
			</div>
			<div class="index_test_box"></div>
			<section class="det_content">
				<div class="det_pri">
				    <#if goods??>
    					<p>${goods.name!''}</p>
    					<label>${goods.code!''}</label>
    					<p>￥<#if priceListItem??&&priceListItem.salePrice??>${priceListItem.salePrice?string("0.00")}<#else>0.00</#if></p>
    					<p>销量：${goods.soldNumber!'0'}件</p>
    					<#-- 该标签用以存储库存 -->
    					<input type="hidden" id="inventory${goods.id?c}" value="<#if goods.leftNumber??>${goods.leftNumber?c}<#else>0</#if>">
    					<p>库存：<#if goods.leftNumber??>${goods.leftNumber?c}<#else>0</#if>件</p>
					</#if>
				</div>
				<div class="index_test_box"></div>
				<ul class="det_list">
					<li class="li01">
						<div>品牌：</div>
						<p><#if goods.belongTo??&&goods.belongTo==1>华润<#else>乐易装</#if></p>
					</li>
				    <#--
					<li class="li02">
						<div>促销：</div>
						<p>金钻雅家五合一PU清底漆*3 + 金钻雅家五合一PU清底漆* 2 + 金钻雅家五合一PU清底漆 * 1   赠送   金钻雅家五合一PU清底漆 * 3</p>
						<div></div>
						<p>满<a href="#">399</a>，赠墙面漆一桶</p>
					</li>
					-->
					<li class="li05"></li>
					<li class="li03">
						<div>数量：</div>
						<a onclick="changeQuantity(${goods.id?c},'delete')">-</a>
						<input type="text" class="goodsSelectedQuantity" id="quantity${goods.id?c}" value="${select_quantity!'0'}" />
						<a onclick="changeQuantity(${goods.id?c},'add')">+</a>
						<#if goods??&&goods.isColorful??&&goods.isColorful>
                            <p>调色</p>
						</#if>
					</li>
					<#--
					<li class="li04">
						<div>送至：</div>
						<p>重庆市 渝中区 解放碑</p>
					</li>
					<li class="li04">
						<div>服务：</div>
						<p>${goods.service!''}</p>
					</li>
                   -->
				</ul>
				<div class="index_test_box"></div>
				<dl class="det_text">
				    <#--
					<dt>商品参数</dt>
					<dd>
						<span>品牌</span>
						<p>${goods.brandTitle!''}</p>
					</dd>
					<dd>
						<span>产地</span>
						<p>法国</p>
					</dd>
					<dd>
						<span>发货地</span>
						<p>
						  <#if priceListItem??>
    					      ${priceListItem.dispatch!''}
						  </#if>
						</p>
					</dd>
					-->
				</dl>
				<#if activity_list??>
    				<div class="det_mes"  style="margin-bottom:10px;">
                        <div class="det_mestitle">促销活动</div>
                        <ul>
                            <#list activity_list as item>
                                <li>
                                    <span style="width:96%; padding:5px 3%; display:block;">${item.title!''}</span>
                                    <img style="width:100%;" src="${item.activityImg!''}"></img>
                                </li>
                            </#list>
                        </ul>
                    </div>
                </#if>
				<div class="index_test_box"></div>
			    <#if goods.detail??>
    				<div class="det_img">${goods.detail!''}</div>
				</#if>
				<div class="det_mes">
					<div class="det_mestitle">宝贝评价<span>（<#if user_comment_list??>${user_comment_list?size}<#else>0</#if>）</span></div>
					<#if user_comment_list??&&user_comment_list?size gt 0>
		                <#list user_comment_list as comment>
        					<dl class="det_mes_box">
        						<dt>
        							<div><img src="${comment.userHeadUri!''}"></div>
        							<span>${comment.username!''}</span>
        						</dt>
        						<dd>
        							<p>${comment.content!''}</p>
        							<#--
            							<div>
            								<img src="/client/images/det_banner01.png" />
            							</div>
        							-->
        							<span>
        							    <#if comment.stars??>
                                            <#list 1..comment.stars as star>
            								    <img src="/client/images/det_star.png">
            								</#list>
    									</#if>
        							</span>
        							<a><#if comment.commentTime??>${comment.commentTime?string("yyyy-MM-dd")}</#if></a>
        						</dd>
        					</dl>
    					</#list>
					</#if>
				</div>
				<ul class="sec_footer">
					<li>
						<span><a style="color:white;" href="tel://${phone!''}">客服</a></span>
					</li>
					<li>
                        <#if isCollect??&&isCollect>
					       <span id="operate" class="isCollectTrue" onclick="operateCollect(<#if goods??&&goods.id??>${goods.id?c}</#if>)">收藏</span>
				        <#else>
				           <span id="operate" class="isCollectFalse" onclick="operateCollect(<#if goods??&&goods.id??>${goods.id?c}</#if>)">收藏</span>
						</#if>
					</li>
					<li>
						<span onclick="addCart();">加入已选</span>
					</li>
					<li>
						<span onclick="buyNow(${goods.id?c});">立刻购买</span>
					</li>	
				</ul>
			</section>
		</div>		
	</body>
</html>
