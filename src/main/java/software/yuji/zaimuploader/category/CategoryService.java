package software.yuji.zaimuploader.category;

import oauth.signpost.exception.OAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.yuji.zaimuploader.api.Zaim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class CategoryService {

    @Autowired
    private final Zaim zaim;

    private final CategoryRepository repository;

    public CategoryService(Zaim zaim, CategoryRepository repository) {
        this.zaim = zaim;
        this.repository = repository;
    }

    public void init() throws OAuthException, IOException {
        List<Category> saveEntities = new ArrayList<>();
        List<Category> deleteEntities = new ArrayList<>();
        for (Zaim.ZaimCategory category : zaim.getCategory()) {
            Category entity = new Category(
                    category.getId(),
                    CategoryMode.valueOf(category.getMode().toUpperCase(Locale.ROOT)),
                    category.getName(),
                    category.getSort()
            );
            if (category.getActive() == 1) {
                saveEntities.add(entity);
            } else {
                deleteEntities.add(entity);
            }
        }

        repository.saveAll(saveEntities);
        repository.deleteAll(deleteEntities);
    }

    public List<Category> findByMode(CategoryMode mode) {
        List<Category> list = new ArrayList<>();
        repository.findByMode(mode).forEach(list::add);

        list.sort(Comparator.comparingInt(Category::getSort));

        return list;
    }
}
