package com.ynyes.lyz.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.ynyes.lyz.entity.TdActivity;
import com.ynyes.lyz.entity.TdActivityGift;
import com.ynyes.lyz.entity.TdActivityGiftList;
import com.ynyes.lyz.entity.TdBrand;
import com.ynyes.lyz.entity.TdCartColorPackage;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdPayType;
import com.ynyes.lyz.entity.TdPriceList;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdProductCategory;
import com.ynyes.lyz.entity.TdShippingAddress;
import com.ynyes.lyz.entity.TdSubdistrict;
import com.ynyes.lyz.entity.TdUser;
import com.ynyes.lyz.entity.TdUserRecentVisit;
import com.ynyes.lyz.util.ClientConstant;
import com.ynyes.lyz.util.StringUtils;

@Service
public class TdCommonService {

	@Autowired
	private TdUserService tdUserService;

	@Autowired
	private TdProductCategoryService tdProductCategoryService;

	@Autowired
	private TdGoodsService tdGoodsService;

	@Autowired
	private TdPriceListItemService tdPriceListItemService;

	@Autowired
	private TdDiySiteService tdDiySiteService;

	@Autowired
	private TdUserRecentVisitService tdUserRecentVisitService;

	@Autowired
	private TdCouponService tdCouponService;

	@Autowired
	private TdActivityService tdActivityService;

	@Autowired
	private TdActivityGiftService tdActivityGiftService;

	@Autowired
	private TdCartGoodsService tdCartGoodsService;

	@Autowired
	private TdOrderService tdOrderService;

	@Autowired
	private TdPayTypeService tdPayTypeService;

	@Autowired
	private TdOrderGoodsService tdOrderGoodsService;

	@Autowired
	private TdSubdistrictService tdSubdistrictService;

	@Autowired
	private TdBrandService tdBrandService;

	@Autowired
	private TdPriceListService tdPriceListService;

	/**
	 * 获取登陆用户信息的方法
	 * 
	 * @author dengxiao
	 */
	public void setHeader(HttpServletRequest req, ModelMap map) {
		String username = (String) req.getSession().getAttribute("username");
		if (null != username) {
			map.addAttribute("username", username);
			map.addAttribute("user", tdUserService.findByUsernameAndIsEnableTrue(username));
		}

	}

	/**
	 * 获取登陆用户所属门店信息的方法
	 * 
	 * @author dengxiao
	 */
	public TdDiySite getDiySite(HttpServletRequest req) {
		// 获取到登陆用户的用户名
		String username = (String) req.getSession().getAttribute("username");
		// 通过用户名查找到用户资料
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		// 获取登陆用户的门店信息
		TdDiySite diySite = tdDiySiteService.findByRegionIdAndCustomerId(user.getCityId(), user.getCustomerId());
		if (null == diySite) {
			diySite = new TdDiySite();
		}
		return diySite;
	}

	/**
	 * 查找一个商品价目表项的方法
	 * 
	 * @author dengxiao
	 */
	public TdPriceListItem getGoodsPrice(HttpServletRequest req, TdGoods goods) {
		// 获取登陆用户的门店
		TdDiySite diySite = this.getDiySite(req);
		// 获取sobId
		Long sobId = diySite.getRegionId();

		if (null == goods) {
			return null;
		}

		if (null == goods.getInventoryItemId()) {
			return null;
		}

		String productFlag = goods.getBrandTitle();

		if (null == productFlag) {
			return null;
		}

		String priceType = null;

		// 零售价
		if (productFlag.equalsIgnoreCase("华润")) {
			priceType = "LS";
		}
		// 乐意装价
		else if (productFlag.equalsIgnoreCase("乐易装")) {
			priceType = "LYZ";
		}
		// 莹润价
		else if (productFlag.equalsIgnoreCase("莹润")) {
			priceType = "YR";
		}
		// 不支持的价格
		else {
			return null;
		}

		List<TdPriceList> priceList_list = tdPriceListService
				.findBySobIdAndPriceTypeAndStartDateActiveAndEndDateActive(sobId, priceType, new Date(), new Date());

		if (null == priceList_list || priceList_list.size() == 0 || priceList_list.size() > 1) {
			return null;
		}

		// 价目表ID
		Long list_header_id = 0L;
		list_header_id = priceList_list.get(0).getListHeaderId();

		List<TdPriceListItem> priceItemList = tdPriceListItemService
				.findByListHeaderIdAndInventoryItemIdAndStartDateActiveAndEndDateActive(list_header_id,
						goods.getInventoryItemId(), new Date(), new Date());

		if (null == priceItemList || priceItemList.size() == 0 || priceItemList.size() > 1) {
			return null;
		}

		return priceItemList.get(0);
	}

