package pk.shoplus.parameter;

public final class StatusType {

	/**
	 * 成功状态
	 */
	public static final int SUCCESS = 1;

	/**
	 * 失败状态(即将废弃)
	 */
	public static final int FAILURE = -1;

	public static final int WARNING = -11;

	/**
	 * 用户名不存在或已废弃(即将废弃)
	 */
	public static final int USERNAMENOTEXIST = -2;
	/**
	 * 邮箱已存在(即将废弃)
	 */
	public static final int USEREMAILEXIST = -3;

	/**
	 * 成功状态(即将废弃)
	 */
	public static final int REVIEWNOTPASS = -4;

	/**
	 * 错误的密码(即将废弃)
	 */
	public static final int FAILUREPASSWORD = -4;

	/**
	 * 已经注册的商铺(即将废弃)
	 */
	public static final int SHOPEXIST = -5;

	/**
	 * 已经添加过的shop-product(即将废弃)
	 */
	public static final int SHOPPRODUCTEXIST = -6;
	/**
	 * 已经存在购物车(即将废弃)
	 */
	public static final int CART_ITEM_EXIST = -7;
	/**
	 * 已经关注(即将废弃)
	 */
	public static final int ALREADY_ADD_WISHLIST = -8;
	/**
	 * 参数不合法(即将废弃)
	 */
	public static final int PARAMETEREXIST = -11;

	/**
	 * 参数不合逻辑(即将废弃)
	 */
	public static final int PARAMETERLOGIC = -12;

	/**
	 * 数据已存在(即将废弃)
	 */
	public static final int DATAEXIST = -13;
	/**
	 * 無法登陸(即将废弃)
	 */
	public static final int NOROLE = -14;

	/**
	 * 数据库中不存在的信息(即将废弃)
	 */
	public static final int NOTHINGNESS = -15;

	// -1000 ~ -1499 基本错误编码
	/**
	 * parameter is null
	 */
	public static final int PARAM_NULL = -1001;

	/**
	 * string parameter is empty or null
	 */
	public static final int PARAM_EMPTY_OR_NULL = -1002;

	/**
	 * number parameter is positive
	 */
	public static final int PARAM_NOT_POSITIVE = -1003;

	/**
	 * list parameter is null or empty
	 */
	public static final int PARAM_LIST_NULL_OR_EMPTY = -1004;

	/**
	 * session user is null
	 */
	public static final int SESSION_USER_NULL = -1005;

	/**
	 * string is not numeric
	 */
	public static final int IS_NOT_NUMERIC = -1006;

	/**
	 * error email format
	 */
	public static final int EMAIL_ADDRESS_ERROR = -1007;

	/**
	 * error password length
	 */
	public static final int PASSWORD_LENGTH_ERROR = -1008;

	/**
	 * Incorrect password
	 */
	public static final int INCORRECT_PASSWORD = -1009;

	/**
	 * error string length
	 */
	public static final int STRING_LENGTH_ERROR = -1010;

	/**
	 * IllegalArgument other param error
	 */
	public static final int ILLEGAL_ARGUMENT = -1011;

	/**
	 * is not valid date
	 */
	public static final int IS_NOT_VALID_DATE = -1012;

	/**
	 * URLDecoder.decode(description, "UTF-8"); catch
	 * UnsupportedEncodingException e
	 */
	public static final int STRING_CONVERT_UNSUPPORTED_ENCODING_EXCEPTION = -1013;

	/**
	 * is not good json
	 */
	public static final int IS_NOT_GOOD_JSON = -1014;

	/**
	 * 编码格式错误
	 */
	public static final int CODE_FORMAT_ERROR = -1015;

	public static final int DECODE_FORMAT_ERROR = -1015;

	/**
	 * 登录用户对应店铺不存在
	 */
	public static final int SHOP_NOT_EXIST_FROM_SESSION_USER = -1016;

	/**
	 * ArrayList out of range
	 */
	public static final int ARRAY_INDEX_OUT_OF_RANGE = -1017;

