package me.abje.minicommerce.money;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class RatesResponse {
    public Query query;

    public static class Query {
        public int count;
        public String created;
        public String lang;
        public Map<String, List<Rate>> results;
    }
}
