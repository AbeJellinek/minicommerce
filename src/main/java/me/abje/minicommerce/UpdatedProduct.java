package me.abje.minicommerce;

import me.abje.minicommerce.db.Product;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class UpdatedProduct {
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private boolean shippable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isShippable() {
        return shippable;
    }

    public void setShippable(boolean shippable) {
        this.shippable = shippable;
    }

    public void on(Product product, CurrencyUnit currency) {
        product.setName(name);
        product.setPrice(Money.of(currency, price));
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setShippable(shippable);
    }
}