	/**
	 * 手机号格式错误
	 */
	public static final int MOBILE_FORMAT_ERROR = -1018;
	/**
	 * 身份证格式错误
	 */
	public static final int IDCARD_FORMAT_ERROR = -1019;
	/**
	 * 营业执照格式错误 businessLicenseNumber
	 */
	public static final int BUSINESSLICENSENUMBER_FORMAT_ERROR = -1020;

	/**
	 * This is not a JSON Array
	 */
	public static final int IS_NOT_JSON_ARRAY = -1021;

	/**
	 * 起始日期大于结束日期
	 */
	public static final int START_DATE_GREAT_THAN_END_DATE = -1022;
	/**
	 * 起始日期或结束日期大于当前日期
	 */
	public static final int START_DATE_OR_END_DATE_EARLY_TODAY = -1023;

	// 数据库异常编码 -1500 ~ -1999
	/**
	 * can't connect to database
	 */
	public static final int NOT_CONNECT_TO_DATABASE = -1501;
	/**
	 * can't connect to database
	 */
	public static final int DATABASE_ERROR = -1502;

	public static final int TIME_FORMAT_ERROR = -1503;

	// product -2000 ~ -2999

	// admin -3000 ~ -3999
	/**
	 * admin brand Brand id is not exist;
	 */
	public static final int BRAND_ID_NOT_EXIST = -3001;

	/**
	 * category is exist;
	 */
	public static final int CATEGORY_IS_EXIST = -3002;

	/**
	 * categoryPropertyKey create error
	 */
	public static final int CATEGORY_PROPERTY_KEY_CREATE_ERROR = -3003;

	/**
	 * categoryPropertyValue create error
	 */
	public static final int CATEGORY_PROPERTY_VALUE_CREATE_ERROR = -3004;

	/**
	 * categoryProperty create error
	 */
	public static final int CATEGORY_PROPERTY_CREATE_ERROR = -3005;

	/**
	 * categoryProductInfo create error
	 */
	public static final int CATEGORY_PRODUCT_INFO_CREATE_ERROR = -3006;

	/**
	 * categoryProductProperty create error
	 */
	public static final int CATEGORY_PRODUCT_PROPERTY_CREATE_ERROR = -3007;

	/**
	 * category 下有product
	 */
	public static final int CATEGORY_HAVE_PRODUCT = -3008;

	/**
	 * get category by category id return null
	 */
	public static final int GET_CATEGORY_BY_ID_NULL = -3009;
	/**
	 * admin user email is not is exist
	 */
	public static final int ADMIN_USER_EMAIL_NOT_EXIST = -3010;

	/**
	 * admin user email is not is exist
	 */
	public static final int ADMIN_USER_ROLE_NOT_EXIST = -3011;

	/**
	 * user input password error
	 */
	public static final int ADMIN_USER_PASSWORD_ERROR = -3012;

	/**
	 * role is not exist
	 */
	public static final int ADMIN_ROLE_NOT_EXIST = -3013;

	/**
	 * role is not exist
	 */
	public static final int ADMIN_NOTIFICATION_CREATE_ERROR = -3014;

	/**
	 * order status的值不对
	 */
	public static final int ORDER_STATUS_ERROR = -3015;

	/**
	 * ProductFeatureType is not exist
	 */
	public static final int PRODUCT_FEATRUE_TYPE_NOT_EXIST = -3016;

	/**
	 * getProductPageWithVendorAndBrandAndCategory return product list is not
	 * exist
	 */
	public static final int PRODUCT_PAGE_NOT_EXIST = -3017;

	/**
	 * convertToProductFromMap return product is not exist
	 */
	public static final int PRODUCT_NOT_EXIST = -3018;

	/**
	 * product status is not exist
	 */
	public static final int PRODUCT_STATUS_NOT_EXIST = -3019;

	/**
	 * product status is (NEW_PENDING or MODIFY_PENDING) and is not EXISTING
	 */
	public static final int NEW_PENDING_OR_MODIFY_PENDING_AND_NOT_EXISTING = -3020;

