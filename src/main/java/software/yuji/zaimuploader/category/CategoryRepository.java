package software.yuji.zaimuploader.category;

import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Iterable<Category> findByMode(CategoryMode mode);
}
