
package pk.shoplus.data;

import org.sql2o.Connection;
import pk.shoplus.service.CategoryService;

import java.util.HashMap;
import java.util.Map;


public class ProductCategories {


    public static ProductCategories.List list(String part) {
        return new ProductCategories.List(part);
    }



    public static class List {


        private Part part;


        private Map<String, Object> params;


        public List(String part) {
            this.part = new Part(part);
            params = new HashMap<>();
        }


        public ProductCategories.List setId(String id) {
            params.put("id", id);
            return this;
        }


        public ProductCategories.ListResponse execute() throws Exception {
            Connection conn = DataBase.getConnection();
            CategoryService service = new CategoryService(conn);

            ProductCategories.ListResponse response = new ProductCategories.ListResponse();
            java.util.List<Category> items = new java.util.ArrayList<Category>();

            if (params.containsKey("id")) {
                pk.shoplus.model.Category res =
                        service.getCategoryById(Long.parseLong((String)params.get("id")));
                if (res != null) {
                    Category category = new Category();
                    if (part.contains("id")) {
                        category.setId(res.category_id.toString());
                    }
                    if (part.contains("snippet")) {
                        ProductCategorySnippet snippet = new ProductCategorySnippet();
                        snippet.setTitle(res.chinese_name);
                    }
                    items.add(category);
                }
            }

            response.setItems(items);
            return response;
        }


    }


    
    public static class ListResponse {


        private java.util.List<Category> items;


        private ProductCategories.ListResponse setItems(
                java.util.List<Category> items) {
            this.items = items;
            return this;
        }


        public java.util.List<Category> getItems() {
            return items;
        }


        public boolean isEmpty() {
            return items.size() <= 0;
        }


    }




}
