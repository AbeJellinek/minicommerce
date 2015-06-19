package me.abje.minicommerce;

import java.util.List;

public class SearchResponse extends SuccessResponse {
    private final List<Result> results;

    public SearchResponse(boolean success, List<Result> results) {
        super(success, "");
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private String title;
        private String url;
        private String price;
        private String description;

        public Result(String title, String url, String price, String description) {
            this.title = title;
            this.url = url;
            this.price = price;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
