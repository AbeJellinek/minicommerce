package me.abje.minicommerce.config;

import org.joda.money.CurrencyUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(locations = "classpath:minicommerce.properties", prefix = "minicommerce")
public class MinicommerceConfig {
    private CurrencyUnit currency;
    private String theme;
    private String siteName;
    private String stripePublic;
    private String stripeSecret;

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    @NotNull
    public String getCurrencyCode() {
        return currency.getCurrencyCode();
    }

    public void setCurrencyCode(String currencyCode) {
        this.currency = CurrencyUnit.of(currencyCode);
    }

    @NotNull
    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @NotNull
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @NotNull
    public String getStripePublic() {
        return stripePublic;
    }

    public void setStripePublic(String stripePublic) {
        this.stripePublic = stripePublic;
    }

    @NotNull
    public String getStripeSecret() {
        return stripeSecret;
    }

    public void setStripeSecret(String stripeSecret) {
        this.stripeSecret = stripeSecret;
    }
}
