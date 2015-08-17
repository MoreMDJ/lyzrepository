<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><#if site??>${site.seoTitle!''}-</#if>我的关注</title>
<meta name="keywords" content="${site.seoKeywords!''}">
<meta name="description" content="${site.seoDescription!''}">
<meta name="copyright" content="${site.copyright!''}" />
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />

<script src="/touch/js/jquery-1.9.1.min.js"></script>
<script src="/touch/js/common.js"></script>
<link href="/touch/css/base.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="/touch/css/follow.css"/>
</head>

<body>
<div class="maintop_bg"></div>
<header class="maintop">
  <div class="main">
    <p>我的关注</p>
    <a class="a1" href="javascript:history.go(-1);"><img src="/touch/images/back.png" height="22" /></a>
    <a class="a2" href="/touch"><img src="/touch/images/home.png" height="22" /></a></div>
</header>

 <div class="main">
  <#if collect_page??>
    <#list collect_page.content as cg>
        <a href="/touch/goods/${cg.goodsId}">
            <b><img src="${cg.goodsCoverImageUri}" /></b>
            <p>${cg.goodsTitle!''}<span class="sp1">￥${cg.goodsSalePrice?string("0.00")}</span></p>
            <p class="p1">${cg.goodsSubTitle!''}</p>
            <div class="clear"></div>
        </a>
    </#list>
</#if>
</div>
  
<!--main END-->


</body>
</html>
