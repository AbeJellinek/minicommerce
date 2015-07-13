package me.abje.minicommerce;

import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.Cart;
import me.abje.minicommerce.db.CartRepository;
import me.abje.minicommerce.db.User;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ControllerAdvice
public class MiniControllerAdvice {
    @ModelAttribute("user")
    public User getCurrentUser(Authentication authentication) {
        return authentication == null ? null : (User) authentication.getPrincipal();
    }

    @Autowired
    protected MinicommerceConfig config;

    @Autowired
    protected CartRepository carts;

    @ModelAttribute("siteName")
    public String getSiteName() {
        return config.getSiteName();
    }

    @ModelAttribute("_csrf")
    public CsrfToken getCsrf(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @ModelAttribute("cart")
    public Cart getCart(HttpSession session) {
        Integer cartId = (Integer) session.getAttribute("cart");
        Cart cart;
        if (cartId == null) {
            cart = carts.save(new Cart(config.getCurrency()));
            session.setAttribute("cart", cart.getId());
        } else {
            cart = carts.findOne(cartId);
        }
        return cart;
    }

    @ModelAttribute("defaultCurrency")
    public CurrencyUnit getDefaultCurrency() {
        return config.getCurrency();
    }

    @ModelAttribute("userCurrency")
    public CurrencyUnit getCurrency(HttpSession session) {
        CurrencyUnit currency = (CurrencyUnit) session.getAttribute("currency");
        if (currency != null)
            return currency;
        session.setAttribute("currency", currency = getDefaultCurrency());
        return currency;
    }
}
