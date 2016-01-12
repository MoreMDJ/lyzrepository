<!DOCTYPE html>
<html lang="zh-CN" class="bgc-f3f4f6">
<head>
<meta charset="UTF-8">
<meta name="keywords" content="">
<meta name="copyright" content="" />
<meta name="description" content="">
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<title>乐易装</title>
<!-- css -->
<link rel="stylesheet" type="text/css" href="/client/css/my_base.css"/>
<link rel="stylesheet" type="text/css" href="/client/css/x_common.css"/>
<link rel="stylesheet" type="text/css" href="/client/css/x_gu_sales.css"/>
<!-- js -->
<script type="text/javascript" src="/client/js/jquery-1.11.0.js"></script>
</head>
<body class="bgc-f3f4f6">
  <!--弹窗-->
  <div id="bg"></div>
  <div id="popbox">
    <div class="time-select">
      <div>开始时间：<input type="date" id="start" min="2015-12-04" value="<#if startDate??>${startDate?string("yyyy-MM-dd")}</#if>"></div>
      <div>结束时间：<input type="date" id="end" min="2015-12-04"  value="<#if endDate??>${endDate?string("yyyy-MM-dd")}</#if>"></div>
      <a class="btn-sure-time" href="javascript:;" onclick="pupclose()">确定</a>
    </div>    
  </div>
  <script type="text/javascript">
    $("#bg").height($(window).height());
    function pupopen(){
      document.getElementById("bg").style.display="block";
      document.getElementById("popbox").style.display="block" ;
    }
    function pupclose(){
      document.getElementById("bg").style.display="none";
      document.getElementById("popbox").style.display="none" ;
      window.location.href="/delivery?start=" + document.getElementById("start").value + "&end=" + document.getElementById("end").value;
    }
  </script>
  <!--弹窗 END-->
  <!-- 头部 -->
  <header>
    <a class="back" href="#"></a>
    <div class="date-group">
      <a <#if days?? && days!=7>class="active"</#if> href="/delivery?days=3">三天内</a>
      <a <#if days?? && days==7>class="active"</#if> href="/delivery?days=7">七天内</a>
      <a <#if startDate?? || endDate??>class="active"</#if> class="btn-filter" href="javascript:;" onclick="pupopen()">筛选</a>
    </div>
  </header>
  <!-- 头部 END -->

  <!-- 详情列表 -->
  <article class="look-details-list">
    <ul>
      <li <#if type?? && type==1>class="active"</#if>><a href="/delivery?type=1<#if days??>&days=${days}</#if><#if startDate??>&start=${startDate?string("yyyy-MM-dd")}</#if><#if endDate??>&end=${endDate?string("yyyy-MM-dd")}</#if>">已配送（<#if order_list??>${order_list?size}<#else>0</#if>）</a></li>
      <li <#if type?? && type==2>class="active"</#if>><a href="/delivery?type=2<#if days??>&days=${days}</#if><#if startDate??>&start=${startDate?string("yyyy-MM-dd")}</#if><#if endDate??>&end=${endDate?string("yyyy-MM-dd")}</#if>">配送中（<#if order_list??>${order_list?size}<#else>0</#if>）</a></li>
      <li <#if type?? && type==3>class="active"</#if>><a href="/delivery?type=3<#if days??>&days=${days}</#if><#if startDate??>&start=${startDate?string("yyyy-MM-dd")}</#if><#if endDate??>&end=${endDate?string("yyyy-MM-dd")}</#if>">待配送（<#if order_list??>${order_list?size}<#else>0</#if>）</a></li>
    </ul>
    <!-- 详情列表 -->
    
    <#if order_list??>
    	<#list order_list as item>
    		<section>
		      <a href="/delivery/detail/${item.id?c}">
		      	<#if statusId==3 || statusId==4>
		        	<div class="time">【预计 ${item.deliveryDate!''} <span>12:00</span> 送达】</div>
	        	<#elseif statusId==5 || statusId==6>
	        		<div class="time">【<#if item.deliveryTime??>${item.deliveryTime?string("yyyy-MM-dd")}</#if> <span><#if item.deliveryTime??>${item.deliveryTime?string("HH:mm")}</#if></span> 送达】</div>
		        </#if>
		        <div class="address">收货地址：${item.shippingAddress!''}</div>
		      </a>
		    </section>
    	</#list>
    </#if>
  </article>
  <!-- 详情列表 END -->

  <div class="clear h66"></div>


</body>
</html>