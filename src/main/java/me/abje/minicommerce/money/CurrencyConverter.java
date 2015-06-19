package me.abje.minicommerce.money;

import me.abje.minicommerce.config.MinicommerceConfig;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CurrencyConverter {
    @Autowired
    private MinicommerceConfig config;

    private Map<CurrencyUnit, Double> rates;

    public CurrencyConverter() {
        this.rates = new HashMap<>();
    }

    public void setRates(List<Rate> rateList) {
        for (Rate rate : rateList) {
            rates.put(CurrencyUnit.of(rate.Name.replaceFirst("[A-Z][A-Z][A-Z]/", "")),
                    Double.parseDouble(rate.Rate));
        }
    }

    public Map<CurrencyUnit, Double> getRates() {
        return rates;
    }

    public double getRate(CurrencyUnit unit) {
        return rates.get(unit);
    }

    public Money convertTo(Money money, CurrencyUnit currency) {
        if (money.getCurrencyUnit().equals(currency) || !rates.containsKey(currency))
            return money;
        return money.convertedTo(currency, BigDecimal.valueOf(rates.get(currency)), RoundingMode.HALF_UP);
    }

    public Money convertToDefault(Money money) {
        return convertTo(money, config.getCurrency());
    }

    public static String prettify(Money money) {
        NumberFormat format = NumberFormat.getCurrencyInstance(LocaleContextHolder.getLocale());
        format.setCurrency(money.getCurrencyUnit().toCurrency());
        return format.format(money.getAmount());
    }
}
