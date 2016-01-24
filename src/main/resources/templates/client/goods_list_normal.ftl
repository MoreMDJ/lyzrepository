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
        
        <script src="/client/js/jquery-1.11.0.js" type="text/javascript"></script>
        <script src="/client/js/index.js" type="text/javascript"></script>
        <script src="/client/js/goods_list.js" type="text/javascript"></script>
    </head>
    <script type="text/javascript">
        $(function(){
            win_colo_temp();//颜色调理窗口窗口
            <#-- win_cla();//分类弹窗-->        
            footer();//底部居中
            var aBox = $('.lei_box01');
            var aBtn = $('.lei_box');
            for(var i=0;i<aBtn.length;i++){
                go(aBtn[i]);
            };
            function go(obj){
                var oBtn = obj.children[0];
                var aShow = obj.getElementsByTagName('div'); 
                var onOff = true;
                for(var i=0;i<aShow.length;i++){
                    aShow[i].style.display = 'none';
                };
                oBtn.onclick = function(){
                    if(onOff){
                        for(var i=0;i<aShow.length;i++){
//                          richMove(aShow[i],{height:61},10)
                        aShow[i].style.display = 'block';
                            console.log(0)
                        };
                    }else{
                        for(var i=0;i<aShow.length;i++){
//                          richMove(aShow[i],{height:0},10)
                            aShow[i].style.display = 'none';
                        };  
                    };
                    onOff=!onOff;
                };
            };
            
            for(var i=0;i<aBox.length;i++){
                go_go(aBox[i]);
            };
            function go_go(obj){
                var oBtn = obj.children[0];
                var aShow = obj.getElementsByTagName('dl'); 
                var onOff = true;
                oBtn.onclick = function(){
                    if(onOff){
                        for(var i=0;i<aShow.length;i++){
                            aShow[i].style.display = 'block';
                            console.log(0)
                        };
                    }else{
                        for(var i=0;i<aShow.length;i++){
                            aShow[i].style.display = 'none';
                        };  
                    };
                    onOff=!onOff;
                };
            };
        });
    </script>
    <body>
        <#-- 引入警告提示样式 -->
        <#include "/client/common_warn.ftl">
        <#-- 引入等待提示样式 -->
        <#include "/client/common_wait.ftl">   
        <div>
            <#-- 引入下单方式选择滑动窗 -->
            <#include "/client/common_shopping_type.ftl">
            <!--
                作者：rich
                描述：侧边栏 滑动
            -->
            <div id="color_package_select">
                <#include "/client/color_package.ftl">
            </div>
            <!--
                描述：调颜色
            -->
            <#include "/client/common_search_header.ftl">
            <!--
                作者：rich
                描述：头部结束
            -->
            <#if level_one_categories??>
                <#-- 遍历一级分类 -->
                <#list level_one_categories as level_one>
                    <section class="lei_box">
                        <h3 class="lei_title">${level_one.title!''}</h3>
                        <#if ("level_two_categories"+level_one_index)?eval??>
                            <#-- 遍历二级分类 -->
                            <#list ("level_two_categories"+level_one_index)?eval as level_two>
                                <div class="lei_box01">
                                    <div class="box01_title">${level_two.title!''}</div>
                                    <#if ("goods_list"+level_one_index+"_"+level_two_index)?eval??>
                                        <#-- 遍历二级分类下的所有商品 -->
                                        <#list ("goods_list"+level_one_index+"_"+level_two_index)?eval as goods>
                                            <#if goods??>
                                                <dl>
                                                    <dt>
                                                        <#-- 用户存储指定商品的库存 -->
                                                        <input type="hidden" id="inventory${goods.id?c}" value="<#if goods??&&goods.leftNumber??>${goods.leftNumber?c}<#else>0</#if>">
                                                        <#-- 商品的标题，点击可跳转到详情页 -->
                                                        <h3 onclick="window.location.href='/goods/detail/${goods.id?c}'">${goods.title!''}</h3>
                                                        <label>${goods.code!''}</label>
                                                        <#-- 判断该商品是不是属于调色商品 -->
                                                        <#if goods.isColorful??&&goods.isColorful>
                                                            <a id="color${goods.id?c}" href="javascript:changeColor(${goods.id?c});">调色</a>
                                                        </#if>
                                                    </dt>
                                                    <dd>
                                                        <#if ("priceListItem"+level_one_index+"_"+level_two_index+"_"+goods_index)?eval??>
                                                            <#if ("priceListItem"+level_one_index+"_"+level_two_index+"_"+goods_index)?eval.salePrice??>
                                                                <p>￥${("priceListItem"+level_one_index+"_"+level_two_index+"_"+goods_index)?eval.salePrice?string("0.00")}</p>
                                                            </#if>
                                                                
                                                            <#if ("priceListItem"+level_one_index+"_"+level_two_index+"_"+goods_index)?eval.isPromotion??>
                                                                <#if ("priceListItem"+level_one_index+"_"+level_two_index+"_"+goods_index)?eval.isPromotion>
                                                                    <a style="margin-right:3%;">促销</a>
                                                                </#if>
                                                            </#if>
                                                        </#if>
                                                        <div>
                                                            <span onclick="changeQuantity(${goods.id?c},'delete')">-</span>
                                                            <input class="goodsSelectedQuantity" min="0" type="number" id="quantity${goods.id?c}" value="0">
                                                            <span onclick="changeQuantity(${goods.id?c},'add')">+</span>
                                                        </div>
                                                    </dd>
                                                </dl>
                                            </#if>
                                        </#list>
                                    </#if>
                                </div>
                            </#list>
                        </#if>
                    </section>
                </#list>
            </#if>
            <div class="index_test_box03"></div>
            
            
            <div class="go_buy">
                <p onclick="window.location.href='/user/selected'">查看已选(<span id="select_num">${selected_number!'0'}</span>)</p>
                <a style="background:#ffaa00;" href="javascript:addCart();">加入已选</a>
                <a href="javascript:clearing();">去结算</a>
            </div>
            <#include "/client/common_footer.ftl">
        </div>      
    </body>
</html>