	/**
	 * 查找三级分类的方法并查找指定三级分类下的所有商品及其价目表的方法
	 * 
	 * @author dengxiao
	 */
	public void getCategory(HttpServletRequest req, ModelMap map) {
		// 查找到所有的一级分类
		List<TdProductCategory> level_one_categories = tdProductCategoryService.findByParentIdIsNullOrderBySortIdAsc();
		map.addAttribute("level_one_categories", level_one_categories);
		// 遍历一级分类用于查找所有的二级分类
		for (int i = 0; i < level_one_categories.size(); i++) {
			// 获取指定的一级分类
			TdProductCategory one_category = level_one_categories.get(i);
			// 根据指定的一级分类查找到该分类下所有的二级分类
			List<TdProductCategory> level_two_categories = tdProductCategoryService
					.findByParentIdOrderBySortIdAsc(one_category.getId());
			map.addAttribute("level_two_categories" + i, level_two_categories);
			// 遍历二级分类查找其下所有的商品
			for (int j = 0; j < level_two_categories.size(); j++) {
				TdProductCategory two_category = level_two_categories.get(j);
				List<TdGoods> goods_list = tdGoodsService
						.findByCategoryIdAndIsOnSaleTrueOrderBySortIdAsc(two_category.getId());
				List<TdGoods> putaway = new ArrayList<>();
				// 遍历所有的商品，查询在指定城市的商品的价格
				for (int k = 0; k < goods_list.size(); k++) {
					TdGoods goods = goods_list.get(k);
					if (null != goods) {
						TdPriceListItem priceListItem = this.getGoodsPrice(req, goods);
						if (null != priceListItem) {
							putaway.add(goods);
							map.addAttribute("priceListItem" + i + "_" + j + "_" + k, priceListItem);
						} else {
							putaway.add(null);
						}
					}
				}
				map.addAttribute("goods_list" + i + "_" + j, putaway);
			}
		}
	}

	/**
	 * 获取所有已选商品的方法
	 * 
	 * @author dengxiao
	 */
	public List<TdCartGoods> getSelectedGoods(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		List<TdCartGoods> all_selected = (ArrayList<TdCartGoods>) req.getSession().getAttribute("all_selected");
		if (null == all_selected) {
			return new ArrayList<TdCartGoods>();
		}
		return all_selected;
	}

	/**
	 * 获取所有已选调色包的方法
	 * 
	 * @author dengxiao
	 */
	public List<TdCartColorPackage> getSelectedColorPackage(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		List<TdCartColorPackage> all_color = (ArrayList<TdCartColorPackage>) req.getSession().getAttribute("all_color");
		if (null == all_color) {
			return new ArrayList<TdCartColorPackage>();
		}
		return all_color;
	}

	/**
	 * 获取已选数量（包括商品和调色包）的方法
	 * 
	 * @author dengxiao
	 */
	public Long getSelectedNumber(HttpServletRequest req) {
		Long selected_number = 0L;
		List<TdCartGoods> selected_goods_list = this.getSelectedGoods(req);
		List<TdCartColorPackage> selected_color_list = this.getSelectedColorPackage(req);
		if (null != selected_goods_list) {
			selected_number += selected_goods_list.size();
		}
		if (null != selected_color_list) {
			selected_number += selected_color_list.size();
		}
		return selected_number;
	}

	/**
	 * 获取指定id商品和添加登陆用户浏览记录的方法
	 * 
	 * @author dengxiao
	 */
	public void addUserRecentVisit(HttpServletRequest req, ModelMap map, Long goodsId) {
		// 获取登陆用户的用户名
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		// 获取指定id的商品的信息
		TdGoods goods = tdGoodsService.findOne(goodsId);
		map.addAttribute("goods", goods);
		// 添加浏览记录
		TdUserRecentVisit visit = new TdUserRecentVisit();
		visit.setUsername(username);
		visit.setUserId(user.getId());
		visit.setGoodsId(goods.getId());
		visit.setGoodsTitle(goods.getTitle());
		visit.setGoodsCoverImageUri(goods.getCoverImageUri());
		visit.setVisitTime(new Date());
		visit.setSku(goods.getCode());
		// 默认排序号1
		visit.setSortId(1.00);

		// 查看是否有重复的浏览记录
		TdUserRecentVisit user_visit = tdUserRecentVisitService.findByGoodsIdAndUserId(goodsId, user.getId());
		// 如果有此件商品的浏览记录，则删除它
		if (null != user_visit) {
			tdUserRecentVisitService.delete(user_visit);
		}

		// 查找当前用户所有的浏览记录
		List<TdUserRecentVisit> all_visit = tdUserRecentVisitService.findByUserIdOrderByVisitTimeAsc(user.getId());
		// 查询当前存储的浏览记录数量是否大于最大数量
		if (null != all_visit && all_visit.size() == ClientConstant.MAXRECENTNUM) {
			tdUserRecentVisitService.delete(all_visit.get(0));
		}
		// 存储新的浏览记录
		tdUserRecentVisitService.save(visit);
	}

	/**
	 * 判断登陆用户的优惠券是否有过期的，并将过期优惠券状态改变的方法
	 * 
	 * @author dengxiao
	 */
	public void checkUserCoupon(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		// 获取登陆用户所有未过期且未使用的优惠券
		List<TdCoupon> coupon_list = tdCouponService
				.findByUsernameAndIsUsedFalseAndIsOutDateFalseOrderByGetTimeDesc(username);
		for (TdCoupon coupon : coupon_list) {
			if (null != coupon.getExpireTime()) {
				// 获取过期时间的毫秒值
				Long finish = coupon.getExpireTime().getTime();
				// 获取当前时间的毫秒值
				Long now = new Date().getTime();
				// 判断优惠券是否过期
				if (now > finish) {
					coupon.setIsOutDate(true);
					tdCouponService.save(coupon);
				}
			}
		}
	}

