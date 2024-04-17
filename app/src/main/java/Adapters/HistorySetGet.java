package Adapters;


public class HistorySetGet {
    private String food_name;
    private String food_price;
    private String coupon_reference_Number;
    private String coupon_date;
    private String coupon_status;
    private String coupon_serveTime;

    public HistorySetGet(final String food_name, final String food_price, final String coupon_reference_Number, final String coupon_date, final String coupon_status, final String coupon_serveTime) {
        this.food_name = food_name;
        this.food_price = food_price;
        this.coupon_reference_Number = coupon_reference_Number;
        this.coupon_date = coupon_date;
        this.coupon_status = coupon_status;
        this.coupon_serveTime = coupon_serveTime;
    }

    public String getFood_name() {
        return this.food_name;
    }

    public void setFood_name(final String food_name) {
        this.food_name = food_name;
    }

    public String getFood_price() {
        return this.food_price;
    }

    public void setFood_price(final String food_price) {
        this.food_price = food_price;
    }

    public String getCoupon_reference_Number() {
        return this.coupon_reference_Number;
    }

    public void setCoupon_reference_Number(final String coupon_reference_Number) {
        this.coupon_reference_Number = coupon_reference_Number;
    }

    public String getCoupon_date() {
        return this.coupon_date;
    }

    public void setCoupon_date(final String coupon_date) {
        this.coupon_date = coupon_date;
    }

    public String getCoupon_status() {
        return this.coupon_status;
    }

    public void setCoupon_status(final String coupon_status) {
        this.coupon_status = coupon_status;
    }

    public String getCoupon_serveTime() {
        return this.coupon_serveTime;
    }

    public void setCoupon_serveTime(final String coupon_serveTime) {
        this.coupon_serveTime = coupon_serveTime;
    }
}