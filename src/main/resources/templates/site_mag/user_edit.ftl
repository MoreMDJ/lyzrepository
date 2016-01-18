<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/mag/style/idialog.css" rel="stylesheet" id="lhgdialoglink">
<title>编辑会员信息</title>
<script type="text/javascript" src="/mag/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="/mag/js/Validform_v5.3.2_min.js"></script>
<script type="text/javascript" src="/mag/js/lhgdialog.js"></script>
<script type="text/javascript" src="/mag/js/WdatePicker.js"></script>
<script type="text/javascript" src="/mag/js/swfupload.js"></script>
<script type="text/javascript" src="/mag/js/swfupload.queue.js"></script>
<script type="text/javascript" src="/mag/js/swfupload.handlers.js"></script>
<script type="text/javascript" src="/mag/js/layout.js"></script>
<link href="/mag/style/style.css" rel="stylesheet" type="text/css">
<link href="/mag/style/WdatePicker.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
$(function () {
    //初始化表单验证
    $("#form_user").initValidform();

    //初始化上传控件
    $(".upload-img").each(function () {
        $(this).InitSWFUpload({ 
            sendurl: "/Verwalter/upload", 
            flashurl: "/mag/js/swfupload.swf"
        });
    });

        //（缩略图）
    var txtPic = $("#txtImgUrl").val();
    if (txtPic == "" || txtPic == null) {
        $(".thumb_ImgUrl_show").hide();
    }
    else {
        $(".thumb_ImgUrl_show").html("<ul><li><div class='img-box1'><img src='" + txtPic + "' bigsrc='" + txtPic + "' /></div></li></ul>");
        $(".thumb_ImgUrl_show").show();
    }

    $("#txtImgUrl").blur(function () {
        var txtPic = $("#txtImgUrl").val();
        if (txtPic == "" || txtPic == null) {
            $(".thumb_ImgUrl_show").hide();
        }
        else {
            $(".thumb_ImgUrl_show").html("<ul><li><div class='img-box1'><img src='" + txtPic + "' bigsrc='" + txtPic + "' /></div></li></ul>");
            $(".thumb_ImgUrl_show").show();
        }
    });  
    
    //设置封面图片的样式
    $(".photo-list ul li .img-box img").each(function () {
        if ($(this).attr("src") == $("#hidFocusPhoto").val()) {
            $(this).parent().addClass("selected");
        }
    }); 
    
    $("#btnEditRemark").click(function () { EditOrderRemark(); });    //修改积分备注 
});   

 //修改粮草备注
        function EditOrderRemark() {
            var dialog = $.dialog({
                title: '修改积分备注',
                content: '<input type="checkbox" name="showtype" id="showtype" checked="checked"/><label> 仅后台显示</label> </br><textarea id="pointRemark" name="txtPointRemark" rows="2" cols="20" class="input"></textarea>',
                min: false,
                max: false,
                lock: true,
                ok: function () {
                    var showtype = $("#showtype", parent.document).is(':checked');                    
                    var remark = $("#pointRemark", parent.document).val();                   
                    if (remark == "") {
                        $.dialog.alert('对不起，请输入备注内容！', function () { }, dialog);
                        return false;
                    }
                    var userId = eval(document.getElementById("userId")).value;
                    var point = eval(document.getElementById("totalPoints")).value;
                    var postData = { "userId": userId, "totalPoints": point, "data": remark, "type":"editPoint", "isBackgroundShow": showtype};
                    //发送AJAX请求
                    sendAjaxUrl(dialog, postData, "/Verwalter/user/param/edit");
                    return false;
                },
                cancel: true
            });
        }
    //发送AJAX请求
        function sendAjaxUrl(winObj, postData, sendUrl) {
            $.ajax({
                type: "post",
                url: sendUrl,
                data: postData,
                dataType: "json",
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    $.dialog.alert('尝试发送失败，错误信息：' + errorThrown, function () { }, winObj);
                },
                success: function (data) {
                    if (data.code == 0) {
                        winObj.close();
                        $.dialog.tips(data.msg, 2, '32X32/succ.png', function () { location.reload(); }); //刷新页面
                    } else {
                        $.dialog.alert('错误提示：' + data.message, function () { }, winObj);
                    }
                }
            });
        }   

