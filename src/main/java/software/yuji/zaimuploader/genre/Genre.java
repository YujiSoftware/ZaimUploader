package software.yuji.zaimuploader.genre;

import software.yuji.zaimuploader.category.Category;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Genre {
    @Id
    private int id;

    private String name;

    private int sort;

    @OneToOne
    private Category category;

    protected Genre() {
    }

    public Genre(int id, String name, int sort, Category category) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
