package Adapters;

public class ImageModel {
    private String imageUrl;

    public ImageModel() {
        // Default constructor required for Firestore
    }

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
