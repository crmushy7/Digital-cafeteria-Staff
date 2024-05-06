package Adapters;

public class FoodSetGet {
    private String foodPrice;
    private String foodName;
    private String foodStatus;
    private String itemImage;
    private String menuAvailability;


    public FoodSetGet(final String foodPrice, final String foodName, final String foodStatus, final String itemImage, final String menuAvailability) {
        this.foodPrice = foodPrice;
        this.foodName = foodName;
        this.foodStatus = foodStatus;
        this.itemImage = itemImage;
        this.menuAvailability = menuAvailability;
    }


    public String getFoodPrice() {
        return this.foodPrice;
    }

    public void setFoodPrice(final String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodName() {
        return this.foodName;
    }

    public void setFoodName(final String foodName) {
        this.foodName = foodName;
    }

    public String getFoodStatus() {
        return this.foodStatus;
    }

    public void setFoodStatus(final String foodStatus) {
        this.foodStatus = foodStatus;
    }

    public String getItemImage() {
        return this.itemImage;
    }

    public void setItemImage(final String itemImage) {
        this.itemImage = itemImage;
    }

    public String getMenuAvailability() {
        return this.menuAvailability;
    }

    public void setMenuAvailability(final String menuAvailability) {
        this.menuAvailability = menuAvailability;
    }
}