	/**
	 * product status is (EXISTING) and is not OFF_SALE
	 */
	public static final int EXISTING_AND_NOT_OFF_SALE = -3021;

	/**
	 * product status is (OFF_SALE) and is not EXISTING
	 */
	public static final int OFF_SALE_AND_NOT_EXISTING = -3022;

	/**
	 * product status is (NEW_REJECTED or MODIFY_REJECTED) and is not EXISTING
	 */
	public static final int NEW_REJECTED_OR_MODIFY_REJECTED_AND_NOT_EXISTING = -3023;

	/**
	 * get sku by id return null
	 */
	public static final int SKU_NOT_EXIST = -3024;

	/**
	 * skuStore is not exist
	 */
	public static final int SKUSTORE_NOT_EXIST = -3025;

	/**
	 * productInfo is not exist
	 */
	public static final int PRODUCT_INFO_NOT_EXIST = -3026;

	/**
	 * productInfoProperty is not exist
	 */
	public static final int PRODUCT_INFO_PROPERTY_NOT_EXIST = -3027;

	/**
	 * admin user create error
	 */
	public static final int ADMIN_USER_CREATE_ERROR = -3028;

	/**
	 * return application status 取值不对
	 */
	public static final int RETURN_APPLICATION_STATUS_ERROR = -3029;

	/**
	 * return application is not exist
	 */
	public static final int RETURN_APPLICATION_NOT_EXIST = -3030;

	/**
	 * AdminRoleManagementRole 权限名称已存在
	 */
	public static final int ROLE_NAME_IS_EXIST = -3031;

	/**
	 * defaultShippingCost is not exist
	 */
	public static final int DEFAULT_SHIPPING_COST_NOT_EXIST = -3032;

	/**
	 * create ShopBrand Is fail or create ShopCategory Is fail
	 */
	public static final int SHOPBRAND_OR_SHOPCATEGORY_CREATE_ERROR = -3033;

	/**
	 * getShopIndividualApplicationById return null
	 */
	public static final int SHOP_INDIVIDUAL_APPLICATION_NOT_EXIST = -3034;

	/**
	 * VendorApplication is not exist
	 */
	public static final int VENDOR_APPLICATION_NOT_EXIST = -3035;

	/**
	 * FileUpload error
	 */
	public static final int FILE_UPLOAD_ERROR = -3036;

	/**
	 * categoryProductInfo is not exist
	 */
	public static final int CATEGORY_PRODUCT_INFO_NOT_EXIST = -3037;

	/**
	 * create ProductInfo error
	 */
	public static final int CREATE_PRODUCT_INFO_ERROR = -3038;

	/**
	 * create ProductProperty error
	 */
	public static final int CREATE_PRODUCT_PROPERTY_ERROR = -3039;

	/**
	 * CategoryProductProperty not exist
	 */
	public static final int CATEGORY_PRODUCT_PROPERTY_NOT_EXIST = -3040;

	/**
	 * create ProductSkuProperty error
	 */
	public static final int CREATE_PRODUCT_SKU_PROPERTY_ERROR = -3041;

	/**
	 * create ProductSkuPropertyKey error
	 */
	public static final int CREATE_PRODUCT_SKU_PROPERTY_KEY_ERROR = -3042;

	/**
	 * create ProductSkuPropertyValue error
	 */
	public static final int CREATE_PRODUCT_SKU_PROPERTY_VALUE_ERROR = -3043;

	/**
	 * create Sku error
	 */
	public static final int CREATE_SKU_ERROR = -3044;

	/**
	 * create SkuStore error
	 */
	public static final int CREATE_SKU_STORE_ERROR = -3045;

	/**
	 * create SkuProperty error
	 */
	public static final int CREATE_SKU_PROPERTY_ERROR = -3046;

	/**
	 * create CashbackLevel error
	 */
	public static final int CREATE_CASHBACK_ERROR = -3047;

	/**
	 * 如果返回结果是false,则说明时间符合不规则
	 */
	public static final int SHIPPING_FEE_RULE_DATE_ERROR = -3048;

