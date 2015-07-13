package me.abje.minicommerce;

import me.abje.minicommerce.db.Cart;

public interface PaymentMethod {
    public JSONResponse buy(Checkout checkout, Cart cart);
}
