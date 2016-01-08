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
        <link rel="stylesheet" type="text/css" href="/client/css/other.css"/>
        <link rel="stylesheet" type="text/css" href="/client/css/my_base.css"/>
        <link rel="stylesheet" type="text/css" href="/client/css/x_common.css"/>
        <link rel="stylesheet" type="text/css" href="/client/css/x_my_wealth.css"/>
        <!-- js -->
        <script src="/client/js/jquery-1.11.0.js" type="text/javascript"></script>
        <script src="/client/js/index.js" type="text/javascript"></script>
        <script type="text/javascript">
            $(function(){
                win_cla();
                footer();
            });
        </script>
    </head>
    <body class="bgc-f3f4f6">
        <#-- 引入公共购物方式选择滑动窗口 -->
        <#include "/client/common_shopping_type.ftl">
        
        <!-- 头部 -->
        <header>
            <a class="back" href="javascript:history.go(-1);"></a>
            <p>余额充值</p>
            <a class="save" href="#">使用说明</a>
        </header>
        <!-- 头部 END -->
        
        <!-- 充值 -->
        <article class="balance-recharge">
            <section class="recharge">
                <div>
                    <span class="sp1">充值</span>
                    <input type="text" placeholder="请输入充值金额">
                    <span class="sp2">元</span>
                </div>
            </section>
            <!-- 充值方式 -->
            <section class="pay-way">
                <div class="paylist">
                    <div class="left">
                        <img class="way" width="78" height="40" src="/client/images/x_icon_zhifubao.png" alt="支付宝支付">
                    </div>
                    <div class="zfb">
                        <div class="div1">支付宝</div>
                        <div class="div2">推荐有支付宝账号的用户使用</div>
                    </div>
                    <div class="right">
                        <img width="30" height="30" class="check" src="/client/images/x_icon_checked.png" alt="">
                    </div>
                </div>
                <div class="paylist">
                    <div class="left">
                        <img class="way" width="78" height="40" src="/client/images/x_icon_weixinzhifu.png" alt="微信支付">
                    </div>
                    <div class="zfb">
                        <div class="div1">微信支付</div>
                        <div class="div2">推荐有微信钱包账号的用户使用</div>
                    </div>
                    <div class="right">
                        <img width="30" height="30" class="check" src="/client/images/x_icon_check.png" alt="">
                    </div>
                </div>
                <div class="paylist">
                    <div class="left">
                        <img class="way" width="78" height="40" src="/client/images/x_icon_yinhangka.png" alt="银行卡支付">
                    </div>
                    <div class="zfb">
                        <div class="div1">银行卡</div>
                        <div class="div2">推荐有银联银行卡账户的用户使用</div>
                    </div>
                    <div class="right">
                        <img width="30" height="30" class="check" src="/client/images/x_icon_check.png" alt="">
                    </div>
                </div>
            </section>
            <a class="btn-next" href="#">下一步</a>
        </article>
        <script type="text/javascript">
            $(".pay-way .paylist").click(function(){
                $(this).find("img.check").attr("src","/client/images/x_icon_checked.png");
                $(this).siblings().find("img.check").attr("src","/client/images/x_icon_check.png");
            });
        </script>
        <!-- 充值 END -->
        <div class="clear h66"></div>
        <!-- 底部 -->
        <#include "/client/common_footer.ftl">
        <!-- 底部 END -->
    </body>
</html>