<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><#if productCategory??>${productCategory.seoTitle!''}-</#if>正品惠客</title>
<meta name="keywords" content="<#if productCategory??>${productCategory.seoKeywords!''}</#if>">
<meta name="description" content="<#if productCategory??>${productCategory.seoDescription!''}</#if>">
<meta name="copyright" content="<#if site??>${site.copyright!''}</#if>" />
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />

<script src="/touch/js/jquery-1.9.1.min.js"></script>
<script src="/touch/js/common.js"></script>

<link href="/touch/css/base.css" rel="stylesheet" type="text/css" />
<link href="/touch/css/front.css" rel="stylesheet" type="text/css" />

<script type="text/javascript">
$(document).ready(function(){
  
});

var pageIdx = 1;
function loadMore()
{
    $.ajax({
        type:"post",
        url:"/touch/list/more/${categoryId!'1'}-${orderId!'0'}-${soldId!'0'}-${priceId!'0'}-${timeId!'0'}-" + pageIdx,
        success:function(data){
            if ("" == data)
            {
                $("#a-more").css("display", "none");
            }
            else
            {
                $("#goods-menu").append(data);
                pageIdx++;
            }
        }
    });
}
</script>
</head>

<body>
<div class="maintop_bg"></div>
<header class="maintop">
  <div class="main">
  	<#-- 以下为搜索功能的输入框 -->
    <label class="list_search_all"><input type="text" class="list_search" /><a href="#" class="list_search_a"></a></label>
    <a class="a1" href="javascript:history.go(-1)"><img src="images/back.png" height="22" /></a>
    <a class="a2" href="#"><img src="images/menu.png" height="22" /></a>
  </div>
</header>

<div class="list_top">
<table class="main">
  <tr>
    <a <#if orderId==0>class="sel"</#if> href="/touch/list/${categoryId!'1'}-0-<#if soldId?? && soldId==0>1<#else>0</#if>-${priceId!'0'}-${timeId!'0'}-${pageId!'0'}"><p>销量</p></a>
    <a <#if orderId==1>class="sel"</#if> href="/touch/list/${categoryId!'1'}-1-${soldId!'0'}-<#if priceId?? && priceId==0>1<#else>0</#if>-${timeId!'0'}-${pageId!'0'}"><p>价格</p></a>
    <a <#if orderId==2>class="sel"</#if> href="/touch/list/${categoryId!'1'}-2-${soldId!'0'}-${priceId!'0'}-<#if timeId?? && timeId==0>1<#else>0</#if>-${pageId!'0'}"><p>上架时间</p></a>
    <td><a href="#"></a></td>
  </tr>
</table>
</div>
<div class="clear50"></div>
<div class="main">
 
  <a class="phone_list" href="#">
  	 <#--
  	 <#if goods_page??>
        <#list goods_page.content as item>
            <a href="/touch/goods/${item.id!''}">
                <b><img src="${item.coverImageUri!''}" /></b>
                <p class="p1">${item.title!''}</p>
                <p class="p2">${item.subTitle!''}</p>
                <p class="p2">评论：<span class="red">${item.totalComments!'0'}</span>人</p>
                <p class="red p3">￥<#if item.salePrice??>${item.salePrice?string("0.00")}</#if><span class="unl-lt c9">￥<#if item.marketPrice??>${item.marketPrice?string("0.00")}</#if></span></p>
                <div class="clear"></div>
            </a>
        </#list>
    </#if>
 	--> 	
 	<#if goods_page??>
	 	<#list goods_page.content as item>
	    <b><img src="images/front/img01.png" /></b>
	    <p class="p1">${item.title!''}</p>
	    <p>${item.subTitle!''}</p>
	    <p class="p2"><label class="p2_l1">￥<span>5${item.marketPrice}</span></label><label class="p2_l2">￥<span>${item.salePrice}</span></label></p>
	    <div class="clear"></div>
	    <h6><span>已售 </span>${item.soldNumber}</h6>
	 	</a>
	  	<div class="clear20"></div>
	  </#list>
  </#if>
</div>
<!--main END-->
</body>
</html>