	/**
	 * 获取参与某些活动的所有商品及其对应的价格
	 * 
	 * @author dengxiao
	 */
	public List<Map<TdGoods, Double>> getPromotionGoodsAndPrice(HttpServletRequest req, List<TdActivity> activities) {
		// 创建一个集合用户存储参与活动的商品及其价格
		List<Map<TdGoods, Double>> promotion_list = new ArrayList<>();
		// 创建一个集合存储所有的参加活动的商品id
		List<Long> ids = new ArrayList<>();

		TdDiySite diySite = this.getDiySite(req);

		// 获取参与活动的商品
		for (TdActivity activity : activities) {
			if (null != activity && null != activity.getGoodsNumber()) {
				// 此all_goods_number的格式为【id_number,id_number...】
				String all_goods_number = activity.getGoodsNumber();
				// 拆分all_goods_number
				String[] goods_number = all_goods_number.split(",");
				// 遍历，再拆分
				for (String item : goods_number) {
					// 拆分item，获取参与活动的商品id
					String[] param = item.split("_");
					if (null != param) {
						// 创建一个布尔变量判断商品id是否重复
						Boolean isRepeat = false;
						for (Long id : ids) {
							if (Long.parseLong(param[0]) == id) {
								isRepeat = true;
							}
						}
						// 在不重复的情况下将商品的id添加到ids中
						if (!isRepeat) {
							ids.add(Long.parseLong(param[0]));
						}
					}
				}
			}
		}

		// 遍历ids
		for (Long id : ids) {
			// 根据id获取指定的商品
			TdGoods goods = tdGoodsService.findOne(id);
			// 获取此件商品的价格
			if (null != goods) {
				TdPriceListItem priceList = tdPriceListItemService.findByPriceListIdAndGoodsId(diySite.getPriceListId(),
						goods.getId());
				// 创建一个Map集合存储【商品,价格】
				if (null != priceList) {
					Map<TdGoods, Double> pair = new HashMap<>();
					// 判断此件商品是否已经添加
					pair.put(goods, priceList.getSalePrice());
					promotion_list.add(pair);
				}
			}
		}
		return promotion_list;
	}

	/**
	 * 获取已选商品所参加的活动
	 * 
	 * @author dengxiao
	 */
	public List<TdActivity> getActivityBySelected(HttpServletRequest req) {
		// 创建一个集合用于存储当前已选所能参加的所有活动
		List<TdActivity> activities_joined = new ArrayList<>();
		// 创建一个集合用于存储当前已选商品所能参加的活动
		List<TdActivity> activities_by_goods = new ArrayList<>();
		// 创建一个集合用于存储当前已选调色包商品所能参加的活动
		List<TdActivity> activities_by_color = new ArrayList<>();

		// 获取所有的已选商品
		List<TdCartGoods> selectedGoods = this.getSelectedGoods(req);
		// 获取所有的已选调色包商品
		List<TdCartColorPackage> selectedColorPackage = this.getSelectedColorPackage(req);
		// 获取所有已选商品的数量
		Long selectedNumber = this.getSelectedNumber(req);
		// 获取当前门店
		TdDiySite diySite = this.getDiySite(req);

		// 遍历已选商品，获取已选商品所能参加的所有活动
		for (TdCartGoods cartGoods : selectedGoods) {
			activities_by_goods = tdActivityService.findActivitiesJoinedOrderBySortIdAsc(
					new Integer(selectedNumber + ""), (diySite.getId() + ","), (cartGoods.getGoodsId() + "_"),
					new Date());
			// 将查找出来的所有跟商品有关的活动添加进入总集合中
			if (null != activities_by_goods) {
				for (TdActivity activity : activities_by_goods) {
					if (null != activity && !activities_joined.contains(activity)) {
						activities_joined.add(activity);
					}
				}
			}
		}
		// 遍历所有的已选调色包，获取已选调色包所能参加的活动
		for (TdCartColorPackage cartColorPackage : selectedColorPackage) {
			activities_by_color = tdActivityService.findActivitiesJoinedOrderBySortIdAsc(
					new Integer(selectedNumber + ""), diySite.getId() + ",", cartColorPackage.getGoodsId() + "_",
					new Date());
			// 遍历调色包所参加的活动，判断其是否与已选商品参加的活动重复，如果不重复，则添加进入总集合中
			if (null != activities_by_color) {
				for (TdActivity activity : activities_by_color) {
					if (null != activity && !activities_joined.contains(activity)) {
						activities_joined.add(activity);
					}
				}
			}
		}

		// 将总集合进行内部排序（按照sortId进行排序）
		activities_joined = this.compareTheList(activities_joined);
		return activities_joined;
	}

