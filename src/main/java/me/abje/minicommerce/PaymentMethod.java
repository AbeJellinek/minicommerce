package me.abje.minicommerce;

import me.abje.minicommerce.db.Cart;

public interface PaymentMethod {
    public SuccessResponse buy(Checkout checkout, Cart cart);
}