</script>
</head>

<body class="mainbody">
<form name="form_user" method="post" action="/Verwalter/user/save" id="form_user">
<div>
<input type="hidden" name="__VIEWSTATE" id="__VIEWSTATE" value="${__VIEWSTATE!""}" >
<input type="hidden" id="userId" name="userId" value="<#if user??>${user.id?c!""}</#if>" >
</div>
<!--导航栏-->
<div class="location" style="position: static; top: 0px;">
  <a href="/Verwalter/user/list?roleId=${roleId!""}"><i></i><span>返回列表页</span></a>
  <a href="/Verwalter/center" class="home"><i></i><span>首页</span></a>
  <i class="arrow"></i>
  <span>会员管理</span>
  <i class="arrow"></i>
  <span>编辑会员信息</span>
</div>
<div class="line10"></div>
<!--/导航栏-->

<!--内容-->
<div class="content-tab-wrap">
  <div id="floatHead" class="content-tab" style="position: static; top: 52px;">
    <div class="content-tab-ul-wrap">
      <ul>
        <li><a href="javascript:;" onclick="tabs(this);" class="selected">基本资料</a></li>
        <li><a href="javascript:;" onclick="tabs(this);">安全设置</a></li>
        
        <li><a href="javascript:;" onclick="tabs(this);">账户信息</a></li>
        
      </ul>
    </div>
  </div>
</div>

