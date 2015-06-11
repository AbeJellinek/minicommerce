package me.abje.minicommerce;

import me.abje.minicommerce.db.Message;
import me.abje.minicommerce.db.MessageRepository;
import me.abje.minicommerce.db.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private CurrentUserControllerAdvice currentUserControllerAdvice;

    @Autowired
    private MessageRepository messages;

    private User user() {
        return currentUserControllerAdvice.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
    }

    @RequestMapping
    public String dashboard(Model model) {
        model.addAttribute("messages", messages.findAll());
        return "th/dashboard";
    }

    @RequestMapping(value = "/ajax/message/{id}/dismiss", method = RequestMethod.POST)
    @ResponseBody
    public String dismissMessage(@PathVariable("id") Message message) {
        messages.delete(message);
        return "";
    }
}
