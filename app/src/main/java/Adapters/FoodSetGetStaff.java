package Adapters;

public class FoodSetGetStaff {
    private String foodPrice;
    private String foodName;
    private String foodStatus;
    private String itemImage;
    private String soldNumber;
    private String menuID;


    public FoodSetGetStaff(String foodPrice, String foodName, String foodStatus, String itemImage, String soldNumber, String menuID) {
        this.foodPrice = foodPrice;
        this.foodName = foodName;
        this.foodStatus = foodStatus;
        this.itemImage = itemImage;
        this.soldNumber = soldNumber;
        this.menuID = menuID;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodStatus() {
        return foodStatus;
    }

    public void setFoodStatus(String foodStatus) {
        this.foodStatus = foodStatus;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getSoldNumber() {
        return soldNumber;
    }

    public void setSoldNumber(String soldNumber) {
        this.soldNumber = soldNumber;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