<!--基本资料-->
<div class="tab-content">
  <dl>
    <dt>用户类型</dt>
    <dd>
      <div class="rule-multi-radio multi-radio">
        <span>
            <input type="radio" name="userType" value="0" datatype="n" <#if user?? && user.userType?? && user.userType==0>checked="checked"</#if>><label>普通会员</label>
            <input type="radio" name="userType" value="1" datatype="n" <#if user?? && user.userType?? && user.userType==1>checked="checked"</#if>><label>销售顾问</label>
            <input type="radio" name="userType" value="2" datatype="n" <#if user?? && user.userType?? && user.userType==2>checked="checked"</#if>><label>店长</label>
            <input type="radio" name="userType" value="3" datatype="n" <#if user?? && user.userType?? && user.userType==3>checked="checked"</#if>><label>店主</label>
            <input type="radio" name="userType" value="4" datatype="n" <#if user?? && user.userType?? && user.userType==4>checked="checked"</#if>><label>区域经理</label>
        	<input type="radio" name="userType" value="5" datatype="n" <#if user?? && user.userType?? && user.userType==5>checked="checked"</#if>><label>配送员</label>
        </span>
      </div>
      <span class="Validform_checktip"></span>
    </dd>
  </dl> 
  <dl>
    <dt>用户状态</dt>
    <dd>
      <div class="rule-multi-radio">
        <span id="rblStatus">
            <input type="radio" name="isEnable" value="1" datatype="n" <#if user?? && user.isEnable?? && user.isEnable == true>checked="checked"</#if>>
            <label>正常</label>
            <input type="radio" name="isEnable" value="0" datatype="n" <#if user?? && user.isEnable?? && user.isEnable == false>checked="checked"</#if>>
            <label>禁用</label>
        </span>
      </div>
      <span class="Validform_checktip">*禁用账户无法登录</span>
    </dd>
  </dl>
  <#--
    <dl>
        <dt>用户名：</dt>
         【备注人】张季 2016年1月8日10:18:05 【原版】用户名不可修改
        <dd>
            <#if user??>
                <span>${user.username!""}</span>
            <#else>
                <input name="username" type="text" maxlength="200" class="input normal" datatype="s6-20" ajaxurl="/Verwalter/user/check<#if user??>?id=${user.id}</#if>" sucmsg=" " minlength="2">
            </#if>
            
       <dd>
            <input name="username" type="text" value="<#if user??>${user.username!''}</#if>" maxlength="200" class="input normal" datatype="m|/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/" ajaxurl="/Verwalter/user/check<#if user??>?id=${user.id}</#if>" sucmsg=" " minlength="2">
       		<input name="oldUsername" type="hidden" value="<#if user??>${user.username!''}</#if>" />
       </dd>
            <span class="Validform_checktip">
        </span></dd>
    </dl>
    -->
    <dl>
        <dt>用户名：</dt>
        <dd>
            <#if user??>
                <span>${user.username!""}</span>
            <#else>
                <input name="username" type="text" maxlength="200" class="input normal" datatype="m|/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/"ajaxurl="/Verwalter/user/check<#if user??>?id=${user.id}</#if>" sucmsg=" " minlength="2">
            </#if>
       <dd>
    </dl>
  <dl>
    <dt>真实姓名</dt>
    <dd><input name="realName" type="text" value="<#if user??>${user.realName!""}</#if>" class="input normal"></dd>
  </dl>
  <dl>
    <dt>用户编码</dt>
    <dd>
    	<input name="opUser" type="text" value="<#if user??>${user.opUser!""}</#if>" class="input normal">
    	<span class="Validform_checktip">仅配送员填写</span>
    </dd>
  </dl>
  <dl>
    <dt>归属门店</dt>
    <dd>
        <select name="upperDiySiteId" datatype="n">
            <#if !user??>
                <option value="">请选择门店</option>
            </#if>
            <#if site_list??>
                <#list site_list as item>
                    <option <#if user.upperDiySiteId?? && item.id==user.upperDiySiteId>selected="selected"</#if> value="${item.id?c}">${item.title!''}</option>
                </#list>
            </#if>
        </select>
    </dd>
  </dl>
  <dl>
    <dt>上传头像</dt>
    <dd>
        <input id="txtImgUrl" name="headImageUri" type="text" value="<#if user??>${user.headImageUri!""}</#if>" class="input normal upload-path">
        <div class="upload-box upload-img"></div>
        <div class="photo-list thumb_ImgUrl_show">
            <ul>
                <li>
                    <div class="img-box1"></div>
                </li>
            </ul>
        </div>
    </dd>
  </dl>
  <dl>
    <dt>用户性别</dt>
    <dd>
      <div class="rule-multi-radio multi-radio">
        <span style="display: none;">
        <input type="radio" name="sex" value="保密" <#if !user?? || !user.sex?? || user.sex=="保密">checked="checked"</#if>>
        <label>保密</label>
        <input type="radio" name="sex" value="男" <#if user?? && user.sex?? && user.sex=="男">checked="checked"</#if>>
        <label>男</label>
        <input type="radio" name="rblSex" value="女" <#if user?? && user.sex?? && user.sex=="女">checked="checked"</#if>>
        <label>女</label>
        </span>
      </div>
    </dd>
  </dl>
  <dl>
    <dt>出生日期</dt>
    <dd>
      <div class="input-date">
        <input name="birthdate" type="text" value="<#if user??>${user.birthday!""}</#if>" class="input date" onfocus="WdatePicker({dateFmt:&#39;yyyy-MM-dd&#39;})" datatype="/^\s*$|^\d{4}\-\d{1,2}\-\d{1,2}$/" errormsg="请选择正确的日期" sucmsg=" ">
        <i>日期</i>
      </div>
    </dd>
  </dl>  
</div>
<!--/基本资料-->

