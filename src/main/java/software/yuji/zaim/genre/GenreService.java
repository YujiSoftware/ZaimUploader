package software.yuji.zaim.genre;

import oauth.signpost.exception.OAuthException;
import org.springframework.stereotype.Service;
import software.yuji.zaim.api.Zaim;
import software.yuji.zaim.category.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GenreService {

    private final Zaim zaim;

    private final GenreRepository repository;

    public GenreService(Zaim zaim, GenreRepository repository) {
        this.zaim = zaim;
        this.repository = repository;
    }

    public void init() throws OAuthException, IOException {
        List<Genre> saveEntities = new ArrayList<>();
        List<Genre> deleteEntities = new ArrayList<>();
        for (Zaim.ZaimGenre genre : zaim.getGenre()) {
            Genre entity = new Genre(
                    genre.getId(),
                    genre.getName(),
                    genre.getSort(),
                    new Category(genre.getCategoryId())
            );
            if (genre.getActive() == 1) {
                saveEntities.add(entity);
            } else {
                deleteEntities.add(entity);
            }
        }

        repository.saveAll(saveEntities);
        repository.deleteAll(deleteEntities);
    }

    public List<Genre> loadAll() {
        List<Genre> list = new ArrayList<>();
        repository.findAll().forEach(list::add);

        list.sort(Comparator.comparingInt(Genre::getSort));

        return list;
    }
}
