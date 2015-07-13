package me.abje.minicommerce.db;

import org.joda.money.Money;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Objects;

@Entity
public class UserOrder extends ModelBase {
    private String name;
    private String email;
    @OneToOne
    private Cart cart;
    @Embedded
    private Address address;
    private boolean fulfilled;
    private boolean shippable;
    private String paymentType;
    private String paymentId;
    private Money paymentAmount;

    protected UserOrder() {
    }

    public UserOrder(String name, String email, Cart cart, Address address, boolean fulfilled, boolean shippable,
                     String paymentType, String paymentId, Money paymentAmount) {
        this.name = name;
        this.email = email;
        this.cart = cart;
        this.address = address;
        this.fulfilled = fulfilled;
        this.shippable = shippable;
        this.paymentType = paymentType;
        this.paymentId = paymentId;
        this.paymentAmount = paymentAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    public boolean isShippable() {
        return shippable;
    }

    public void setShippable(boolean shippable) {
        this.shippable = shippable;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Money getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Money paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserOrder userOrder = (UserOrder) o;
        return Objects.equals(fulfilled, userOrder.fulfilled) &&
                Objects.equals(shippable, userOrder.shippable) &&
                Objects.equals(name, userOrder.name) &&
                Objects.equals(email, userOrder.email) &&
                Objects.equals(cart, userOrder.cart) &&
                Objects.equals(address, userOrder.address) &&
                Objects.equals(paymentType, userOrder.paymentType) &&
                Objects.equals(paymentId, userOrder.paymentId) &&
                Objects.equals(paymentAmount, userOrder.paymentAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, email, cart, address, fulfilled, shippable, paymentType, paymentId, paymentAmount);
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cart=" + cart +
                ", address=" + address +
                ", fulfilled=" + fulfilled +
                ", shippable=" + shippable +
                ", paymentType='" + paymentType + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", paymentAmount=" + paymentAmount +
                "} " + super.toString();
    }

    @Embeddable
    public static class Address {
        protected String line1;
        protected String line2;
        protected String city;
        protected String state;
        protected String country;
        protected String postalCode;

        protected Address() {
        }

        public Address(String line1, String line2, String city, String state, String country, String postalCode) {
            this.line1 = line1;
            this.line2 = line2;
            this.city = city;
            this.state = state;
            this.country = country;
            this.postalCode = postalCode;
        }

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address = (Address) o;
            return Objects.equals(line1, address.line1) &&
                    Objects.equals(line2, address.line2) &&
                    Objects.equals(city, address.city) &&
                    Objects.equals(state, address.state) &&
                    Objects.equals(country, address.country) &&
                    Objects.equals(postalCode, address.postalCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(line1, line2, city, state, country, postalCode);
        }

        @Override
        public String toString() {
            return "Address{" +
                    "line1='" + line1 + '\'' +
                    ", line2='" + line2 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", country='" + country + '\'' +
                    ", postalCode='" + postalCode + '\'' +
                    '}';
        }
    }
}
