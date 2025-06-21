package coffeeshop.model;

import java.math.BigDecimal;

public class MenuItem {
    private int id;
    private String name;
    private String type; 
    private BigDecimal price;
    private String imageFilename;

    public MenuItem(int id, String name, String type, BigDecimal price, String imageFilename) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageFilename = imageFilename;
    }

    public MenuItem(String name, String type, BigDecimal price, String imageFilename) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.imageFilename = imageFilename;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public BigDecimal getPrice() { return price; }
    public String getImageFilename() { return imageFilename; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImageFilename(String imageFilename) { this.imageFilename = imageFilename; }

    @Override
    public String toString() {
        return "MenuItem{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", price=" + price +
               ", imageFilename='" + imageFilename + '\'' +
               '}';
    }
}
