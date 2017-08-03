
package pk.shoplus.data;

import java.util.HashMap;
import java.util.Map;

import org.sql2o.Connection;

import pk.shoplus.model.ApiConfiguration;
import pk.shoplus.service.ApiConfigurationService;


public class Stores {


    public static Stores.List list(String part) {
        return new Stores.List(part);
    }


    public static class List {

        
        private Part part;


        private Map<String, Object> params;


        public List(String part) {
            this.part = new Part(part);
            params = new HashMap<>();
        }


        public Stores.List setId(String id) {
            params.put("id", id);
            return this;
        }


        public Stores.ListResponse execute() throws Exception {
            Connection conn = DataBase.getConnection();
            ApiConfigurationService apiConfigurationService = 
                new ApiConfigurationService(conn);

            java.util.List<Store> items = new java.util.ArrayList<>();
            Stores.ListResponse response = new Stores.ListResponse();

            if (params.get("id") != null) {
                // TODO: use sql method replace api configuration service
                Store store = new Store();
                Map<String, Object> condition = new HashMap<>();
                condition.put("store_code", params.get("id"));
                condition.put("system", "intramirror");
                ApiConfiguration res =
                    apiConfigurationService.getMappingByCondition(condition, null);
                if (res != null) {
                    if (part.contains("id")) {
                        store.setId(res.store_code);
                    }
                    if (part.contains("vendorId")) {
                        store.setVendorId(res.vendor_id.toString());
                    }
                    store.setApiId(res.api_configuration_id.toString());
                    store.setEndpointId(res.api_end_point_id.toString());
                    items.add(store);
                }
            }

            response.setItems(items);
            return response;
        }


    }


    public static class ListResponse {


        private java.util.List<Store> items;


        private Stores.ListResponse setItems(
                java.util.List<Store> items) {
            this.items = items;
            return this;
        }


        public java.util.List<Store> getItems() {
            return items;
        }


        public boolean isEmpty() {
            return items.isEmpty();
        }


    }



}

