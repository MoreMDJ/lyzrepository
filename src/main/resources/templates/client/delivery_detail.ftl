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
<link rel="stylesheet" type="text/css" href="css/my_base.css"/>
<link rel="stylesheet" type="text/css" href="css/x_common.css"/>
<link rel="stylesheet" type="text/css" href="css/x_gu_sales.css"/>
<!-- js -->
<!-- <script type="text/javascript" src="js/jquery-1.11.3.min.js"></script> -->
</head>
<body class="bgc-f3f4f6">
  <!--弹窗-->
  <div id="bg"></div>
  <div id="arreabox">
    <form>
      <div class="title">申请欠款</div>   
      <div class="text1">已交款<input type="text">元</div>
      <div class="text1">欠款&nbsp;&nbsp;<input type="text">元</div>
      <div class="button-group">
        <a class="sure" href="#" onclick="pupclose()">关闭</a>
        <a class="cancle" href="#" onclick="pupclose()">提交</a>
      </div> 
    </form>
  </div>
  <script type="text/javascript">
    // $("#bg").height($(window).height());
    function pupopen(){
      document.getElementById("bg").style.display="block";
      document.getElementById("arreabox").style.display="block" ;
    }
    function pupclose(){
      document.getElementById("bg").style.display="none";
      document.getElementById("arreabox").style.display="none" ;
    }
  </script>
  <!--弹窗 END-->

  <!-- 头部 -->
  <header>
    <a class="back" href="#"></a>
    <p>详情产看</p>
  </header>
  <!-- 头部 END -->

  <!-- 详情查看 -->
  <article class="look-details">
    <!-- 配送详情 -->
    <section>
      <div class="title">配送详情</div>
      <div class="content">
        <div class="mesg">预计送达时间：2015-12-25 9:00</div>
        <div class="mesg">收货人姓名：秦（先生）</div>
        <div class="mesg">手机号码：15888888888</div>
        <div class="mesg">收货地址：重庆市渝北区黄龙路28号</div>
        <div class="mesg">配送信息：由乐易装专送提供高品质配送服务</div>
      </div>
    </section>
    <!-- 订单详情 -->
    <section>
      <div class="title">订单详情</div>
      <div class="content">
        <div class="mesg">订单号码：1720187894135442630052</div>
        <div class="mesg">产品名称：防水漆 *1</div>
        <div class="mesg">调色包：#ffaacc *1</div>
        <div class="mesg">赠品：刷子 *1</div>
        <div class="mesg">支付方式：支付宝</div>
        <div class="mesg">是否开发票：是</div>
      </div>
    </section>
    <!-- 申请欠款 -->
    <section>
      <div class="title">申请欠款</div>
      <div class="content">
        <div class="mesg">已交款：1800元</div>
        <div class="mesg">欠款：200元</div>
      </div>
    </section>
    <a class="btn-submit-save bgc-ccc" href="#">确认送达</a>
    <a class="btn-submit-save bgc-ff8e08" href="#" onclick="pupopen()">申请欠款</a>
  </article>
  <!-- 详情查看 END -->

  <div class="clear h66"></div>

  <!-- 底部 -->
  <div class="index_footer">
    <ul>
    <li>
    <a href="#">
      <div></div>
      <span>首页</span>
    </a>
    </li>
    <li>
    <a href="#">
      <div></div>
      <span>下单</span>
    </a>
    </li>
    <li>
    <a href="#">
      <div></div>
      <span>我的</span>
    </a>
    </li>
    <li>
    <a href="#">
      <div></div>
      <span>已选</span>
    </a>
    </li>
  </ul>
  <div class="footer_act">
    <a href="#"></a>
  </div>
  </div>
  <!-- 底部 END -->

</body>
</html>