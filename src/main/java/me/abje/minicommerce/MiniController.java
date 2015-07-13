package me.abje.minicommerce;

import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.Cart;
import me.abje.minicommerce.db.CartRepository;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Controller
public class MiniController {
    protected List<CurrencyUnit> currencies;

    @Autowired
    protected MinicommerceConfig config;

    @Autowired
    protected CartRepository carts;

    @ModelAttribute("siteName")
    public String getSiteName() {
        return config.getSiteName();
    }

    @ModelAttribute("currencies")
    public List<CurrencyUnit> getCurrencies() {
        return currencies;
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

    @PostConstruct
    private void postConstruct() {
        currencies = new ArrayList<>(Arrays.asList(CurrencyUnit.of("USD"), CurrencyUnit.of("JPY"), CurrencyUnit.of("GBP"),
                CurrencyUnit.of("CHF"), CurrencyUnit.of("AUD"), CurrencyUnit.of("CAD"), CurrencyUnit.of("MXN")));
        currencies.sort(Comparator.naturalOrder());
        currencies.remove(config.getCurrency());
    }
}
