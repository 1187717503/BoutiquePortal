
package pk.shoplus.data;


public class Store {


    private String id;


    private String vendorId;


    private String apiId;


    private String endpointId;


    public String getApiId() {
        return apiId;
    }


    public Store setApiId(String id) {
        apiId = id;
        return this;
    }


    public String getEndpointId() {
        return endpointId;
    }


    public Store setEndpointId(String id) {
        endpointId = id;
        return this;
    }


    public String getVendorId() {
        return vendorId;
    }


    public Store setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }


    public String getId() {
        return id;
    }


    public Store setId(String id) {
        this.id = id;
        return this;
    }

}


