package com.intramirror.web.VO;

import java.util.List;

/**
 * Created by caowei on 2018/5/10.
 */
public class TransitWarehouseInvoiceVO {

    private InvoiceVO chinaInvoice;

    private List<InvoiceVO> UNInvoices;

    private List<InvoiceVO> otherInvoices;

    private List<InvoiceVO> comoInvoice;

    public List<InvoiceVO> getComoInvoice() {
        return comoInvoice;
    }

    public void setComoInvoice(List<InvoiceVO> comoInvoice) {
        this.comoInvoice = comoInvoice;
    }

    public InvoiceVO getChinaInvoice() {
        return chinaInvoice;
    }

    public void setChinaInvoice(InvoiceVO chinaInvoice) {
        this.chinaInvoice = chinaInvoice;
    }

    public List<InvoiceVO> getUNInvoices() {
        return UNInvoices;
    }

    public void setUNInvoices(List<InvoiceVO> UNInvoices) {
        this.UNInvoices = UNInvoices;
    }

    public List<InvoiceVO> getOtherInvoices() {
        return otherInvoices;
    }

    public void setOtherInvoices(List<InvoiceVO> otherInvoices) {
        this.otherInvoices = otherInvoices;
    }
}