<!--安全设置-->
<div class="tab-content" style="display:none;">  
  <dl>
    <dt>设置新密码</dt>
    <dd><input name="password" type="password" value="" class="input normal" datatype="*6-20" ignore="ignore" nullmsg="请设置密码" errormsg="密码范围在6-20位之间" sucmsg=" " value=""> <span class="Validform_checktip">*新密码将覆盖原密码，至少6位</span></dd>
  </dl>
  <dl>
    <dt>确认密码</dt>
    <dd><input name="password1" type="password" value="" class="input normal" datatype="*" ignore="ignore"  recheck="password" nullmsg="请再输入一次密码" errormsg="两次输入的密码不一致" sucmsg=" " value=""> <span class="Validform_checktip">*再次输入密码</span></dd>
  </dl>
  <#if user??>
  	<input name="oldPassword" type="hidden" value="${user.password!''}" />
  </#if>
  <#--
  <dl>
    <dt>邮箱账号</dt>
    <dd><input name="email" type="text" value="<#if user??>${user.email!""}</#if>" id="txtEmail" class="input normal" ignore="ignore" datatype="e" sucmsg=" " > <span class="Validform_checktip">*取回密码时用到</span></dd>
  </dl>
  <dl>
    <dt>手机号码</dt>
    <dd><input name="mobile" type="text" value="<#if user??>${user.mobile!""}</#if>" class="input normal" ignore="ignore" datatype="m" sucmsg=" " ></dd>
  </dl> 
  <dl>
    <dt>累计消费额</dt>
    <dd><span><#if user??>${user.totalSpendCash!""}</#if></span></dd>
  </dl>
 <dl>
    <dt>用户等级</dt>
    <dd><input name="userLevelId" type="text" value="<#if user??>${user.userLevelId!""}</#if>" class="input normal" datatype="n0-2" errormsg="请输入正确的等级" sucmsg=" " > <span class="Validform_checktip">*数字表示的用户等级，从1开始，熟悉越高等级越高</span></dd>
  </dl> 
  <dl>
    <dt>用户等级</dt>
    <dd>
         <div class="rule-single-select">
              <select name="userLevelId" id="userLevelId" >
                   <#if !user??>
                        <option value="">请选择类别...</option>
                    </#if>
                    <#if user_level_list??>
                         <#list user_level_list as level>
                             <option value="${level.id!""}" <#if user?? && user.userLevelId?? && user.userLevelId==level.id>selected="selected"</#if>>${level.title!""}</option>
                         </#list>
                    </#if>
              </select>
         </div>
    </dd>
  </dl>
  <dl>
    <dt>用户等级名称</dt>
    <dd><span><#if user??>${user.userLevelTitle!""}</#if></span></dd>
  </dl>
  <dl>
    <dt>用户积分</dt>
    <dd><span><#if user??>${user.totalPoints!""}</#if></span></dd>
  </dl>
  <dl>
    <dt>修改用户积分</dt>
    <dd>
        <input name="totalPoints1" id="totalPoints" type="text" class="input" value="<#if user?? && user.totalPoints??>${user.totalPoints?c}<#else>0</#if>">
        <input name="btnEditRemark" type="button" id="btnEditRemark" class="ibtn" value="确认修改" style="margin-top: -3px;">
    </dd>
  </dl>  
  <dl>
    <dt>咨询总数</dt>
    <dd><span><#if user??>${user.totalConsults!""}</#if></span></dd>
  </dl>
  <dl>
    <dt>评论总数</dt>
    <dd><span><#if user??>${user.totalComments!""}</#if></span></dd>
  </dl>
  <dl>
    <dt>退换货总数</dt>
    <dd><span><#if user??>${user.totalReturns!""}</#if></span></dd>
  </dl>-->
</div>
<!--/安全设置-->

