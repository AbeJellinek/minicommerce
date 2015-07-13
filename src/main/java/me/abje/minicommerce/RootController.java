package me.abje.minicommerce;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import com.google.common.base.Joiner;
import com.stripe.Stripe;
import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.Cart;
import me.abje.minicommerce.db.CartRepository;
import me.abje.minicommerce.db.Product;
import me.abje.minicommerce.db.ProductRepository;
import me.abje.minicommerce.money.CurrencyConverter;
import me.abje.minicommerce.money.Rate;
import me.abje.minicommerce.money.RatesResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private HandlebarsViewResolver handlebarsViewResolver;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private List<CurrencyUnit> currencies;

    @PostConstruct
    private void postConstruct() {
        Stripe.apiKey = config.getStripeSecret();

        currencies = new ArrayList<>(Arrays.asList(CurrencyUnit.of("USD"), CurrencyUnit.of("JPY"), CurrencyUnit.of("GBP"),
                CurrencyUnit.of("CHF"), CurrencyUnit.of("AUD"), CurrencyUnit.of("CAD"), CurrencyUnit.of("MXN")));
        currencies.sort(Comparator.naturalOrder());
        currencies.remove(config.getCurrency());

        handlebarsViewResolver.<String>registerHelper("i", (context, options) -> {
            Locale locale = LocaleContextHolder.getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            return new MessageFormat(bundle.getString(context), locale).format(options.params);
        });

        handlebarsViewResolver.<CurrencyUnit>registerHelper("currencyName", (context, options) ->
                context.toCurrency().getDisplayName(LocaleContextHolder.getLocale()));

        //noinspection ResultOfMethodCallIgnored
        new File("uploaded/").mkdir();
    }

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

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("inBrowse", true);
        model.addAttribute("products", products.findAll(new Sort(Sort.Direction.ASC, "name")));
        return "index";
    }

    @RequestMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("inCart", true);
        return "cart";
    }

    @RequestMapping(value = "/cart/quantity/{id}/{quantity}", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse updateQuantity(@PathVariable("id") int id, @PathVariable("quantity") int quantity,
                                          Cart cart) {
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
    public SuccessResponse addToCart(@PathVariable("id") Product product, Cart cart) {
        cart.add(product, 1);
        carts.save(cart);
        return new CartResponse(true, cart);
    }

    @RequestMapping(value = "/cart/remove/{id}", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse removeFromCart(@PathVariable("id") int id, Cart cart) {
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
    public String checkout(Model model, Cart cart) {
        if (cart.getTotalQuantity() == 0)
            return "redirect:/";

        model.addAttribute("stripePublic", config.getStripePublic());
        model.addAttribute("inCart", true);
        model.addAttribute("cartReadOnly", true);
        return "checkout";
    }

    @RequestMapping(value = "/checkout/payment", method = RequestMethod.POST)
    @ResponseBody
    public SuccessResponse checkoutPayment(@Valid Checkout checkout, Cart cart, HttpSession session) {
        if (cart.getTotalQuantity() == 0)
            return new SuccessResponse(false, "Your cart is empty.");

        beanFactory.autowireBean(checkout.getPayment());
        SuccessResponse response = checkout.getPayment().buy(checkout, cart);

        if (response.isSuccess()) {
            cart.setOld(true);
            session.setAttribute("cart", null);
        }

        return response;
    }

    @RequestMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") Product product, Model model, Cart cart) {
        model.addAttribute("inBrowse", true);
        model.addAttribute("product", product);
        model.addAttribute("canAdd", !cart.containsProduct(product));
        return "viewProduct";
    }

    @RequestMapping("/login")
    public String login() {
        return "th/login";
    }

    @RequestMapping("/currency/set/{currencyCode}")
    @ResponseBody
    public SuccessResponse setCurrency(@PathVariable("currencyCode") String currencyCode, HttpSession session) {
        CurrencyUnit currency = CurrencyUnit.of(currencyCode);
        session.setAttribute("currency", currency);
        return new SuccessResponse(true, "");
    }

    @RequestMapping("/currency/convert/{amount}")
    @ResponseBody
    public String convertCurrency(@PathVariable("amount") String amount, HttpSession session) {
        CurrencyUnit currency = (CurrencyUnit) session.getAttribute("currency");
        if (currency == null || currency.equals(config.getCurrency()))
            return "";
        return CurrencyConverter.prettify(converter.convertTo(Money.of(config.getCurrency(),
                Double.parseDouble(amount.replaceAll("[^0-9\\.]", ""))), currency));
    }

    @RequestMapping("/search/{query}")
    @ResponseBody
    public SearchResponse search(@PathVariable("query") String query, HttpSession session) {
        List<Product> productList = products.findByNameContaining(query);
        CurrencyUnit currency = (CurrencyUnit) session.getAttribute("currency");

        List<SearchResponse.Result> results =
                productList.stream().map(product ->
                        new SearchResponse.Result(
                                product.getName(),
                                "/product/" + product.getId(),
                                CurrencyConverter.prettify(converter.convertTo(product.getPrice(), currency)),
                                StringUtils.abbreviate(product.getDescription(), 100))).
                        collect(Collectors.toList());
        return new SearchResponse(true, results);
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
        String joined = Joiner.on(", ").appendTo(new StringBuilder("("), currencies.stream().map(currency ->
                "\"" + config.getCurrencyCode() + currency.getCode() + "\"").iterator()).append(')').toString();
        RestTemplate restTemplate = new RestTemplate();
        RatesResponse ratesResponse = restTemplate.getForObject("http://query.yahooapis.com/v1/public/yql?q=" +
                "select * from yahoo.finance.xchange where pair in " +
                joined +
                "&env=store://datatables.org/alltableswithkeys&format=json", RatesResponse.class);
        RatesResponse.Query query = ratesResponse.query;
        List<Rate> rates = query.results.get("rate");
        converter.setRates(rates);
    }
}
