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
<script type="text/javascript" src="js/jquery-1.11.3.min.js"></script>
</head>
<body class="bgc-f3f4f6">
  <!--弹窗-->
  <div id="bg"></div>
  <div id="popbox">
    <div class="time-select">
      <div>开始时间：<input type="date" min="2015-12-04"></div>
      <div>结束时间：<input type="date" min="2015-12-04"></div>
      <a class="btn-sure-time" href="#" onclick="pupclose()">确定</a>
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
    }
  </script>
  <!--弹窗 END-->
  <!-- 头部 -->
  <header>
    <a class="back" href="#"></a>
    <div class="date-group">
      <a class="active" href="#">三天内</a>
      <a href="#">七天内</a>
      <a class="btn-filter" href="#" onclick="pupopen()">筛选</a>
    </div>
  </header>
  <!-- 头部 END -->

  <!-- 详情列表 -->
  <article class="look-details-list">
    <ul>
      <li class="active"><a href="#">已配送（222）</a></li>
      <li><a href="#">配送中（222）</a></li>
      <li><a href="#">待配送（222）</a></li>
    </ul>
    <!-- 详情列表 -->
    <section>
      <a href="详情页.html">
        <div class="time">【2015-12-24 <span>17:16</span> 送达】</div>
        <div class="address">收货地址：重庆市渝北区黄龙路26号</div>
      </a>
    </section>
    <section>
      <a href="详情页.html">
        <div class="time">【2015-12-24 <span>17:16</span> 送达】</div>
        <div class="address">收货地址：重庆市渝北区黄龙路26号</div>
      </a>
    </section>
    <section>
      <a href="详情页.html">
        <div class="time">【2015-12-24 <span>17:16</span> 送达】</div>
        <div class="address">收货地址：重庆市渝北区黄龙路26号</div>
      </a>
    </section>
    <section>
      <a href="详情页.html">
        <div class="time">【2015-12-24 <span>17:16</span> 送达】</div>
        <div class="address">收货地址：重庆市渝北区黄龙路26号</div>
      </a>
    </section>
  </article>
  <!-- 详情列表 END -->

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