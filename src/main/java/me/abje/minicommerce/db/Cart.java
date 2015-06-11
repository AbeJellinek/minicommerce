package me.abje.minicommerce.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.abje.minicommerce.money.CurrencyConverter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Cart extends ModelBase implements Iterable<Cart.Item> {
    @ElementCollection
    private List<Item> items;
    @JsonIgnore
    private CurrencyUnit currency;

    public Cart() {
        this(null, null);
    }

    public Cart(CurrencyUnit currency) {
        this(new ArrayList<>(), currency);
    }

    public Cart(List<Item> items, CurrencyUnit currency) {
        this.items = items;
        this.currency = currency;
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    @JsonIgnore
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    public int getTotalQuantity() {
        if (items.isEmpty())
            return 0;
        return items.stream().collect(Collectors.summingInt(Item::getQuantity));
    }

    @JsonIgnore
    public Money getTotal() {
        if (items.isEmpty())
            return Money.zero(currency);
        return Money.total(items.stream().map(x -> x.getProduct().getPrice().multipliedBy(x.getQuantity())).collect(Collectors.toList()));
    }

    public String getPrettyTotal() {
        return CurrencyConverter.prettify(getTotal());
    }

    public void add(Product product, int quantity) {
        for (Item item : items) {
            if (item.getProduct().equals(product)) {
                item.quantity += quantity;
                return;
            }
        }

        items.add(new Item(product, quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cart items1 = (Cart) o;
        return Objects.equals(items, items1.items) &&
                Objects.equals(currency, items1.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items, currency);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", currency=" + currency +
                "} " + super.toString();
    }

    @Embeddable
    public static class Item {
        @OneToOne
        private Product product;
        private int quantity;

        public Item() {
        }

        public Item(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        @JsonIgnore
        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
