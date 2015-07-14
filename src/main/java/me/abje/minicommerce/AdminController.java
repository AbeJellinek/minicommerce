package me.abje.minicommerce;

import me.abje.minicommerce.config.MinicommerceConfig;
import me.abje.minicommerce.db.*;
import me.abje.minicommerce.money.CurrencyConverter;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private MiniControllerAdvice miniControllerAdvice;

    @Autowired
    private MessageRepository messages;

    @Autowired
    private UserOrderRepository orders;

    @Autowired
    private MinicommerceConfig config;

    @Autowired
    private ProductRepository products;

    private User user() {
        return miniControllerAdvice.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
    }

    @RequestMapping
    public String dashboard(Model model) {
        model.addAttribute("messages", messages.findAll());
        model.addAttribute("recentOrders", orders.findAll().stream().
                limit(10).collect(Collectors.toList()));
        model.addAttribute("numOrders", orders.count());
        model.addAttribute("totalRevenue", CurrencyConverter.prettify(
                Money.of(config.getCurrency(), orders.findAll().stream().mapToDouble(order ->
                        order.getPaymentAmount().getAmount().doubleValue()).sum())));
        return "th/dashboard";
    }

    @RequestMapping(value = "/ajax/message/{id}/dismiss", method = RequestMethod.POST)
    @ResponseBody
    public String dismissMessage(@PathVariable("id") Message message) {
        messages.delete(message);
        return "";
    }

    @RequestMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", products.findAll());
        return "th/products";
    }

    @RequestMapping(value = "/products/{product}", method = RequestMethod.GET)
    public String editProduct(@PathVariable("product") Product product, Model model) {
        model.addAttribute("product", product);
        return "th/edit_product";
    }

    @RequestMapping(value = "/products/new", method = RequestMethod.GET)
    public String editNewProduct() {
        Product product = products.save(new Product("New Product", Money.zero(config.getCurrency()), "", "", false));
        return "redirect:/admin/products/" + product.getId();
    }

    @RequestMapping(value = "/products/{product}", method = RequestMethod.POST)
    public String postEditProduct(@PathVariable("product") Product product, UpdatedProduct update, Model model) {
        update.on(product, config.getCurrency());
        products.save(product);

        model.addAttribute("product", product);
        model.addAttribute("updated", true);
        return "th/edit_product";
    }

    @RequestMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseBody
    public JSONResponse uploadImage(@RequestPart("image") MultipartFile imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        File file = new File("uploaded/" + UUID.randomUUID() + ".png");
        ImageIO.write(image, "PNG", file);
        return new JSONResponse(true, "/img/uploaded/" + file.getName());
    }
}
