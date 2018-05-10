package com.intramirror.web.VO;

import java.util.List;
import java.util.Map;

/**
 * Created by caowei on 2018/5/10.
 */
public class InvoiceVO {

    private ShipperVO shipperVO;

    private RecipientVO recipientVO;

    private String invoiceTo;

    private String invoiceName;

    private String invoicePersonName;

    private String vatNum;

    private String invoiceDate;

    private String invoiceNum;

    private List<Map<String,Object>> list;

    private String remark = "Shipment exempt from VAT - IVA non imponibile Art. 8 1°C L/A DPR 633/72";

    public String getVatNum() {
        return vatNum;
    }

    public void setVatNum(String vatNum) {
        this.vatNum = vatNum;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public ShipperVO getShipperVO() {
        return shipperVO;
    }

    public void setShipperVO(ShipperVO shipperVO) {
        this.shipperVO = shipperVO;
    }

    public RecipientVO getRecipientVO() {
        return recipientVO;
    }

    public void setRecipientVO(RecipientVO recipientVO) {
        this.recipientVO = recipientVO;
    }

    public String getInvoiceTo() {
        return invoiceTo;
    }

    public void setInvoiceTo(String invoiceTo) {
        this.invoiceTo = invoiceTo;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getInvoicePersonName() {
        return invoicePersonName;
    }

    public void setInvoicePersonName(String invoicePersonName) {
        this.invoicePersonName = invoicePersonName;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
