package pk.shoplus.model;

import java.util.Date;
import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Entity;
import pk.shoplus.model.annotation.Id;

@Entity("boutique_image")
public class BoutiqueImage {
    @Id
    public Long boutique_image_id;

    @Column
    public Long product_id;

    @Column
    public String image;

    @Column
    public Date created_at;

    @Column
    public Date updated_at;

    public Long getBoutique_image_id() {
        return boutique_image_id;
    }

    public void setBoutique_image_id(Long boutique_image_id) {
        this.boutique_image_id = boutique_image_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

}
