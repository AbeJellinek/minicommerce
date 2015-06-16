package me.abje.minicommerce;

import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.Cart;
import me.abje.minicommerce.db.CartRepository;
import me.abje.minicommerce.db.Product;
import me.abje.minicommerce.db.ProductRepository;
import me.abje.minicommerce.money.CurrencyConverter;
import me.abje.minicommerce.money.Rate;
import me.abje.minicommerce.money.RatesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ListIterator;

@Controller
public class RootController {
    @Autowired
    private CurrencyConverter converter;

    @Autowired
    private CartRepository carts;

    @Autowired
    private MinicommerceConfig config;

    @Autowired
    private ProductRepository products;

    @ModelAttribute("siteName")
    public String getSiteName() {
        return config.getSiteName();
    }

    @ModelAttribute("_csrf")
    public CsrfToken getCsrf(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @ModelAttribute("cart")
    public Cart cart(HttpSession session) {
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

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("inBrowse", true);
        model.addAttribute("products", products.findAll());
        return "index";
    }

    @RequestMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("inCart", true);
        return "cart";
    }

    @RequestMapping(value = "/cart/quantity/{id}/{quantity}", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse updateQuantity(@PathVariable("id") int id, @PathVariable("quantity") int quantity,
                                          HttpSession session) {
        Cart cart = cart(session);

        for (ListIterator<Cart.Item> iterator = cart.getItems().listIterator(); iterator.hasNext(); ) {
            Cart.Item item = iterator.next();
            if (item.getProduct().getId() == id) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    iterator.remove();
                }
                break;
            }
        }

        carts.save(cart);

        return new CartResponse(true, cart);
    }

    @RequestMapping(value = "/cart/add/{id}", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse addToCart(@PathVariable("id") Product product, HttpSession session) {
        Cart cart = cart(session);
        cart.add(product, 1);
        carts.save(cart);
        return new CartResponse(true, cart);
    }

    @RequestMapping(value = "/cart/remove/{id}", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse removeFromCart(@PathVariable("id") int id, HttpSession session) {
        Cart cart = cart(session);
        for (ListIterator<Cart.Item> iterator = cart.getItems().listIterator(); iterator.hasNext(); ) {
            Cart.Item item = iterator.next();
            if (item.getProduct().getId() == id) {
                iterator.remove();
                break;
            }
        }
        carts.save(cart);

        return new CartResponse(true, cart);
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkout(Model model) {
        model.addAttribute("inCart", true);
        model.addAttribute("cartReadOnly", true);
        return "checkout";
    }

    @RequestMapping(value = "/checkout/payment", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse checkoutPayment() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SuccessResponse(true);
    }

    @RequestMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") Product product, Model model) {
        model.addAttribute("inBrowse", true);
        model.addAttribute("product", product);
        return "viewProduct";
    }

    @RequestMapping("/login")
    public String login() {
        return "th/login";
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

            container.addErrorPages(error401Page, error404Page, error500Page);
        });
    }

    @Scheduled(fixedRate = 24 /* hours */ * 60 /* minutes */ * 60 /* seconds */ * 1000 /* milliseconds */)
    public void updateConversionRates() {
        RestTemplate restTemplate = new RestTemplate();
        RatesResponse ratesResponse = restTemplate.getForObject("http://query.yahooapis.com/v1/public/yql?q=" +
                "select * from yahoo.finance.xchange where pair in " +
                String.format("(\"%1$sUSD\", \"%1$sEUR\", \"%1$sJPY\", \"%1$sGBP\", \"%1$sCHF\", \"%1$sAUD\", \"%1$sCAD\")", config.getCurrencyCode()) +
                "&env=store://datatables.org/alltableswithkeys&format=json", RatesResponse.class);
        RatesResponse.Query query = ratesResponse.query;
        List<Rate> rates = query.results.get("rate");
        converter.setRates(rates);
    }
}
