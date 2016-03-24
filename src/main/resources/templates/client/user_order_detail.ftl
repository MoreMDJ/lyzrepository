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
        <link rel="stylesheet" type="text/css" href="/client/css/x_order_manage.css"/>
    </head>
    <body class="bgc-f3f4f6">
        <!-- 头部 -->
        <header>
            <a class="back" href="javascript:history.go(-1);"></a>
            <p>订单详情</p>
        </header>
        <!-- 头部 END -->
        
        <!-- 订单详情 -->
        <#if order??>
            <article class="order-details">
                <!-- 用户信息 -->
                <#if order.deliverTypeTitle!='门店自提'>
                <div class="user-info">
                    <div class="div1">
                        <div class="div1-1">收货人: <span>${order.shippingName!''}</span></div>
                        <div class="div1-2">电话：<span>${order.shippingPhone!''}</span></div>
                    </div>
                    <div class="div2">收货地址：<span>${order.shippingAddress!''}</span></div>
                </div>
                </#if>
                <!-- 订单列表 -->
                <ol class="order-list">
                    <li class="li1">
                        <label>订单号：<span>${order.orderNumber!''}</span></label>
                        <div class="species c-ff8e08">
                            <#if order.statusId??>
                                <#switch order.statusId>
                                    <#case 2>待付款<#break>
                                    <#case 3>待发货<#break>
                                    <#case 4>待签收<#break>
                                    <#case 5>待评价<#break>
                                    <#case 6>已完成<#break>
                                    <#case 7>已取消<#break>
                                </#switch>
                            </#if>
                        </div>
                    </li>
                    <li>
                        <#if order.remarkInfo??>
                            <label>提示：<span>${order.remarkInfo!''}</span></label>
                        </#if>
                    </li>
                    <#if order.orderGoodsList??>
                        <#list order.orderGoodsList as item>
                            <li class="li2 bdbt-n">
                                <div class="img"><img src="${item.goodsCoverImageUri!''}" alt="产品图片"></div>
                                <div class="product-info">
                                    <div class="div1">${item.goodsTitle!''}</div>
                                    <div class="div1">${item.sku!''}</div>
                                    <div class="div2">￥<span><#if item.price??>${item.price?string("0.00")}<#else>0.00</#if></span><label>数量：<span>${item.quantity!'0'}</span></label></div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                    <!-- 赠品 -->
                    <#if order.presentedList??>
                        <#list order.presentedList as item>
                            <li class="li2 bdbt-n">
                                <div class="img"><img src="${item.goodsCoverImageUri!''}" alt="产品图片"></div>
                                <div class="product-info">
                                    <div class="div1">${item.goodsTitle!''}<span style="color:red">【赠品】</span></div>
                                    <div class="div1">${item.sku!''}</div>
                                    <div class="div2">￥<span><#if item.price??>${item.price?string("0.00")}<#else>0.00</#if></span><label>数量：<span>${item.quantity!'0'}</span></label></div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                    <#if order.presentedList??>
                        <#list order.giftGoodsList as item>
                            <li class="li2 bdbt-n">
                                <div class="img"><img src="${item.goodsCoverImageUri!''}" alt="产品图片"></div>
                                <div class="product-info">
                                    <div class="div1">${item.goodsTitle!''}<span style="color:red">【小辅料】</span></div>
                                    <div class="div1">${item.sku!''}</div>
                                    <div class="div2">￥<span><#if item.price??>${item.price?string("0.00")}<#else>0.00</#if></span><label>数量：<span>${item.quantity!'0'}</span></label></div>
                                </div>
                            </li>
                        </#list>
                    </#if>
                    <li class="li5">支付方式：${order.payTypeTitle!''}<div class="div1">实付款：<p>￥<span><#if order.actualPay??>${order.actualPay?string("0.00")}<#else>0.00</#if></span></p></div></li>
                    <li class="li5" style="overflow: visible;height: auto;">订单备注：${order.remark!''}</li>
                    <li class="li5">服务导购：${order.sellerRealName!''}</li>
                    <li class="li5">使用现金券：<div class="div1">券金额：<p>￥<span><#if order.cashCoupon??>${order.cashCoupon?string("0.00")}<#else>0.00</#if></span></p></div></li>
                    <li class="li6"><span>使用产品券：</span><div class="div1"><p>${order.productCoupon!''}</p></div></li>
                    <style type="text/css">
                        .li6 {
                            height: auto;
                            line-height: 40px;
                            color: #999;
                            border-top: 1px solid #ddd;
                        }
                        .li6 > span {width:30%;float:left;}
                        .li6 > .div1 {width:70%; float:left; color:#666;}
                    </style>
                    <li class="li5">配送人：<#if opUser??>${opUser.realName!''}</#if><#if opUser??&&opUser.username??>(<a href="tel:${opUser.username!''}"></a>)</#if></li>
               		<li class="li5">配送仓库：<#if tdWareHouse??>${tdWareHouse.whName!''}</#if></li>
                </ol>   
            </article>
            <!-- 订单详情 END -->
                    
            <!-- 配送信息 -->
            <article class="delivery-info">
                <#if order.statusId==4>
                <div class="title">
                    <span>配送信息</span>
                    <a href="/user/order/map?oid=${order.id?c}" style="float:right;margin-right:10px;font:1em;color:#999999;" >查看地图</a>
                </div>
                <!-- 物流信息 -->
                <#--<div class="estimated-time">预计到达时间：2015-11-30</div>-->
                    <ul class="delivery-pro">
                        <li class="active">
                            <div class="progress-pic"></div>
                            <div class="progress-ifo">
                                <div>已出库</div>
                                <div></div>
                            </div>
                        </li>
                        <#if tdUser??>
                            <li class="last">
                                <div class="progress-pic"></div>
                                <div class="progress-ifo">
                                    <div>${geoInfo.formattedAddress!''}——${tdUser.realName!''}（${tdUser.username!''}）正在派件</div>
                                    <div>${geoInfo.time?string("0.00")}</div>
                                </div>
                            </li>
                        </#if>
                    </ul>
                </#if>
            </article>
            <!-- 配送信息 END -->
        </#if>
    </body>
</html>