package com.ynyes.lyz.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.ynyes.lyz.entity.TdActivity;
import com.ynyes.lyz.entity.TdActivityGift;
import com.ynyes.lyz.entity.TdActivityGiftList;
import com.ynyes.lyz.entity.TdCartColorPackage;
import com.ynyes.lyz.entity.TdCartGoods;
import com.ynyes.lyz.entity.TdCoupon;
import com.ynyes.lyz.entity.TdDiySite;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.entity.TdPriceListItem;
import com.ynyes.lyz.entity.TdProductCategory;
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
		TdDiySite diySite = tdDiySiteService.findOne(user.getUpperDiySiteId());
		if (null == diySite) {
			diySite = new TdDiySite();
		}
		return diySite;
	}

	/**
	 * 查找三级分类的方法并查找指定三级分类下的所有商品及其价目表的方法
	 * 
	 * @author dengxiao
	 */
	public void getCategory(HttpServletRequest req, ModelMap map) {
		TdDiySite diySite = getDiySite(req);
		// 根据门店信息获取到用户当前的价目表
		Long priceListId = null;
		if (null != diySite) {
			priceListId = diySite.getPriceListId();
		}

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
				map.addAttribute("goods_list" + i + "_" + j, goods_list);
				// 遍历所有的商品，查询在指定城市的商品的价格
				for (int k = 0; k < goods_list.size(); k++) {
					TdGoods goods = goods_list.get(k);
					if (null != goods) {
						TdPriceListItem priceListItem = tdPriceListItemService.findByPriceListIdAndGoodsId(priceListId,
								goods.getId());
						map.addAttribute("priceListItem" + i + "_" + j + "_" + k, priceListItem);
					}
				}
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
	public List<TdCartGoods> getPresent(HttpServletRequest req, List<TdCartGoods> selected,
			List<TdCartGoods> presented) {
		// 获取当前已选能够参加的活动
		List<TdActivity> activities = this.getActivityBySelected(req);
		// 创建一个布尔类型变量表示能否是否还能参加活动（用于跳出递归）
		Boolean isActivity = false;
		// 遍历活动集合，按照活动执行顺序判断所获取的赠品
		for (TdActivity activity : activities) {
			if (null != activity) {
				// 创建一个布尔变量表示已选商品能否参加指定的活动
				Boolean isJoin = true;
				// 获取该活动所需要的商品及其数量的列表
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
									// 遍历selected（已选商品）
									for (int i = 0; i < selected.size(); i++) {
										TdCartGoods cartGoods = selected.get(i);
										if (cartGoods.getGoodsId() == id && cartGoods.getQuantity() < quantity) {
											isJoin = false;
										}
									}
								}
							}
						}
						// 如果循环结束，isJoin的值还是true，则代表该活动可以参加
						if (isJoin) {
							isActivity = true;
							// 获取活动的赠品
							String giftAndNumber = activity.getGiftNumber();
							// 拆分得到赠品的【id_数量】个体
							if (null != giftAndNumber) {
								String[] singles = giftAndNumber.split(",");
								for (String single : singles) {
									// 拆分个体，获取赠品的id和数量属性
									if (null != single) {
										String[] param = single.split("_");
										Long id = Long.parseLong(param[0]);
										Long quantity = Long.parseLong(param[1]);
										// 创建一个商品的已选项存储赠品，其价格为0
										TdCartGoods gift = new TdCartGoods();
										// 查找到指定的商品
										TdGoods goods = tdGoodsService.findOne(id);
										gift.setGoodsTitle(goods.getTitle());
										gift.setGoodsId(id);
										gift.setQuantity(quantity);
										gift.setPrice(0.00);
										presented.add(gift);
									}
								}
							}
							// 如果本次有活动参加，则去除参加活动的已选商品，继续查看是否有其他活动可以参加
							if (isActivity) {
								selected = this.getLeftCartGoods(selected, activity);
								presented = this.getPresent(req, selected, presented);
							}
						}
					}
				}
			}
		}

		return presented;
	}

	/**
	 * 获取已选商品能够得到的小辅料
	 * 
	 * @author dengxiao
	 */
	public List<TdActivityGiftList> getGift(HttpServletRequest req, Map<Long, Long> group,
			List<TdActivityGiftList> sendGift) {
		// 获取当前所有能够参加的小辅料赠送活动
		List<TdActivityGift> avtivities = this.getActivityGiftBySelected(req);

		// 创建一个布尔变量判断是否能够继续参加活动
		Boolean isJoin = false;

		// 遍历活动
		for (TdActivityGift gift : avtivities) {
			if (null != gift) {
				if (null == gift.getCategoryId()) {
					gift.setCategoryId(0L);
				}
				if (null != group.get(gift.getCategoryId())) {
					if (null != gift.getBuyNumber() && gift.getBuyNumber() <= group.get(gift.getCategoryId())) {
						isJoin = true;
						// 获取当前活动赠送的小辅料
						List<TdActivityGiftList> giftList = gift.getGiftList();
						if (null != giftList) {
							for (int i = 0; i < giftList.size(); i++) {
								TdActivityGiftList item = giftList.get(i);
								// 创建一个布尔变量表示当前赠送的商品是否已经存在
								if (null != item) {
									Boolean isExist = false;
									for (int j = 0; j < sendGift.size(); j++) {
										TdActivityGiftList havedGift = sendGift.get(j);
										if (null != havedGift && null != havedGift.getGoodsId()
												&& item.getGoodsId() == havedGift.getGoodsId()) {
											havedGift.setNumber(havedGift.getNumber() + item.getNumber());
											isExist = true;
											break;
										}
									}
									if (!isExist) {
										sendGift.add(item);
									}
								}
							}
						}
						// 获取参加完毕之后减去参加活动的数量
						group.put(gift.getCategoryId(), (group.get(gift.getCategoryId()) - gift.getBuyNumber()));
						break;
					}
				}
			}
		}

		if (isJoin) {
			sendGift = this.getGift(req, group, sendGift);
		}

		return sendGift;
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
	 * 获取参加活动后剩余未参加活动已选商品的方法
	 * 
	 * @author dengxiao
	 */
	public List<TdCartGoods> getLeftCartGoods(List<TdCartGoods> selected, TdActivity activity) {
		String goodsAndNumber = activity.getGoodsNumber();
		// 拆分参加活动的商品信息
		if (null != goodsAndNumber) {
			String[] singles = goodsAndNumber.split(",");
			for (String single : singles) {
				if (null != single) {
					String[] param = single.split("_");
					Long id = Long.parseLong(param[0]);
					Long quantity = Long.parseLong(param[1]);
					// 遍历已选，去除参与活动的商品
					for (int i = 0; i < selected.size(); i++) {
						TdCartGoods cartGoods = selected.get(i);
						if (null != cartGoods && null != cartGoods.getGoodsId() && cartGoods.getGoodsId() == id) {
							cartGoods.setQuantity(cartGoods.getQuantity() - quantity);
						}
					}
				}
			}
		}
		return selected;
	}

	/**
	 * 将已选调色包整合到已选商品的方法（结算时调用）
	 * 
	 * @author dengxiao
	 */
	public List<TdCartGoods> getAllContainsColorPackage(HttpServletRequest req) {
		// 获取已选商品
		List<TdCartGoods> selected = this.getSelectedGoods(req);
		// 获取已选调色包
		List<TdCartColorPackage> all_color = this.getSelectedColorPackage(req);

		// 遍历已选调色包，获取调色包对应的商品
		for (TdCartColorPackage colorPackage : all_color) {
			if (null != colorPackage) {
				// 创建一个新的cartGoods用于存储
				TdCartGoods cartGoods = new TdCartGoods();
				cartGoods.setGoodsCoverImageUri(colorPackage.getImageUri());
				cartGoods.setGoodsId(colorPackage.getGoodsId());
				cartGoods.setGoodsTitle(colorPackage.getNumber());
				cartGoods.setPrice(colorPackage.getSalePrice());
				cartGoods.setRealPrice(colorPackage.getRealPrice());
				cartGoods.setQuantity(colorPackage.getQuantity());
				// 将其添加到selected中
				selected.add(cartGoods);
			}
		}
		return selected;
	}

	/**
	 * 根据已选获取【类别id：数量】组
	 * 
	 * @author dengxiao
	 */
	public Map<Long, Long> getGroup(HttpServletRequest req) {
		Map<Long, Long> group = new HashMap<>();
		// 获取已选商品（整合后）
		List<TdCartGoods> all_selected = this.getAllContainsColorPackage(req);
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
	 * 清空所有购物信息的方法
	 * 
	 * @author dengxiao
	 */
	public void clear(HttpServletRequest req) {
		// 获取当前登陆用户的用户名
		String username = (String) req.getSession().getAttribute("username");

		req.getSession().setAttribute("order_payTypeId", null);
		req.getSession().setAttribute("order_remark", null);
		req.getSession().setAttribute("order_diySiteId", null);
		req.getSession().setAttribute("order_deliveryId", null);
		req.getSession().setAttribute("order_deliveryDate", null);
		req.getSession().setAttribute("order_deliveryDetailId", null);
		req.getSession().setAttribute("order_noProductCouponUsed", null);
		req.getSession().setAttribute("order_productCouponUsed", null);
		req.getSession().setAttribute("all_selected", null);
		req.getSession().setAttribute("all_color", null);
		// 在数据库中，删除当前用户所有的已选
		List<TdCartGoods> list = tdCartGoodsService.findByUsername(username);
		tdCartGoodsService.deleteAll(list);
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
