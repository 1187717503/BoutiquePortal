package pk.shoplus.data;


import pk.shoplus.model.*;

import java.util.Date;

/**
 * Created by chone on 2017/4/13.
 */
public class ProductStatus {


    private int approvalStatus;


    private String rejectionSeason;


    private String rejectionBy;


    private Date rejectionAt;


    public Date getRejectionAt(Date rejectionAt) {
        return rejectionAt;
    }


    public ProductStatus setRejectionAt(Date rejectionAt) {
        this.rejectionAt = rejectionAt;
        return this;
    }


    public String getRejectionBy() {
        return rejectionBy;
    }


    public ProductStatus setRejectionBy(String rejectionBy) {
        this.rejectionBy = rejectionBy;
        return this;
    }


    public String getRejectionSeason() {
        return rejectionSeason;
    }


    public ProductStatus setRejectionSeason(String rejectionSeason) {
        this.rejectionSeason = rejectionSeason;
        return this;
    }


    public int getApprovalStatus() {
        return approvalStatus;
    }


    public ProductStatus setApprovalStatus(int approvalStatus) throws InvalidApprovalStatusException {
        if (approvalStatus == ProductApprovalStatus.APPROVED ||
                approvalStatus == ProductApprovalStatus.REJECTED ||
                approvalStatus == ProductApprovalStatus.PENDING ||
                approvalStatus == ProductApprovalStatus.MODIFIED ||
                approvalStatus == ProductApprovalStatus.OFF ||
                approvalStatus == ProductApprovalStatus.MODIFIY_REJECTED) {
            this.approvalStatus = approvalStatus;
        } else {
            throw new InvalidApprovalStatusException();
        }
        return this;
    }


    public class InvalidApprovalStatusException extends Exception {

    }

}
