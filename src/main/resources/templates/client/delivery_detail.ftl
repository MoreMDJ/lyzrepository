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
<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=bdd6b0736678f88ed49be498bff86754"></script>
<script type="text/javascript">

var map, geolocation;

//加载地图，调用浏览器定位服务
map = new AMap.Map('container');

setInterval("timer()", 1000 * 60 * 60);
    
function timer() {
    map.plugin('AMap.Geolocation', function() {
        geolocation = new AMap.Geolocation({
            enableHighAccuracy: true,//是否使用高精度定位，默认:true
            timeout: 2000          //超过10秒后停止定位，默认：无穷大
        });
        map.addControl(geolocation);
        geolocation.getCurrentPosition();
        AMap.event.addListener(geolocation, 'complete', onComplete);//返回定位信息
    });
    
    var geocoder;
    
    //解析定位结果
    function onComplete(data) {
    	AMap.service('AMap.Geocoder',function(){//回调函数
	        //实例化Geocoder
	        geocoder = new AMap.Geocoder({
	            city: "010" //城市，默认：“全国”
	        });
	        
			var lnglatXY=[data.position.getLng(), data.position.getLat()];//地图上所标点的坐标
			
	        geocoder.getAddress(lnglatXY, function(status, result) {
	            if (status === 'complete' && result.info === 'OK') {
	               //获得了有效的地址信息:
	               warning(result.regeocode.formattedAddress);
	               
	               $.ajax({ 
						url: "/delivery/geo/submit", 
						type: "post",
						dataType: "json",
						data: 
						{
							"longitude": data.position.getLng(), 
							"latitude": data.position.getLat(),
							"accuracy": data.accuracy,
							"isConverted": data.isConverted,
							"formattedAddress" : result.regeocode.formattedAddress
						},
						success: function(data)
						{
				        	if (data.code != 0)
				        	{
				        		warning(data.message);
				        	}
				  		}
					});
	            }else{
	               //获取地址失败
	            }
	        });
	    })
    }
}
</script>
<script>
// 确认送达
function submitDelivery(id)
{
	if (null == id)
	{
		warning("ID不能为空");
		return;
	}
	
	$.ajax({ 
		url: "/delivery/submitDelivery", 
		type: "post",
		dataType: "json",
		data: {"id": id},
		success: function(data)
		{
        	if (data.code == 0)
        	{
        		warning("确认成功");
        		window.location.reload();
        	}
        	else
        	{
        		warning(data.message);
        	}
  		}
	});
}

function submitOwnMoney()
{
	var payed = document.getElementById("payed").value;
	var owned = document.getElementById("owned").value;
	
	if (null == payed || null == owned || "" == payed || "" == owned)
	{
		warning("请输入正确的金额");
		return;
	}
	
	$.ajax({ 
		url: "/delivery/submitOwnMoney/1", 
		type: "post",
		dataType: "json",
		data: {"payed": payed, "owned": owned},
		success: function(data)
		{
        	if (data.code == 0)
        	{
        		warning("申请成功");
        		window.location.reload();
        	}
        	else
        	{
        		warning(data.message);
        	}
  		}
	});
}
</script>
</head>
<body class="bgc-f3f4f6">
<#include "/client/common_warn.ftl" />
  <!--弹窗-->
  <div id="bg"></div>
  <div id="arreabox">
    <form>
      <div class="title">申请欠款</div>   
      <div class="text1">已交款<input type="text" id="payed" value="0">元</div>
      <div class="text1">欠款&nbsp;&nbsp;<input type="text" id="owned" value="0">元</div>
      <div class="button-group">
        <a class="sure" href="#" onclick="pupclose()">关闭</a>
        <a class="cancle" href="#" onclick="submitOwnMoney()">提交</a>
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
    <a class="btn-submit-save bgc-ccc" href="javascript:;" onclick="submitDelivery(1)">确认送达</a>
    <a class="btn-submit-save bgc-ff8e08" href="javascript:;" onclick="pupopen()">申请欠款</a>
  </article>
  <!-- 详情查看 END -->

  <div class="clear h66"></div>

</body>
</html>