package pk.shoplus.model;

import pk.shoplus.model.annotation.Column;
import pk.shoplus.model.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentEasypay {
    @Id
    public Long payment_easypay_id;
    @Column
    public int store_id;
    @Column
    public Long order_id;
    @Column
    public BigDecimal amount;
    @Column
    public Date expire_date;
    @Column
    public String auth_token;
    @Column
    public int status;
    @Column
    public String description;
    @Column
    public Date created_at;
    @Column
    public Date updated_at;
}
