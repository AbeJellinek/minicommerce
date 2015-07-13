package me.abje.minicommerce;

import com.google.common.collect.ImmutableMap;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.Cart;
import me.abje.minicommerce.db.UserOrder;
import me.abje.minicommerce.db.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class StripePaymentMethod implements PaymentMethod {
    @Autowired
    private UserOrderRepository orders;

    @Autowired
    private MinicommerceConfig config;

    private String stripeToken;

    public StripePaymentMethod(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    @Override
    public SuccessResponse buy(Checkout checkout, Cart cart) {
        try {
            String fullName = checkout.getFirstName() + " " + checkout.getLastName();
            ImmutableMap.Builder<String, Object> address = ImmutableMap.<String, Object>builder().
                    put("line1", checkout.getAddress1()).
                    put("city", checkout.getCity()).
                    put("postal_code", checkout.getPostalCode()).
                    put("country", checkout.getCountry());
            if (!checkout.getAddress2().isEmpty())
                address.put("line2", checkout.getAddress2());
            if (!checkout.getState().isEmpty())
                address.put("state", checkout.getState());
            Charge charge = Charge.create(ImmutableMap.of(
                    "amount", cart.getTotal().getAmountMinorInt(),
                    "currency", config.getCurrencyCode(),
                    "source", stripeToken,
                    "shipping", ImmutableMap.of(
                            "address", address.build(),
                            "name", fullName)));
            UserOrder order = orders.save(new UserOrder(fullName, checkout.getEmail(), cart,
                    new UserOrder.Address(checkout.getAddress1(), checkout.getAddress2(), checkout.getCity(),
                            checkout.getState(), checkout.getCountry(), checkout.getPostalCode()),
                    false, "stripe", charge.getId()));
            return new SuccessResponse(true, "");
        } catch (AuthenticationException | APIException | CardException |
                APIConnectionException | InvalidRequestException e) {
            e.printStackTrace();
            return new SuccessResponse(false, e.getMessage());
        }
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }
}
