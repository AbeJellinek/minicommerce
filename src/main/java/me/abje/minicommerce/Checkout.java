package me.abje.minicommerce;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Checkout {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String address1;
    private String address2;
    @NotNull
    private String postalCode;
    @NotNull
    private String city;
    @NotNull
    private String country;
    private String state;
    private PaymentMethod payment;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStripeToken(@NotNull String stripeToken) {
        this.payment = new StripePaymentMethod(stripeToken);
    }

    public PaymentMethod getPayment() {
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checkout checkout = (Checkout) o;
        return Objects.equals(firstName, checkout.firstName) &&
                Objects.equals(lastName, checkout.lastName) &&
                Objects.equals(email, checkout.email) &&
                Objects.equals(address1, checkout.address1) &&
                Objects.equals(address2, checkout.address2) &&
                Objects.equals(postalCode, checkout.postalCode) &&
                Objects.equals(city, checkout.city) &&
                Objects.equals(country, checkout.country) &&
                Objects.equals(state, checkout.state) &&
                Objects.equals(payment, checkout.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, address1, address2, postalCode, city, country, state, payment);
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", payment='" + payment + '\'' +
                '}';
    }
}