<!--账户信息-->
<div class="tab-content" style="display:none;">
<#if !user?? || (user?? && user.userType?? && user.userType == 0 )>
  <dl>
    <dt>总金额</dt>
    <dd><input name="balance" type="text" class="input normal" sucmsg=" " value="<#if user?? && user.balance??>${user.balance?string('0.00')}<#else>0</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>可提现余额</dt>
    <dd><input name="cashBalance" type="text" class="input normal"sucmsg=" " value="<#if user?? && user.cashBalance??>${user.cashBalance?string('0.00')}<#else>0</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>不可提现余额</dt>
    <dd><input name="unCashBalance" type="text" class="input normal"sucmsg=" " value="<#if user?? && user.unCashBalance??>${user.unCashBalance?string('0.00')}<#else>0</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  </#if>
  <#if !user?? || user?? && user.userType?? && user.userType == 1>
  <dl>
    <dt>下级用户总数</dt>
    <dd><input name="totalLowerUsers" type="text" id="txtPay_Password" class="input normal" sucmsg=" " value="<#if user?? && user.totalLowerUsers??>${user.totalLowerUsers?c}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>用户返现金额</dt>
    <dd><input name="totalCashRewards" type="text" id="txtPay_Password" class="input normal"sucmsg=" " value="<#if user?? && user.totalCashRewards??>${user.totalCashRewards?c}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>提现冻结金额</dt>
    <dd><input name="cashRewardsFrozen" type="text" id="txtPay_Password" class="input normal"sucmsg=" " value="<#if user?? && user.cashRewardsFrozen??>${user.cashRewardsFrozen?c}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  </#if>
  <#--
  <dl>
    <dt>银行卡号</dt>
    <dd><input name="bankCardCode" type="text" id="txtPay_Password" class="input normal"  sucmsg=" " value="<#if user??>${user.bankCardCode!""}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>银行名称</dt>
    <dd><input name="bankTitle" type="text" id="txtPay_Password" class="input normal"  sucmsg=" " value="<#if user??>${user.bankTitle!""}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
 <dl>
    <dt>银行卡已验证</dt>
    <dd><input name="txtPay_Password" type="text" id="txtPay_Password" class="input normal" nullmsg="请设置支付密码" errormsg="支付密码范围在6-20位之间" sucmsg=" " value="<#if user??>${user.totalReturns!""}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>银行卡验证</dt>
    <dd>
      <div class="rule-multi-radio multi-radio">
        <span>
            <input type="radio" name="isBankCardVerified" value="1"  <#if user?? && user.isBankCardVerified?? && user.isBankCardVerified==1>checked="checked"</#if>><label>已验证</label>
            <input type="radio" name="isBankCardVerified" value="0"  <#if user?? && user.isBankCardVerified?? && user.isBankCardVerified==0>checked="checked"</#if>><label>未验证</label>
        </span>
      </div>
      <span class="Validform_checktip"></span>
    </dd>
  </dl> 
  
  <#if !user?? || user?? && user.userType?? && user.userType == 2>
  <dl>
    <dt>虚拟币余额</dt>
    <dd><input name="virtualCurrency" type="text" id="" class="input normal"  sucmsg=" " value="<#if user??>${user.virtualCurrency!""}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
    <dt>冻结金额</dt>
    <dd><input name="frozenCapital" type="text" id="" class="input normal"  sucmsg=" " value="<#if user??>${user.frozenCapital!""}</#if>"> <span class="Validform_checktip"></span></dd>
  </dl>
  <dl>
  </#if>
  -->
</div> 

<!--/账户信息-->


<!--借款标的-->

<!--/借款标的-->

<!--投资标的-->

<!--/投资标的-->

<!--资金明细-->

<!--/资金明细-->

<!--工具栏-->
<div class="page-footer">
  <div class="btn-list">
    <input type="submit" name="btnSubmit" value="提交保存" id="btnSubmit" class="btn">
    <input name="btnReturn" type="button" value="返回上一页" class="btn yellow" onclick="javascript:history.back(-1);">
  </div>
  <div class="clear"></div>
</div>
<!--/工具栏-->

</form>


</body></html>