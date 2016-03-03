<#if !goods??>
<script type="text/javascript" src="/mag/js/layout.js"></script>
</#if>
<script type="text/javascript">
$(function () {
    // 根据选择的产品载入筛选项
    $(".productIdRadio").click(function(){
        $.ajax({
            url : '/Verwalter/product/parameter/'+$(this).val() <#if goods??>+"?goodsId=${goods.id?c}"</#if>,
            type : 'GET',
            success : function(res) {
                $("#productSelectDiv").html(res);
            }
        });
    });
});
function checkDiy(object){
    if ($(object).val() == "全选") {
        $(object).val("取消");
        $(".productIdRadio:enabled").prop("checked", true);
    } else {
        $(object).val("全选");
        $(".productIdRadio:enabled").prop("checked", false);
    }
}
</script>
<#if diysite_list?? && diysite_list?size gt 0>
<dl>
    <dt>门店</dt>
    <dd>
        <div>
            <input type="button" value="全选" onclick="checkDiy(this);">
            <input type="button" value="反全选">
        </div>
        <div class="rule-multi-checkbox">
            <span>
                <#list diysite_list as product>
                    <input type="checkbox" class="productIdRadio" name="diySiteIds" value="${product.id!""}" datatype="*" <#if activity?? && activity.diySiteIds?? && activity.diySiteIds?contains(product.id?c)>checked="checked"</#if>>
                    <label>${product.title!""}</label>
                </#list>
            </span>
        </div>
    </dd>
</dl>
</#if>