	/**
	 * 对一个存储了TdActivity的集合进行内部排序的方法（按照sortId正序排序）
	 * 
	 * @author dengxiao
	 */
	public List<TdActivity> compareTheList(List<TdActivity> list) {
		// 自定义比较规则
		Comparator<TdActivity> compartor = new Comparator<TdActivity>() {
			public int compare(TdActivity a1, TdActivity a2) {
				if (a1.getSortId() - a2.getSortId() > 0) {
					return 1;
				} else if (a1.getSortId() - a2.getSortId() == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		Collections.sort(list, compartor);
		return list;
	}

	/**
	 * 获取所有参加的活动的赠品的方法（非小辅料赠送活动）
	 * 
	 * @param presented表示活动赠送的赠品
	 * @param selected代表已选商品
	 * @return 表示活动赠送的赠品
	 */
	// public List<TdCartGoods> getPresent(HttpServletRequest req,
	// List<TdCartGoods> selected,
	// List<TdCartGoods> presented) {
	// // 获取当前已选能够参加的活动
	// List<TdActivity> activities = this.getActivityBySelected(req);
	// // 创建一个布尔类型变量表示能否是否还能参加活动（用于跳出递归）
	// Boolean isActivity = false;
	// // 遍历活动集合，按照活动执行顺序判断所获取的赠品
	// for (TdActivity activity : activities) {
	// if (null != activity) {
	// // 创建一个布尔变量表示已选商品能否参加指定的活动
	// Boolean isJoin = true;
	// // 获取该活动所需要的商品及其数量的列表
	// String goodsAndNumber = activity.getGoodsNumber();
	// if (null != goodsAndNumber) {
	// // 拆分列表，使其成为【商品id_数量】的个体
	// String[] item = goodsAndNumber.split(",");
	// if (null != item) {
	// for (String each_item : item) {
	// if (null != each_item) {
	// // 拆分个体以获取id和数量的属性
	// String[] param = each_item.split("_");
	// // 当个体不为空且长度为2的时候才是正确的数据
	// if (null != param && param.length == 2) {
	// Long id = Long.parseLong(param[0]);
	// Long quantity = Long.parseLong(param[1]);
	// // 遍历selected（已选商品）
	// for (int i = 0; i < selected.size(); i++) {
	// TdCartGoods cartGoods = selected.get(i);
	// if (cartGoods.getGoodsId() == id && cartGoods.getQuantity() < quantity) {
	// isJoin = false;
	// }
	// }
	// }
	// }
	// }
	// // 如果循环结束，isJoin的值还是true，则代表该活动可以参加
	// if (isJoin) {
	// isActivity = true;
	// // 获取活动的赠品
	// String giftAndNumber = activity.getGiftNumber();
	// // 拆分得到赠品的【id_数量】个体
	// if (null != giftAndNumber) {
	// String[] singles = giftAndNumber.split(",");
	// for (String single : singles) {
	// // 拆分个体，获取赠品的id和数量属性
	// if (null != single) {
	// String[] param = single.split("_");
	// Long id = Long.parseLong(param[0]);
	// Long quantity = Long.parseLong(param[1]);
	// // 创建一个商品的已选项存储赠品，其价格为0
	// TdCartGoods gift = new TdCartGoods();
	// // 查找到指定的商品
	// TdGoods goods = tdGoodsService.findOne(id);
	// gift.setGoodsTitle(goods.getTitle());
	// gift.setGoodsId(id);
	// gift.setQuantity(quantity);
	// gift.setPrice(0.00);
	// presented.add(gift);
	// }
	// }
	// }
	// // 如果本次有活动参加，则去除参加活动的已选商品，继续查看是否有其他活动可以参加
	// if (isActivity) {
	// selected = this.getLeftCartGoods(selected, activity);
	// presented = this.getPresent(req, selected, presented);
	// }
	// }
	// }
	// }
	// }
	// }
	//
	// return presented;
	// }

	/**
	 * 获取已选商品能够得到的小辅料
	 * 
	 * @author dengxiao
	 */
	public TdOrder getGift(HttpServletRequest req, TdOrder order) {
		// 获取已选【分类：数量】组
		Map<Long, Long> group = this.getGroup(req);
		List<TdOrderGoods> giftGoodsList = order.getGiftGoodsList();
		if(null == giftGoodsList){
			giftGoodsList = new ArrayList<>();
		}
		// 获取已选能够参加的活动
		List<TdActivityGift> activities = this.getActivityGiftBySelected(req);
		for (TdActivityGift activity : activities) {
			Long categoryId = activity.getCategoryId();
			Long quantity = activity.getBuyNumber();
			// 判断是否满足条件
			if (null != group.get(categoryId) && group.get(categoryId) >= quantity) {
				// 添加小辅料赠品
				List<TdActivityGiftList> giftList = activity.getGiftList();
				if (null != giftList) {
					for (int i = 0; i < giftList.size(); i++) {
						TdActivityGiftList gift = giftList.get(i);
						TdGoods tdGoods = tdGoodsService.findOne(gift.getGoodsId());
						TdOrderGoods goods = new TdOrderGoods();
						goods.setBrandId(tdGoods.getBrandId());
						goods.setBrandTitle(tdGoods.getBrandTitle());
						goods.setPrice(0.00);
						goods.setQuantity(gift.getNumber());
						goods.setGoodsTitle(tdGoods.getTitle());
						goods.setGoodsSubTitle(tdGoods.getSubTitle());
						goods.setGoodsId(tdGoods.getId());
						goods.setGoodsCoverImageUri(tdGoods.getCoverImageUri());
						goods.setSku(tdGoods.getCode());
						// 创建一个布尔变量用于判断此件商品是否已经加入了小辅料
						Boolean isHave = false;
						for (TdOrderGoods orderGoods : giftGoodsList) {
							if (null != orderGoods && null != orderGoods.getGoodsId()
									&& orderGoods.getGoodsId() == gift.getGoodsId()) {
								isHave = true;
								orderGoods.setQuantity(orderGoods.getQuantity() + gift.getNumber());
							}
						}
						if (!isHave) {
							giftGoodsList.add(goods);
						}
						tdOrderGoodsService.save(goods);
					}
				}
			}
		}
		order.setGiftGoodsList(giftGoodsList);
		order = tdOrderService.save(order);
		return order;
	}

	/**
	 * 获取已选商品能够参加的小辅料赠送活动
	 * 
	 * @author dengxiao
	 */
	public List<TdActivityGift> getActivityGiftBySelected(HttpServletRequest req) {
		// 创建一个集合用于存储当前已选的所能参加的小辅料活动
		List<TdActivityGift> join = new ArrayList<>();

		// 获取已选【类别：数量】组
		Map<Long, Long> category_quantity = this.getGroup(req);

		// 遍历map
		for (Long categoryId : category_quantity.keySet()) {
			// 根据分类id获取小辅料赠送活动
			List<TdActivityGift> activityGift_list = tdActivityGiftService
					.findByCategoryIdAndIsUseableTrueAndBeginTimeBeforeAndEndTimeAfterOrderBySortIdAsc(categoryId,
							new Date());
			// 将参加的活动添加到join中
			if (null != activityGift_list) {
				for (TdActivityGift activityGift : activityGift_list) {
					if (null != activityGift && !join.contains(activityGift)) {
						join.add(activityGift);
					}
				}
			}
		}

		// 进行内部排序
		join = this.compareTheGiftList(join);
		return join;
	}

	/**
	 * 对一个存储了TdActivityGift的集合进行内部排序的方法（按照sortId正序排序）
	 * 
	 * @author dengxiao
	 */
	public List<TdActivityGift> compareTheGiftList(List<TdActivityGift> list) {
		// 自定义比较规则
		Comparator<TdActivityGift> compartor = new Comparator<TdActivityGift>() {
			public int compare(TdActivityGift a1, TdActivityGift a2) {
				if (a1.getSortId() - a2.getSortId() > 0) {
					return 1;
				} else if (a1.getSortId() - a2.getSortId() == 0) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		Collections.sort(list, compartor);
		return list;
	}

	/**
	 * 根据已选获取【类别id：数量】组
	 * 
	 * @author dengxiao
	 */
	public Map<Long, Long> getGroup(HttpServletRequest req) {
		Map<Long, Long> group = new HashMap<>();
		String username = (String) req.getSession().getAttribute("username");
		// 获取已选商品（整合后）
		List<TdCartGoods> all_selected = tdCartGoodsService.findByUsername(username);
		for (TdCartGoods cartGoods : all_selected) {
			// 获取已选的商品
			if (null != cartGoods) {
				TdGoods goods = tdGoodsService.findOne(cartGoods.getGoodsId());
				// 获取已选商品的分类id
				Long categoryId = goods.getCategoryId();
				// 判断是否已经添加进入到map中
				if (null == group.get(categoryId)) {
					group.put(categoryId, cartGoods.getQuantity());
				} else {
					group.put(categoryId, (group.get(categoryId) + cartGoods.getQuantity()));
				}
			}
		}
		return group;
	}

	/**
	 * 清空已选的方法
	 * 
	 * @author dengxiao
	 */
	public void clear(HttpServletRequest req) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			user = new TdUser();
		}

		// 删除用户的已选
		List<TdCartGoods> list = tdCartGoodsService.findByUserId(user.getId());
		tdCartGoodsService.deleteAll(list);
	}

	/**
	 * 根据已选生成虚拟订单
	 * 
	 * @author dengxiao
	 */
	public TdOrder createVirtual(HttpServletRequest req) {
		// 获取登陆用户的信息
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsername(username);
		if (null == user) {
			user = new TdUser();
		}

		// 生成一个订单表示虚拟订单
		TdOrder virtual = new TdOrder();

		// 获取当前用户所有的已选
		List<TdCartGoods> select_goods = tdCartGoodsService.findByUsername(username);

		TdShippingAddress defaultAddress = new TdShippingAddress();
		// 获取默认的收货地址
		List<TdShippingAddress> addressList = user.getShippingAddressList();
		if (null != addressList) {
			for (TdShippingAddress address : addressList) {
				if (null != address && null != address.getIsDefaultAddress() && address.getIsDefaultAddress()) {
					defaultAddress = address;
				}
			}
		}

		// 默认的配送方式1（1代表送货上门，2代表门店自提）
		String delivery = "送货上门";
		// 默认门店为用户的归属门店
		TdDiySite defaultDiy = this.getDiySite(req);

		// 默认的配送日期：第二天的的上午11:30——12:30
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		Date date = cal.getTime();
		SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
		String order_deliveryDate = sdf_ymd.format(date);
		Long order_deliveryDeatilId = 11L;

		// 以下代码用于生成订单编号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date now = new Date();
		String sDate = sdf.format(now);
		Random random = new Random();
		Integer suiji = random.nextInt(900) + 100;
		String orderNum = sDate + suiji;
		// 订单编号生成结束

		// 获取默认的支付方式
		TdPayType defaultType = new TdPayType();
		List<TdPayType> payTypeList = tdPayTypeService.findAllOrderBySortIdAsc();
		if (null != payTypeList) {
			for (TdPayType type : payTypeList) {
				if (null != type && null != type.getTitle() && !"到店支付".equals(type.getTitle())) {
					defaultType = type;
					break;
				}
			}
		}

		// 获取运费
		Double fee = 0.00;
		TdSubdistrict subdistrict = tdSubdistrictService.findOne(defaultAddress.getSubdistrictId());
		if (null == subdistrict) {
			subdistrict = new TdSubdistrict();
		}
		fee = subdistrict.getDeliveryFee();
		if (null == fee) {
			fee = 0.00;
		}

		virtual.setUsername(user.getUsername());
		virtual.setUserId(user.getId());

		virtual.setOrderNumber("XN" + orderNum);
		virtual.setOrderTime(new Date());
		virtual.setShippingAddress(defaultAddress.getCity() + defaultAddress.getDisctrict()
				+ defaultAddress.getSubdistrict() + defaultAddress.getDetailAddress());
		virtual.setShippingName(defaultAddress.getReceiverName());
		virtual.setShippingPhone(defaultAddress.getReceiverMobile());
		virtual.setOrderGoodsList(new ArrayList<TdOrderGoods>());
		virtual.setTotalGoodsPrice(0.00);
		virtual.setTotalPrice(0.00);
		virtual.setProductCouponId("");
		virtual.setProductCoupon("");
		virtual.setCashCoupon(0.00);
		virtual.setLimitCash(0.00);
		virtual.setCashCouponId("");
		virtual.setStatusId(2L);
		virtual.setDeliverTypeTitle(delivery);
		virtual.setDiySiteId(defaultDiy.getId());
		virtual.setDiySiteName(defaultDiy.getTitle());
		virtual.setDiySitePhone(defaultDiy.getServiceTele());

		virtual.setPayTypeId(defaultType.getId());
		virtual.setPayTypeTitle(defaultType.getTitle());
		virtual.setDeliveryDate(order_deliveryDate);
		virtual.setDeliveryDetailId(order_deliveryDeatilId);
		virtual.setDeliverFee(fee);

		// 遍历所有的已选商品，生成虚拟订单
		for (TdCartGoods cart : select_goods) {
			TdOrderGoods goods = new TdOrderGoods();
			goods.setGoodsId(cart.getGoodsId());
			goods.setGoodsTitle(cart.getGoodsTitle());
			goods.setGoodsCoverImageUri(cart.getGoodsCoverImageUri());
			goods.setSku(cart.getSku());
			goods.setPrice(cart.getPrice());
			goods.setQuantity(cart.getQuantity());
			goods.setBrandId(cart.getBrandId());
			goods.setBrandTitle(cart.getBrandTitle());
			virtual.setTotalGoodsPrice(virtual.getTotalGoodsPrice() + (cart.getPrice() * cart.getQuantity()));
			virtual.setTotalPrice(virtual.getTotalPrice() + (cart.getPrice() * cart.getQuantity()));
			virtual.setLimitCash(
					virtual.getLimitCash() + ((cart.getPrice() - cart.getRealPrice()) * cart.getQuantity()));
			List<TdOrderGoods> goodsList = virtual.getOrderGoodsList();
			goodsList.add(goods);
			tdOrderGoodsService.save(goods);
			tdOrderService.save(virtual);
		}
		virtual = this.getPresent(req, virtual);
		virtual = this.getGift(req, virtual);
		return virtual;
	}

	/**
	 * 查找用户已选获得的赠品
	 * 
	 * @author dengxiao
	 */
	public TdOrder getPresent(HttpServletRequest req, TdOrder order) {
		String username = (String) req.getSession().getAttribute("username");
		// 获取用户的已选
		List<TdCartGoods> all_selected = tdCartGoodsService.findByUsername(username);

		// 获取赠品列表
		List<TdOrderGoods> presentedList = order.getPresentedList();

		if (null == presentedList) {
			presentedList = new ArrayList<>();
		}

		// 为了避免脏数据刷新，创建一个map用于存储已选【id：数量】
		Map<Long, Long> selected_map = new HashMap<>();

		for (TdCartGoods cartGoods : all_selected) {
			Long id = cartGoods.getGoodsId();
			Long quantity = cartGoods.getQuantity();

			selected_map.put(id, quantity);
		}

		// 获取用户的门店
		TdDiySite diySite = this.getDiySite(req);
		List<TdActivity> activity_list = tdActivityService
				.findByDiySiteIdsContainingAndBeginDateBeforeAndFinishDateAfterOrderBySortIdAsc(diySite.getId() + "",
						new Date());
		for (TdActivity activity : activity_list) {
			// 创建一个布尔变量表示已选商品能否参加指定的活动
			Boolean isJoin = true;
			// 获取该活动所需要的商品及其数量的列表
			Map<Long, Long> cost = new HashMap<>();
			String goodsAndNumber = activity.getGoodsNumber();
			if (null != goodsAndNumber) {
				// 拆分列表，使其成为【商品id_数量】的个体
				String[] item = goodsAndNumber.split(",");
				if (null != item) {
					for (String each_item : item) {
						if (null != each_item) {
							// 拆分个体以获取id和数量的属性
							String[] param = each_item.split("_");
							// 当个体不为空且长度为2的时候才是正确的数据
							if (null != param && param.length == 2) {
								Long id = Long.parseLong(param[0]);
								Long quantity = Long.parseLong(param[1]);
								cost.put(id, quantity);
								if (null == selected_map.get(id) || selected_map.get(id) < quantity) {
									isJoin = false;
									break;
								}
							}
						}
					}

					if (isJoin) {
						// 判断参与促销的倍数（表示同一个活动可以参加几次）
						List<Long> mutipuls = new ArrayList<>();
						// 获取倍数关系
						for (Long goodsId : cost.keySet()) {
							Long quantity = cost.get(goodsId);
							Long goods_quantity = selected_map.get(goodsId);
							Long mutiplu = goods_quantity / quantity;
							mutipuls.add(mutiplu);
						}

						// 集合中最小的数字即为倍数
						Long min = Collections.min(mutipuls);

						// 改变剩下的商品的数量
						for (Long goodsId : cost.keySet()) {
							Long quantity = cost.get(goodsId);
							Long leftNum = selected_map.get(goodsId) - (quantity * min);
							selected_map.put(goodsId, leftNum);
						}

						// 获取赠品队列
						String giftNumber = activity.getGiftNumber();
						if (null != giftNumber) {
							String[] group = giftNumber.split(",");
							if (null != group) {
								for (String each_item : group) {
									if (null != each_item) {
										// 拆分个体以获取id和数量的属性
										String[] param = each_item.split("_");
										// 当个体不为空且长度为2的时候才是正确的数据
										if (null != param && param.length == 2) {
											Long id = Long.parseLong(param[0]);
											Long quantity = Long.parseLong(param[1]);
											// 查找到指定id的商品
											TdGoods goods = tdGoodsService.findOne(id);
											TdOrderGoods orderGoods = new TdOrderGoods();
											orderGoods.setBrandId(goods.getBrandId());
											orderGoods.setBrandTitle(goods.getBrandTitle());
											orderGoods.setGoodsCoverImageUri(goods.getCoverImageUri());
											orderGoods.setGoodsId(goods.getId());
											orderGoods.setGoodsTitle(goods.getTitle());
											orderGoods.setGoodsSubTitle(goods.getSubTitle());
											orderGoods.setPrice(0.00);
											orderGoods.setQuantity(quantity * min);
											orderGoods.setSku(goods.getCode());
											// 创建一个布尔变量用于表示赠品是否已经在队列中
											Boolean isHave = false;
											for (TdOrderGoods single : presentedList) {
												if (null != single && null != single.getGoodsId()
														&& single.getGoodsId() == orderGoods.getGoodsId()) {
													isHave = true;
													single.setQuantity(single.getQuantity() + orderGoods.getQuantity());
												}
											}

											if (!isHave) {
												presentedList.add(orderGoods);
											}
											tdOrderGoodsService.save(orderGoods);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		order.setPresentedList(presentedList);
		order = tdOrderService.save(order);
		return order;
	}

	/**
	 * 支付结束拆单的方法
	 * 
	 * @author dengxiao
	 */
	public void dismantleOrder(HttpServletRequest req, String username) {

		// 获取虚拟订单
		TdOrder order_temp = (TdOrder) req.getSession().getAttribute("order_temp");
		if (null == order_temp) {
			order_temp = new TdOrder();
		}

		// 创建一个map用于存储拆单后的所有订单
		Map<Long, TdOrder> order_map = new HashMap<>();

		// 获取所有的品牌
		List<TdBrand> brand_list = tdBrandService.findAll();
		if (null != brand_list) {
			for (TdBrand brand : brand_list) {
				TdOrder order = new TdOrder();
				order.setOrderNumber(order_temp.getOrderNumber().replace("XN", brand.getShortName()));
				order.setShippingAddress(order_temp.getShippingAddress());
				order.setShippingName(order_temp.getShippingName());
				order.setShippingPhone(order_temp.getShippingPhone());
				order.setDeliverFee(0.00);
				order.setDeliverTypeTitle(order_temp.getDeliverTypeTitle());
				order.setDeliveryDate(order_temp.getDeliveryDate());
				order.setDeliveryDetailId(order_temp.getDeliveryDetailId());
				order.setOrderGoodsList(new ArrayList<TdOrderGoods>());
				order.setTotalGoodsPrice(0.00);
				order.setTotalPrice(0.00);
				order.setLimitCash(0.00);
				order.setCashCoupon(0.00);
				order.setLimitCash(0.00);
				order.setProductCoupon("");
				order.setCashCouponId("");
				order.setStatusId(3L);
				order.setUsername(username);
				order.setPayTypeId(order_temp.getPayTypeId());
				order.setPayTypeTitle(order_temp.getPayTypeTitle());
				order.setOrderTime(order_temp.getOrderTime());
				order_map.put(brand.getId(), order);
			}
		}

		List<TdOrderGoods> goodsList = order_temp.getOrderGoodsList();
		// 对已选商品进行拆单
		for (TdOrderGoods orderGoods : goodsList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getOrderGoodsList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
				order.setOrderGoodsList(orderGoodsList);
				order.setTotalGoodsPrice(
						order.getTotalGoodsPrice() + (orderGoods.getPrice() * orderGoods.getQuantity()));
				order.setTotalPrice(order.getTotalPrice() + (orderGoods.getPrice() * orderGoods.getQuantity()));
			}
		}

		List<TdOrderGoods> presentedList = order_temp.getPresentedList();
		if (null == presentedList) {
			presentedList = new ArrayList<>();
		}
		// 对赠品进行拆单
		for (TdOrderGoods orderGoods : presentedList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getPresentedList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
				order.setPresentedList(presentedList);
			}
		}

		List<TdOrderGoods> giftGoodsList = order_temp.getGiftGoodsList();
		if (null == giftGoodsList) {
			giftGoodsList = new ArrayList<>();
		}
		// 对赠送的小辅料进行拆单
		for (TdOrderGoods orderGoods : giftGoodsList) {
			if (null != orderGoods) {
				Long brandId = orderGoods.getBrandId();
				TdOrder order = order_map.get(brandId);
				List<TdOrderGoods> orderGoodsList = order.getGiftGoodsList();
				if (null == orderGoodsList) {
					orderGoodsList = new ArrayList<>();
				}
				orderGoodsList.add(orderGoods);
				order.setGiftGoodsList(giftGoodsList);
			}
		}

		// 获取使用现金券的金额
		Double cashCoupon = order_temp.getCashCoupon();
		if (null == cashCoupon) {
			cashCoupon = 0.00;
		}
		// 拆分已使用的现金券
		String cashCouponId = order_temp.getCashCouponId();
		// 分解cashCouponId
		if (null != cashCouponId) {
			String[] cashIds = cashCouponId.split(",");
			for (String id : cashIds) {
				if (null != id && !"".equals(id.trim())) {
					Long couponId = Long.parseLong(id);
					// 根据优惠券的id查找优惠券
					TdCoupon coupon = tdCouponService.findOne(couponId);
					if (null != coupon) {
						Long goodsId = coupon.getGoodsId();
						// 如果goodsId存在，则表示这张优惠券是指定产品现金券
						if (null != goodsId) {
							TdGoods goods = tdGoodsService.findOne(goodsId);
							Long brandId = goods.getBrandId();
							TdOrder order = order_map.get(brandId);
							order.setCashCoupon(order.getCashBalanceUsed() + coupon.getPrice());
							// 余下的金额暂不统计，在后面按照比例拆分
							cashCoupon -= coupon.getPrice();
							order.setTotalPrice(order.getTotalPrice() - coupon.getPrice());
						}
					}
				}
			}
		}

		// 拆分使用的产品券
		String productCouponId = order_temp.getProductCouponId();
		// 分解
		String[] productIds = productCouponId.split(",");
		for (String id : productIds) {
			if (null != id && !"".equals(id.trim())) {
				Long couponId = Long.parseLong(id);
				TdCoupon coupon = tdCouponService.findOne(couponId);
				if (null != coupon) {
					Long goodsId = coupon.getGoodsId();
					if (null != goodsId) {
						TdGoods goods = tdGoodsService.findOne(goodsId);
						Long brandId = goods.getBrandId();
						TdOrder order = order_map.get(brandId);
						order.setProductCouponId(coupon.getId() + ",");
						order.setProductCoupon(goods.getTitle() + "【" + goods.getCode() + "】*1,");
						List<TdOrderGoods> list = order.getOrderGoodsList();
						for (TdOrderGoods orderGoods : list) {
							if (null != orderGoods && null != orderGoods.getGoodsId()
									&& coupon.getGoodsId() == orderGoods.getGoodsId()) {
								order.setTotalPrice(order.getTotalPrice() - orderGoods.getPrice());
							}
						}
					}
				}
			}
		}

		// 开始进行剩余优惠券（即是通用现金券）的拆分，同时也可以进行可提现余额，不可提现余额的拆分

		Double total = 0.00;
		Double cashBalanceUsed = order_temp.getCashBalanceUsed();
		if (null == cashBalanceUsed) {
			cashBalanceUsed = 0.00;
		}
		Double unCashBalanceUsed = order_temp.getUnCashBalanceUsed();
		if (null == unCashBalanceUsed) {
			unCashBalanceUsed = 0.00;
		}

		// 获取目前的总金额
		for (TdOrder order : order_map.values()) {
			if (null != order && null != order.getTotalPrice()) {
				total += order.getTotalPrice();
			}
		}

		// 在此循环，拆分通用现金券额度，可提现余额，不可提现余额
		for (TdOrder order : order_map.values()) {
			if (null != order && null != order.getTotalPrice()) {
				if (total != 0) {
					Double point = order.getTotalPrice() / total;
					order.setCashCoupon(order.getCashCoupon() + (cashCoupon * point));
					order.setCashBalanceUsed(cashBalanceUsed * point);
					order.setUnCashBalanceUsed(unCashBalanceUsed * point);
				}
			}
		}

		// 查询是否存在乐易装的品牌
		TdBrand brand = tdBrandService.findByTitle("乐易装");
		if (null != brand) {
			Long brandId = brand.getId();
			TdOrder order = order_map.get(brandId);
			// 运费放置在乐易装的订单上
			order.setDeliverFee(order_temp.getDeliverFee());
			order.setTotalPrice(order.getTotalPrice() + order.getDeliverFee());
			order.setTotalGoodsPrice(order.getTotalGoodsPrice() + order.getDeliverFee());
		}

		// 遍历存储
		for (TdOrder order : order_map.values()) {
			for (TdOrderGoods string : order.getOrderGoodsList()) {
				System.err.println(string);
			}
			if (null != order && null != order.getTotalGoodsPrice() && order.getTotalGoodsPrice() > 0) {
				tdOrderService.save(order);
			}
		}

		// 删除虚拟订单
		order_temp.setGiftGoodsList(null);
		order_temp.setPresentedList(null);
		order_temp.setOrderGoodsList(null);

		tdOrderService.delete(order_temp);

		// 清空session中的虚拟订单
		req.getSession().setAttribute("order_temp", null);
	}

	/**
	 * 计算能够使用的最大额度的预存款的方法
	 * 
	 * @author dengxiao
	 */
	public Double getMaxCash(HttpServletRequest req, ModelMap map, TdOrder order) {
		String username = (String) req.getSession().getAttribute("username");
		TdUser user = tdUserService.findByUsernameAndIsEnableTrue(username);
		if (null == user) {
			return 0.0;
		}
		Double max = 0.00;
		// 获取用户的预存款
		Double balance = user.getBalance();

		if (null != balance && null != order.getTotalPrice() && null != order.getDeliverFee()) {
			if (balance > (order.getTotalPrice() + order.getDeliverFee())) {
				max = (order.getTotalPrice() + order.getDeliverFee());
			} else {
				max = balance;
			}
		}
		map.addAttribute("max", max);
		return max;
	}

	public static String getIp(HttpServletRequest request) {
		if (request == null)
			return "";
		String ip = request.getHeader("X-Requested-For");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