	/**
	 * order is not exist
	 */
	public static final int ORDER_IS_NOT_EXIST = -3049;
	/**
	 * VendorApplciation is not approved
	 */
	public static final int VENDOR_APPLICATION_NOT_APPROVED = -3050;

	public static final int CATEGORY_IS_NOT_EXIST = -3051;

	public static final int VENDOR_NOT_ACTIVED = -3052;
	
	/**
	 * category have node
	 */
	public static final int Category_HAVE_NODE = -3053;
	
	public static final int ADMIN_ORDER_STOCK_NOT_ENOUGH = -3054;
	public static final int SHOP_STATUS_ERROR = -3055;


	// vendor -4000 ~ -4999
	/**
	 * get vendor by user_id is null
	 */
	public static final int VENDOR_NULL_BY_USERID = -4001;

	/**
	 * un sign vendor application (no vendor)
	 */
	public static final int NO_VENDOR_APPLICATION = -4002;

	// shop -5000 ~ -5999
	/**
	 * shop order list empty
	 */
	public static final int SHOP_ORDER_LIST_EMPTY = -5001;
	/**
	 * 店铺余额不足
	 */
	public static final int SHOP_BALANCE_NO_ENOUGH = -5002;
	/**
	 * 根据id找不到shopOrganizatioonApplication
	 */
	public static final int SHOP_ORGANIZATION_APPLICATION_NOT_EXIST = -5003;
	/**
	 * shopOrganization信息无更新
	 */
	public static final int SHOP_INFORMATION_ORGANIZATION_NOT_UPDATE = -5004;
	/**
	 * 商品已存在正在进行的抢购中
	 */
	public static final int SHOP_RUSH_TO_BUY_PROGRESS_PRODUCT_EXIST = -5005;
	/**
	 * 店铺登录不存在的email
	 */
	public static final int SHOP_LOGIN_EMAIL_NOT_EXIST = -5006;
	/**
	 * shop product status error
	 */
	public static final int SHOP_PRODUCT_STATUS_ERROR = -5007;

	/**
	 * 注册时已经存在的申请
	 */
	public static final int SHOP_ORGANIZATION_APPLICATION_EXIST = -5008;

	/**
	 * shop logistics address not exist
	 */
	public static final int SHOP_LOGISTICS_ADDRESS_NOT_EXIST = -5009;

	/**
	 * 对应的抢购的事件不存在
	 */
	public static final int SHOP_RUSH_TO_BUY_EVENT_NOT_EXIST = -5010;
	/**
	 * 加入抢购时，对应shop与当前用户不一致
	 */
	public static final int SHOP_RUSH_TO_BUY_SHOP_NOT_MATCH = -5011;

	/**
	 * product库存为0
	 */
	public static final int SHOP_PRODUCT_STOCK_EMPTY = -5012;

	/**
	 * 商店不存在
	 */
	public static final int SHOP_IS_NOT_EXIST = -5013;

	/**
	 * 商店中商品不存在
	 */
	public static final int SHOP_PRODUCT_IS_NOT_EXIST = -5014;

	/**
	 * 商店中sku不存在
	 */
	public static final int SHOP_PRODUCT_SKU_IS_NOT_EXIST = -5015;
	/**
	 * order_logistics 不存在
	 */
	public static final int SHOP_ORDER_LOGISTICS_IS_NOT_EXIST = -5016;

	/**
	 * logistics_product 不存在
	 */
	public static final int SHOP_LOGISTICS_PRODUCT_IS_NOT_EXIST = -5017;

	public static final int SHOP_APPROVE_CATEGORY_IS_NOT_EXIST = -5018;

	public static final int SHOP_REVENUE_FLAG_ISNULL = -5019;

