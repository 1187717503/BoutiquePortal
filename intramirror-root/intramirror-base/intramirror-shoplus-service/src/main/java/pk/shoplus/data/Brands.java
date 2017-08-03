
package pk.shoplus.data;

import org.sql2o.Connection;
import pk.shoplus.service.BrandService;


public class Brands {


    public static Brands.List list(String part) {
        return new Brands.List(part);
    }



    public static class List {

        
        private Part part;


        private String englishName;


        public List(String part) {
            this.part = new Part(part);
        }


        public Brands.List setEnglishName(String englishName) {
            this.englishName = englishName;
            return this;
        }


        public Brands.ListResponse execute() {
            Connection conn = DataBase.getConnection();
            BrandService brandService = new BrandService(conn);
            Brands.ListResponse response = new Brands.ListResponse();
            java.util.List<Brand> items = new java.util.ArrayList<Brand>();

            if (this.englishName != null) {
                pk.shoplus.model.Brand res = 
                    brandService.getBrandByEnglishName(englishName);
                if (res != null) {
                    Brand brand = new Brand();
                    if (part.contains("id")) {
                        brand.setId(res.brand_id.toString());
                    }
                    items.add(brand);
                }
            }
            response.setItems(items);
            return response;
        }


    }


    public static class ListResponse {


        private java.util.List<Brand> items;


        private Brands.ListResponse setItems(
                java.util.List<Brand> items) {
            this.items = items;
            return this;
        }


        public java.util.List<Brand> getItems() {
            return items;
        }


        public boolean isEmpty() {
            return items.size() <= 0;
        }


    }



}

