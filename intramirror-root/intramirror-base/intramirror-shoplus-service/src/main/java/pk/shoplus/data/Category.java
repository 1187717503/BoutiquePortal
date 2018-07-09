
package pk.shoplus.data;


public class Category {


    private String id;


    private ProductCategorySnippet snippet;



    public ProductCategorySnippet getSnippet() {
        return snippet;
    }


    public Category setSnippet(ProductCategorySnippet snippet) {
        this.snippet = snippet;
        return this;
    }


    public String getId() {
        return id;
    }


    public Category setId(String id) {
        this.id = id;
        return this;
    }


}
