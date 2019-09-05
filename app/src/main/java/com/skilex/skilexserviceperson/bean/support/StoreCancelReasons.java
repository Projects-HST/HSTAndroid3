package com.skilex.skilexserviceperson.bean.support;

public class StoreCancelReasons {

    private String cancelMasterId;
    private String cancelReason;

    public StoreCancelReasons(String cancelMasterId, String cancelReason) {
        this.cancelMasterId = cancelMasterId;
        this.cancelReason = cancelReason;
    }

    public String getCancelMasterId() {
        return cancelMasterId;
    }

    public void setCancelMasterId(String cancelMasterId) {
        this.cancelMasterId = cancelMasterId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String timeName) {
        this.cancelReason = timeName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return cancelReason;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreCancelReasons) {
            StoreCancelReasons c = (StoreCancelReasons) obj;
            if (c.getCancelReason().equals(cancelReason) && c.getCancelMasterId() == cancelMasterId) return true;
        }

        return false;
    }
}
