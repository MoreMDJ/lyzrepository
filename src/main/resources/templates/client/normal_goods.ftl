<#if some_goods??>
    <#-- 遍历二级分类下的所有商品 -->
    <#list some_goods as goods>
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
                    <#if ("priceListItem"+goods_index)?eval??>
                        <#if ("priceListItem"+goods_index)?eval.salePrice??>
                            <p>￥${("priceListItem"+goods_index)?eval.salePrice?string("0.00")}</p>
                        </#if>
                            
                        <#if ("priceListItem"+goods_index)?eval.isPromotion??>
                            <#if ("priceListItem"+goods_index)?eval.isPromotion>
                                <a style="margin-right:3%;">促销</a>
                            </#if>
                        </#if>
                    </#if>
                    <div>
                        <span onclick="changeQuantity(${goods.id?c},'delete')">-</span>
                        <input class="goodsSelectedQuantity" min="0" type="number" id="quantity${goods.id?c}" value="0" onchange="quantityChange(${goods.id?c})">
                        <span onclick="changeQuantity(${goods.id?c},'add')">+</span>
                    </div>
                </dd>
            </dl>
        </#if>
    </#list>
</#if>