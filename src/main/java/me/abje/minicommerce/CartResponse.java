package me.abje.minicommerce;

import me.abje.minicommerce.db.Cart;

public class CartResponse extends SuccessResponse {
    private final Cart cart;

    public CartResponse(boolean success, Cart cart) {
        super(success, "");
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }
}
