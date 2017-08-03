package pk.shoplus.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson15.JSONArray;
import com.alibaba.fastjson15.JSONObject;

import pk.shoplus.common.FileUploadHelper;
import pk.shoplus.data.Brand;
import pk.shoplus.data.Brands;
import pk.shoplus.data.Category;
import pk.shoplus.data.Product;
import pk.shoplus.data.ProductCategories;
import pk.shoplus.data.ProductContentDetails;
import pk.shoplus.data.ProductSku;
import pk.shoplus.data.ProductSkuDetails;
import pk.shoplus.data.ProductSnippet;
import pk.shoplus.data.Products;
import pk.shoplus.data.Store;
import pk.shoplus.data.Stores;
import pk.shoplus.data.Thumbnail;
import pk.shoplus.data.ThumbnailDetails;

public class ProductAtelierManagement {
	public Map<String,Object> createProduct(ProductAtelierOptions productOptions){
		  Map<String, Object> result = new HashMap<String, Object>();

	        try {
	            JSONObject data = JSONObject.parseObject(productOptions.getBody());
	            String storeID = productOptions.getStoreId();
	            String version = productOptions.getVersion();
	            if (version == null || !version.equals("1.0")) {
	                return ResultHelper.createErrorResult(
	                        "1", "E001: Parameter Version is mandatory");
	            }


	            if (data == null) {
	                return ResultHelper.createErrorResult(
	                        "30", "E030: Empty Request");
	            }

	            // check boutique id
	            String boutiqueId = data.getString("boutique_id");
	            if (boutiqueId == null || boutiqueId.equals("")) {
	                return ResultHelper.createErrorResult("205", "E205: Invalid boutique_id");
	            }

	            // check store id & find vendor id
	            Stores.ListResponse storeResponse = Stores.list("id,vendorId")
	                    .setId(storeID)
	                    .execute();
	            if (storeResponse.isEmpty()) {
	                return ResultHelper.createErrorResult(
	                        "1", "E001: Parameter StoreID is mandatory");
	            }
	            Store store = storeResponse.getItems().get(0);

	            // Boutique id is the unique id of product in context sync data from really store.
	            // If we find the given boutique id in product data return failure.
	            Products.ListResponse productResponse = Products.list("id").setCode(boutiqueId).execute();
	            if (!productResponse.isEmpty()) {
	                return ResultHelper.createErrorResult("204", "E204: Product duplicate");
	            }

	            // check brand
	            Brands.ListResponse brandList = Brands.list("id")
	                    .setEnglishName(data.getString("brand"))
	                    .execute();
	            if (brandList.isEmpty()) {
	                return ResultHelper.createErrorResult("203", "E203: Invalid brand");
	            }
	            Brand brand = brandList.getItems().get(0);


	            // check category
	            ProductCategories.ListResponse categoryList = ProductCategories.list("id")
	                    .setId(data.getString("category_id"))
	                    .execute();
	            if (categoryList.isEmpty()) {
	                return ResultHelper.createErrorResult("201", "E201: Invalid category_id");
	            }
	            Category category = categoryList.getItems().get(0);


	            // create snippet
	            ProductSnippet snippet = new ProductSnippet();
	            snippet
	                    .setBrandId(brand.getId())
	                    .setCategoryId(category.getId())
	                    .setVendorId(store.getVendorId())
	                    .setDescription(data.getString("product_description"))
	                    .setThumbnails(toThumbnails(data.getJSONArray("cover_img")))
	                    .setTitle(data.getString("product_name"));


	            // create content details
	            ProductContentDetails contentDetails = new ProductContentDetails();
	            contentDetails
	                    .setThumbnails(toThumbnails(data.getJSONArray("description_img")))
	                    .setSeasonCode(data.getString("season_code"))
	                    .setColorCode(data.getString("color_code"))
	                    .setColorDescription(data.getString("color_description"))
	                    .setBrandCode(data.getString("brand_id"))
	                    .setMadeIn(data.getString("made_in"))
	                    .setSizeFit(data.getString("size_fit"))
	                    .setSizeCountry(data.getString("size_country"))
	                    .setWidth(data.getBigDecimal("width"))
	                    .setHeight(data.getBigDecimal("height"))
	                    .setLength(data.getBigDecimal("length"))
	                    .setWeight(data.getBigDecimal("weight"))
	                    .setComposition(data.getString("composition"))
	                    .setCarryOver(data.getString("carry_over"));


	            // create sku details
	            ProductSkuDetails skuDetails = new ProductSkuDetails();
	            JSONArray skus = data.getJSONArray("sku");
	            for (Object it : skus) {
	                JSONObject item = (JSONObject) it;
	                ProductSku sku = new ProductSku();
	                sku.setBarcode(item.getString("barcode"))
	                        .setSize(item.getString("size"))
	                        .setStock(item.getBigDecimal("stock"))
	                        .setPrice(data.getBigDecimal("sale_price"));
	                skuDetails.add(sku);
	            }


	            // create product
	            Product product = new Product();
	            product
	                    .setCode(data.getString("boutique_id"))
	                    .setSnippet(snippet)
	                    .setSkuDetails(skuDetails)
	                    .setContentDetails(contentDetails);


	            // inset
	            Products
	                    .insert("code,snippet,skuDetails,contentDetails", product)
	                    .execute();

	            result.put("ResponseStatus", "1000");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResultHelper.createErrorResult(
	                    "6", "E006: Unexpected internal error");
	        }

	        return result;
	}

    private ThumbnailDetails toThumbnails(JSONArray arr) throws Exception {
        ThumbnailDetails thumbnails = new ThumbnailDetails();
        if (arr != null) {
            for (Object image : arr) {
                List<String> list = FileUploadHelper.uploadFileByImgUrl((String) image);
                String url = list.get(0);
                Thumbnail thumbnail = new Thumbnail(url);
                thumbnails.add(thumbnail);
            }
        }
        return thumbnails;
    }
    
    public class ProductAtelierOptions{
    	private String body;
    	private String storeId;
    	private String version;
    	
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public String getStoreId() {
			return storeId;
		}
		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
    	
    	
    }
}
