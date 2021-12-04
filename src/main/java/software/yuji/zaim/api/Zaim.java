package software.yuji.zaim.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class Zaim {

    private final OAuthConsumer consumer;
    private final CloseableHttpClient client;

    private final ObjectMapper objectMapper;

    private Zaim(ZaimProperties properties, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.consumer = properties.getConsumer();
        this.client = HttpClientBuilder.create().build();
    }

    public ZaimCategory[] getCategory() throws IOException, OAuthException {
        HttpResponse response = get("https://api.zaim.net/v2/home/category?mapping=1");

        return objectMapper.readValue(response.getEntity().getContent(), ZaimCategories.class).getCategories();
    }


    public ZaimGenre[] getGenre() throws IOException, OAuthException {
        HttpResponse response = get("https://api.zaim.net/v2/home/genre?mapping=1");

        return objectMapper.readValue(response.getEntity().getContent(), ZaimGenres.class).getGenres();
    }

    private HttpResponse get(String url) throws IOException, OAuthException {
        HttpGet request = new HttpGet(url);
        consumer.sign(request);

        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String responseBody = EntityUtils.toString(response.getEntity());
            throw new IOException("" + response.getStatusLine().toString() + ": " + responseBody);
        }

        return response;
    }

    public static class ZaimCategories {
        private ZaimCategory[] categories;
        private String requested;

        public ZaimCategory[] getCategories() {
            return categories;
        }

        public void setCategories(ZaimCategory[] categories) {
            this.categories = categories;
        }

        public String getRequested() {
            return requested;
        }

        public void setRequested(String requested) {
            this.requested = requested;
        }
    }

    public static class ZaimCategory {
        private int id;
        private String mode;
        private String name;
        private int sort;
        private int active;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime modified;

        @JsonProperty("parent_category_id")
        private int parentCategoryId;

        @JsonProperty("local_id")
        private int localId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public LocalDateTime getModified() {
            return modified;
        }

        public void setModified(LocalDateTime modified) {
            this.modified = modified;
        }

        public int getParentCategoryId() {
            return parentCategoryId;
        }

        public void setParentCategoryId(int parentCategoryId) {
            this.parentCategoryId = parentCategoryId;
        }

        public int getLocalId() {
            return localId;
        }

        public void setLocalId(int localId) {
            this.localId = localId;
        }
    }

    public static class ZaimGenres {
        private ZaimGenre[] genres;
        private String requested;

        public ZaimGenre[] getGenres() {
            return genres;
        }

        public void setGenres(ZaimGenre[] genres) {
            this.genres = genres;
        }

        public String getRequested() {
            return requested;
        }

        public void setRequested(String requested) {
            this.requested = requested;
        }
    }

    public static class ZaimGenre {
        private int id;
        private String mode;
        private String name;
        private int sort;
        private int active;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime modified;

        @JsonProperty("category_id")
        private int categoryId;

        @JsonProperty("parent_genre_id")
        private int parentGenreId;

        @JsonProperty("local_id")
        private int localId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public LocalDateTime getModified() {
            return modified;
        }

        public void setModified(LocalDateTime modified) {
            this.modified = modified;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getParentGenreId() {
            return parentGenreId;
        }

        public void setParentGenreId(int parentGenreId) {
            this.parentGenreId = parentGenreId;
        }

        public int getLocalId() {
            return localId;
        }

        public void setLocalId(int localId) {
            this.localId = localId;
        }
    }
}
