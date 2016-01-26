<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/mag/style/idialog.css" rel="stylesheet" id="lhgdialoglink">
<title>待付款订单</title>
<script type="text/javascript" src="/mag/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="/mag/js/lhgdialog.js"></script>
<script type="text/javascript" src="/mag/js/layout.js"></script>
<link href="/mag/style/pagination.css" rel="stylesheet" type="text/css">
<link href="/mag/style/style.css" rel="stylesheet" type="text/css">
</head>
<body class="mainbody">
<form name="form1" method="post" action="/Verwalter/order/list/${statusId!"0"}" id="form1">
<div>
<input type="hidden" name="__EVENTTARGET" id="__EVENTTARGET" value="">
<input type="hidden" name="__EVENTARGUMENT" id="__EVENTARGUMENT" value="">
<input type="hidden" name="__VIEWSTATE" id="__VIEWSTATE" value="${__VIEWSTATE!""}" >
</div>

<script type="text/javascript">
var theForm = document.forms['form1'];
if (!theForm) {
    theForm = document.form1;
}
function __doPostBack(eventTarget, eventArgument) {
    if (!theForm.onsubmit || (theForm.onsubmit() != false)) {
        theForm.__EVENTTARGET.value = eventTarget;
        theForm.__EVENTARGUMENT.value = eventArgument;
        theForm.submit();
    }
}
</script>
    <!--导航栏-->
    <div class="location">
        <a href="javascript:history.back(-1);" class="back"><i></i><span>返回上一页</span></a>
        <a href="/Verwalter/center" class="home"><i></i><span>首页</span></a>
        <i class="arrow"></i>
        <a><span>订单管理</span></a>
        <i class="arrow"></i>
            <#if statusId??>
                <#if 0==statusId>
                    <span>全部订单</span>
                <#elseif 1==statusId>
                    <span>待确认订单</span>
                <#elseif 2==statusId>
                    <span>待付款订单</span>
                <#elseif 3==statusId>
                    <span>待发货订单</span>
                <#elseif 4==statusId>
                    <span>待收货订单</span>
                <#elseif 5==statusId>
                    <span>待评价订单</span>
                <#elseif 6==statusId>
                    <span>已完成订单</span>
                <#elseif 7==statusId>
                    <span>已取消订单</span>
                <#elseif 8==statusId>
                    <span>用户删除订单</span>
                </#if>
            </#if>
    </div>
    <!--/导航栏-->
    <!--工具栏-->
    <div class="toolbar-wrap">
        <div id="floatHead" class="toolbar">
            <div class="l-list">
                <ul class="icon-list">
                    <li>
                        <a class="all" href="javascript:;" onclick="checkAll(this);"><i></i><span>全选</span></a>
                    </li>
                    <#if statusId?? && 1==statusId>
                    <li>
                        <a onclick="return ExePostBack('btnConfirm','确认后将进入待发货状态，是否继续？');" class="save" href="javascript:__doPostBack('btnConfirm','')"><i></i><span>确认订单</span></a>
                    </li>
                    <#elseif statusId?? && 7==statusId || statusId?? && 8==statusId>
                    <li>
                        <a onclick="return ExePostBack('btnDelete','删除后订单将无法恢复，是否继续？');" class="del" href="javascript:__doPostBack('btnDelete','')"><i></i><span>删除订单</span></a>
                    </li>
                    </#if>
                </ul>
            </div>
            <div class="r-list">
                <input name="keywords" type="text" class="keyword">
                <a id="lbtnSearch" class="btn-search" href="javascript:__doPostBack('btnSearch','')">查询</a>
            </div>
        </div>
    </div>
    <!--/工具栏-->
    <!--列表-->
    
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ltable">
<tbody>
    <tr class="odd_bg">
        <th width="8%">
            选择
        </th>
        <th align="left">
            订单号
        </th>
        <th align="left" width="12%">
            会员账号
        </th>
        <th align="left" width="10%">
            支付方式
        </th>
        <th align="left" width="10%">
            配送方式
        </th>
        <th width="8%">
            订单状态
        </th>
        <th width="10%">
            总金额
        </th>
        <th align="left" width="16%">
            下单时间
        </th>
        <th width="8%">
            操作
        </th>
    </tr>

    <#if order_page??>
        <#list order_page.content as order>
            <tr>
                <td align="center">
                    <span class="checkall" style="vertical-align:middle;">
                        <input id="listChkId" type="checkbox" name="listChkId" value="${order_index}" >
                    </span>
                    <input type="hidden" name="listId" id="listId" value="${order.id?c}">
                </td>
                <td>
                    <a href="/Verwalter/order/edit?id=${order.id?c}&statusId=${statusId!'0'}">${order.orderNumber!""}</a></td>
                <td>${order.username!""}</td>
                <td>${order.payTypeTitle!""}</td>
                <td>${order.deliverTypeTitle!""}</td>
                <td align="center">
                    <#if order.statusId??>
                        <#if 1==order.statusId>
                            <span>待确认订单</span>
                        <#elseif 2==order.statusId>
                            <span>待付款订单</span>
                        <#elseif 3==order.statusId>
                            <span>待发货订单</span>
                        <#elseif 4==order.statusId>
                            <span>待收货订单</span>
                        <#elseif 5==order.statusId>
                            <span>待评价订单</span>
                        <#elseif 6==order.statusId>
                            <span>已完成订单</span>
                        <#elseif 7==order.statusId>
                            <span>已取消订单</span>
                        <#elseif 8==order.statusId>
                            <span>用户删除订单</span>
                        <#elseif 9==order.statusId>
                            <span>订单退货中</span>
                        <#elseif 10==order.statusId>
                            <span>退货确认</span>
                        <#elseif 11==order.statusId>
                            <span>订单退货取消</span>
                        <#elseif 12==order.statusId>
                            <span>退货完成</span>
                        </#if>
                    </#if>
                </td>
                <td align="center" width="10%">￥<font color="#C30000"><#if order??&&order.totalPrice??>${order.totalPrice?string("#.00")}<#else>0.00</#if></font></td>
                <td><#if order.orderTime??>${order.orderTime?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
                <td align="center">
                    <a href="/Verwalter/order/edit?id=${order.id?c}&statusId=${statusId!"0"}">详细</a>
                </td>
            </tr>
        </#list>
    </#if>
</tbody>
</table>
        
<!--/列表-->
<!--内容底部-->
<#if order_page??>
<#assign PAGE_DATA=order_page />
<#include "/site_mag/list_footer.ftl" />
</#if>
<!--/内容底部-->
</form>


</body></html>