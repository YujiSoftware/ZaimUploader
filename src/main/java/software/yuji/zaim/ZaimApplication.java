package software.yuji.zaim;

import oauth.signpost.exception.OAuthException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import software.yuji.zaim.category.Category;
import software.yuji.zaim.category.CategoryMode;
import software.yuji.zaim.category.CategoryService;
import software.yuji.zaim.genre.GenreService;
import software.yuji.zaim.paypay.PayPayService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
@Controller
public class ZaimApplication {

    private final PayPayService payPay;

    private final CategoryService categoryService;

    private final GenreService genreService;

    public ZaimApplication(PayPayService payPay, CategoryService categoryService, GenreService genreService) {
        this.payPay = payPay;
        this.categoryService = categoryService;
        this.genreService = genreService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ZaimApplication.class, args);
    }

    @GetMapping("/init")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public String init() throws OAuthException, IOException {
        categoryService.init();
        genreService.init();

        return null;
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("csv") MultipartFile multipartFile,
            @RequestParam(value = "type") String type, Model model) throws IOException {
        PaymentService service = switch (type) {
            case "paypay" -> payPay;
            default -> null;
        };

        try (InputStream stream = multipartFile.getInputStream()) {
            Payment[] payments = service.readCSV(stream);

            List<Category> categories = categoryService.findByMode(CategoryMode.PAYMENT);

            model.addAttribute("payments", payments);
            model.addAttribute("categories", categories);
            return "confirm";
        }
    }
}