	// customer -6000 ~ -6999
	/**
	 * 错误的密码
	 */
	public static final int CUSTOMER_ERROR_PASSWORD = -6001;
	/**
	 * 邮箱已存在
	 */
	public static final int CUSTOMER_USERE_EMAIL_EXIST = -6002;
	/**
	 * 订单状态不是已支付 OrderStatusType.PAYED
	 */
	public static final int CUSTOMER_ORDER_STATUS_IS_NOT_PAYED = -6003;
	/**
	 * 订单已经有退换申请This order has return application!
	 */
	public static final int CUSTOMER_ORDER_HAS_RETURN_APPLICATION = -6004;

	/**
	 * 已经存在购物车
	 */
	public static final int CUSTOMER_ORDER_CART_ITEM_EXIST = -6005;
	/**
	 * 找不到ShopProductSku
	 */
	public static final int CUSTOMER_SHOP_PRODUCT_SKU_ISNULL = -6006;
	/**
	 * 订单状态没有定义
	 */
	public static final int CUSTOMER_ORDER_STATUS_TYPE_NOT_IN_DEFINED = -6007;
	/**
	 * 退款申请状态没有定义
	 */
	public static final int CUSTOMER_RETURNAPPLICATION_STATUS_TYPE_NOT_IN_DEFINED = -6008;
	/**
	 * 更新用户购物车时 - 没有传用户商店购物车id
	 */
	public static final int CUSTOMER_CART_UPDATE_USER_SHOP_CART_ID_NOT_DEFINED = -6009;
	/**
	 * 更新用户购物车时 - 没有传用户数量
	 */
	public static final int CUSTOMER_CART_UPDATE_AMOUNT_NOT_DEFINED = -6010;
	/**
	 * 用户购物车checkout时 - 没有传 shop_product_sku_id
	 */
	public static final int CUSTOMER_CART_PRE_CHECKOUT_SHOP_PRODUCT_SKU_ID_NOT_DEFINED = -6011;
	/**
	 * 聊天室用户列表为空
	 */
	public static final int CUSTOMER_CHAT_USER_LIST_IS_NULL_OR_EMPTY = -6012;
	/**
	 * 聊天 用户内容为空ChatUserContent is null
	 */
	public static final int CUSTOMER_CHAT_USER_CONTENT_IS_NULL_OR_EMPTY = -6013;
	/**
	 * 创建用户喜爱商铺时 商铺不存在
	 */
	public static final int CUSTOMER_FAVOURITE_STORE_SHOP_IS_NOT_EXIST = -6014;
	/**
	 * 邮箱不存在
	 */
	public static final int CUSTOMER_USERE_EMAIL_NOT_EXIST = -6015;
	/**
	 * 找不到 order
	 */
	public static final int CUSTOMER_ORDER_NOT_EXIST = -6016;
	/**
	 * 申请退换的订单状态不是“已拒绝”
	 */
	public static final int CUSTOMER_RETURN_APPLICATION_STATUS_IS_NOT_REJECTED = -6017;
	/**
	 * 申请退换订单不存在
	 */
	public static final int CUSTOMER_RETURN_APPLICATION_STATUS_IS_NOT_EXISTED = -6018;
	/**
	 * 找不到城市
	 */
	public static final int CUSTOMER_ADDRESS_CITY_IS_NOT_EXISTED = -6019;
	/**
	 * 找不到店铺商品
	 */
	public static final int CUSTOMER_SHOP_PRODUCT_IS_NOT_EXISTED = -6020;

	public static final int CUSTOMER_ACCOUNT_UP_TO_DATE = -6021;

	public static final int SKUSTORE_NOT_ENOUGN = -6022;

	/**
	 * 执行更新供货价JOB失败
	 */
	public static final int UPDATE_SUPLLY_PRICE_FAILURE = -7001;

	public static final int UPDATE_SALES_PRICE_FAILURE = -7002;
	//商品编码不存在
	public static final int PRODUCT_CODE_NOT_EXIST = -7003;

	/**
	 * 找不到门店编码
	 */
	public static final int STORE_CODE_NOT_EXISTED = -7004;

	/** Boutique ID already exists */
	public static final int PRODUCT_ALREADY_EXISTS = -100001;

}
