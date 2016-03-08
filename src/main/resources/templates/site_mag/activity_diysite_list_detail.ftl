<script type="text/javascript" src="/mag/js/layout.js"></script>
<script type="text/javascript">
function checkDiy(object){
    if ($(object).val() == "全选") {
        $(object).val("取消");
       
        var checkbox1 = $(".productIdRadio:enabled").first();
        var labels = checkbox1.siblings("label");
        var MDJnumber = 0;
        labels.each(function(){
            var val = $(this).html();
            var indexNum =$(this).index();
            $(".rule-multi-checkbox").find(':checkbox').eq(MDJnumber++).prop("checked",true);
            $("a:contains('"+val+"')").addClass("selected");
            
        });
    } else {
        $(object).val("全选");
        var checkbox1 = $(".productIdRadio:enabled").first();
        var labels = checkbox1.siblings("label");
        var MDJnumber = 0;
        labels.each(function(){
            var val = $(this).html();
            var indexNum =$(this).index();
            $(".rule-multi-checkbox").find(':checkbox').eq(MDJnumber++).prop("checked",false);
            $("a:contains('"+val+"')").removeClass("selected");
        });
    }
}
function UncheckDiy(object)
{
    var checkbox1 = $(".productIdRadio:enabled").first();
    var labels = checkbox1.siblings("label");
    var MDJnumber = 0;
    labels.each(function(){
        var val = $(this).html();
        var checkOne = $(".rule-multi-checkbox").find(':checkbox').eq(MDJnumber++);
        if(checkOne.prop("checked") == true)
        {
            checkOne.prop("checked",false);
        }
        else
        {
            checkOne.prop("checked",true);
        }
        $("a:contains('"+val+"')").trigger("click");
    });
}
</script>
<dl>
    <dt>门店</dt>
    <dd>
        <div>
        <input type="button" value="全选" onclick="checkDiy(this);">
        <input type="button" value="反全选" onclick="UncheckDiy(this);">
        </div>
        <#if diysite_list?? && diysite_list?size gt 0>
            <div class="rule-multi-checkbox">
                <span>
                    <#list diysite_list as product>
                        <input type="checkbox" class="productIdRadio" name="diySiteIds" value="${product.id!""}" datatype="*" <#if activity?? && activity.diySiteIds?? && activity.diySiteIds?contains(product.id?c)>checked="checked"</#if>>
                        <label>${product.title!""}</label>
                    </#list>
                </span>
            </div>
        </#if>
    </dd>
</dl>
