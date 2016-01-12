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
        <link rel="stylesheet" type="text/css" href="/client/css/x_account_settings.css"/>
        
        <script src="/client/js/jquery-1.11.0.js" type="text/javascript"></script>
        <script src="/client/js/user_diy_site.js" type="text/javascript"></script>
        <style>
            input{
                -webkit-appearance:none;
            }
            
            select{
                -webkit-appearance:none;
            }
        </style>
    </head>
    <body class="bgc-f3f4f6">
        <!--弹窗-->
        <div id="bg"></div>
        <div id="pop-up">
            <div id="diy_title" class="div1">金科云湖天都自提柜</div>
            <div class="div2">门店属性</div>
            <div id="diy_attr" class="div3">直营</div>
            <div class="div2">详细地址</div>
            <div id="diy_address" class="div3">重庆是九龙坡区科园一路（轻轨3号线石桥铺站）</div>
            <div class="div2">电话号码</div>
            <div id="diy_phone" class="div3">15213266666<a href="#">（点击拨打）</a></div>
            <div class="div4">
                <a class="close" onclick="pupclose()">关闭</a>
                <a id="diy_check" class="select" onclick="pupclose()">选择</a>
            </div>
        </div>
         <!--弹窗 END-->
    
        <#-- 引入警告提示样式 -->
        <#include "/client/common_warn.ftl">
        <#-- 引入等待提示样式 -->
        <#include "/client/common_wait.ftl">  
        <!-- 头部 -->
        <header>
            <a class="back" href="javascript:history.go(-1);"></a>
            <p>门店归属</p>
        </header>
        <!-- 头部 END -->
        
        <!-- 门店归属 -->
        <article class="home-stores">
            <#if user??>
                <div>当前所属门店：<span id="now_diy">${user.diyName!''}</span></div>
            </#if>
            <#if !(isSelected??&&isSelected==false)>
                <div>
                    <select id="site_district" onchange="changeDistrict();" style="width:98%;height:35px;font-size:0.9em;color:#666;margin:0 1%;border:none;background:url('/client/images/icon_bottom.png') no-repeat right;">
                        <#if district_list??>
                            <#list district_list as item>
                                <option value="${item.id?c}">${item.name!''}</option>
                            </#list>
                        </#if>
                    </select>
                </div>
                <span id="site_by_district" style="margin-top:10px;display:block;">
                    <#include "/client/site_in_district.ftl">
                </span>
            </#if>
        </article>
        <!-- 门店归属 END -->
        
    </body>
</html>