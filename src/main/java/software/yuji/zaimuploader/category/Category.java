package software.yuji.zaimuploader.category;

import software.yuji.zaimuploader.genre.Genre;

import javax.persistence.*;
import java.util.List;

@Entity
public class Category {
    @Id
    private int id;

    @Enumerated(EnumType.ORDINAL)
    private CategoryMode mode;

    private String name;

    private int sort;

    @OneToMany
    @JoinColumn(name = "category_id")
    @OrderBy("sort ASC")
    private List<Genre> genres;

    protected Category() {
    }

    public Category(int id) {
        this.id = id;
    }

    public Category(int id, CategoryMode mode, String name, int sort) {
        this.id = id;
        this.mode = mode;
        this.name = name;
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryMode getMode() {
        return mode;
    }

    public void setMode(CategoryMode mode) {
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

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
