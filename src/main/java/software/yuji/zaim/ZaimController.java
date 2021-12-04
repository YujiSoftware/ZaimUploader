package software.yuji.zaim;

import oauth.signpost.exception.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.yuji.zaim.category.Category;
import software.yuji.zaim.category.CategoryMode;
import software.yuji.zaim.category.CategoryService;
import software.yuji.zaim.genre.GenreService;
import software.yuji.zaim.paypay.PayPayService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes(types = ZaimController.UploadForm.class)
public class ZaimController {

    private static final String SESSION_ATTRIBUTES_UPLOAD_FORM = "uploadForm";

    public static class UploadForm {
        private UUID uuid;

        private Payment[] payments;

        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public Payment[] getPayments() {
            return payments;
        }

        public void setPayments(Payment[] payments) {
            this.payments = payments;
        }
    }

    private static class CommitForm {
        private Map<String, Commit> commits;

        public Map<String, Commit> getCommits() {
            return commits;
        }

        public void setCommits(Map<String, Commit> commits) {
            this.commits = commits;
        }

        public static class Commit {
            private int genreId;
            private boolean accept = true;

            public int getGenreId() {
                return genreId;
            }

            public void setGenreId(int genreId) {
                this.genreId = genreId;
            }

            public boolean isAccept() {
                return accept;
            }

            public void setAccept(boolean accept) {
                this.accept = accept;
            }
        }
    }

    private final PayPayService payPay;

    private final CategoryService categoryService;

    private final GenreService genreService;

    public ZaimController(PayPayService payPay, CategoryService categoryService, GenreService genreService) {
        this.payPay = payPay;
        this.categoryService = categoryService;
        this.genreService = genreService;
    }

    @ModelAttribute(SESSION_ATTRIBUTES_UPLOAD_FORM)
    public UploadForm setupForm() {
        return new UploadForm();
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

            UploadForm uploadForm = (UploadForm) model.getAttribute(SESSION_ATTRIBUTES_UPLOAD_FORM);
            uploadForm.setUuid(UUID.randomUUID());
            uploadForm.setPayments(payments);

            List<Category> categories = categoryService.findByMode(CategoryMode.PAYMENT);
            model.addAttribute("categories", categories);
            model.addAttribute("commitForm", new CommitForm());

            return "confirm";
        }
    }

    @PostMapping("/commit")
    public String commit(@ModelAttribute CommitForm form, Model model) {
        for (Map.Entry<String, CommitForm.Commit> entry : form.getCommits().entrySet()) {
            System.out.println(String.format("%s, %b, %d", entry.getKey(), entry.getValue().isAccept(), entry.getValue().genreId));
        }

        return "index";
    }
}
