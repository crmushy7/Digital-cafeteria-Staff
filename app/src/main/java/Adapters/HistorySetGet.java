package Adapters;


public class HistorySetGet {
    private String food_name;
    private String food_price;
    private String coupon_reference_Number;
    private String coupon_date;
    private String coupon_status;
    private String coupon_serveTime;
    private String coupon_No;
    private String userID;

    public HistorySetGet(String food_name, String food_price, String coupon_reference_Number, String coupon_date, String coupon_status, String coupon_serveTime, String coupon_No, String userID) {
        this.food_name = food_name;
        this.food_price = food_price;
        this.coupon_reference_Number = coupon_reference_Number;
        this.coupon_date = coupon_date;
        this.coupon_status = coupon_status;
        this.coupon_serveTime = coupon_serveTime;
        this.coupon_No = coupon_No;
        this.userID = userID;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_price() {
        return food_price;
    }

    public void setFood_price(String food_price) {
        this.food_price = food_price;
    }

    public String getCoupon_reference_Number() {
        return coupon_reference_Number;
    }

    public void setCoupon_reference_Number(String coupon_reference_Number) {
        this.coupon_reference_Number = coupon_reference_Number;
    }

    public String getCoupon_date() {
        return coupon_date;
    }

    public void setCoupon_date(String coupon_date) {
        this.coupon_date = coupon_date;
    }

    public String getCoupon_status() {
        return coupon_status;
    }

    public void setCoupon_status(String coupon_status) {
        this.coupon_status = coupon_status;
    }

    public String getCoupon_serveTime() {
        return coupon_serveTime;
    }

    public void setCoupon_serveTime(String coupon_serveTime) {
        this.coupon_serveTime = coupon_serveTime;
    }

    public String getCoupon_No() {
        return coupon_No;
    }

    public void setCoupon_No(String coupon_No) {
        this.coupon_No = coupon_No;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}