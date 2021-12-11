package software.yuji.zaimuploader.genre;

import oauth.signpost.exception.OAuthException;
import org.springframework.stereotype.Service;
import software.yuji.zaimuploader.account.Account;
import software.yuji.zaimuploader.category.Category;
import software.yuji.zaimuploader.zaim.Zaim;

import java.io.IOException;
import java.util.*;

@Service
public class GenreService {

    private final Zaim zaim;

    private final GenreRepository genreRepository;

    private final DefaultGenreRepository defaultGenreRepository;

    public GenreService(Zaim zaim, GenreRepository genreRepository, DefaultGenreRepository defaultGenreRepository) {
        this.zaim = zaim;
        this.genreRepository = genreRepository;
        this.defaultGenreRepository = defaultGenreRepository;
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

        genreRepository.saveAll(saveEntities);
        genreRepository.deleteAll(deleteEntities);
    }

    public Map<Genre, Category> getCategoryMapping() {
        Map<Genre, Category> mapping = new HashMap<>();
        for (Genre genre : genreRepository.findAll()) {
            mapping.put(genre, genre.getCategory());
        }
        return mapping;
    }

    public Optional<Genre> loadDefault(Account account, String message) {
        return defaultGenreRepository.findById(new DefaultGenrePk(account, message)).map(DefaultGenre::getGenre);
    }
}
