<script>
    $("#bg").height($(window).height());
    function pupopen(index){
        <#-- 设置属性 -->
        document.getElementById("diy_title").innerHTML = diysite_arry[index].title;
        document.getElementById("diy_attr").innerHTML = diysite_arry[index].isDirect;
        document.getElementById("diy_address").innerHTML = diysite_arry[index].address;
        document.getElementById("diy_phone").innerHTML = diysite_arry[index].serviceTele + "<a href='tel://"+ diysite_arry[index].serviceTele +"'>(点击拨打)</a>";
        document.getElementById("diy_check").setAttribute("onclick","submitTheSite(" + diysite_arry[index].id + ");");
        
        document.getElementById("bg").style.display="block";
        document.getElementById("pop-up").style.display="block" ;
    }
    function pupclose(){
      document.getElementById("bg").style.display="none";
      document.getElementById("pop-up").style.display="none" ;
    }
    function submitTheSite(id){
        <#-- 开启等待图标 -->
        wait();
        <#-- 发送异步请求 -->
        $.ajax({
            url:"/user/diy/save",
            type:"post",
            timeout:10000,
            data:{
                id:id
            },
            error:function(){
                <#-- 关闭等待图标 -->
                close(1);
                warning("亲，您的网速不给力啊");
            },
            success:function(res){
                <#-- 关闭等待图标 -->
                close(100);
                if(0 == res.status){
                    warning("操作成功");
                    setTimeout(function(){
                        window.location.reload();
                    },1000)
                }else{
                    warning(res.message);
                }
            }
        });
    }     
    diysite_arry = [
        <#if all_site??>
            <#list all_site as item>
                {
                    "id":${item.id?c},
                    "title":"${item.title!''}",
                    "isDirect":<#if item.status??>
                                   <#switch item.status>
                                       <#case 0>"直营"<#break>
                                       <#case 1>"加盟"<#break>
                                       <#case 2>"虚拟"<#break>
                                       <#case 3>"第三方"<#break>
                                   </#switch>
                               <#else>
                                    "无"
                               </#if>,
                    "address":"${item.address!'无'}",
                    "serviceTele":"${item.serviceTele!'00000000000'}"
                }
                <#if (item_index+1) lt all_site?size>
                    ,
                </#if>
            </#list>
        </#if>
    ]
</script>
<#if all_site??>
    <#list all_site as item>
        <div class="stores-list" onclick="pupopen(${item_index?c});">${item.title!''}</div>
    </#list>
</#if>