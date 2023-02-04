package software.yuji.zaimuploader;

import oauth.signpost.exception.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.yuji.zaimuploader.account.AccountService;
import software.yuji.zaimuploader.category.Category;
import software.yuji.zaimuploader.category.CategoryMode;
import software.yuji.zaimuploader.category.CategoryService;
import software.yuji.zaimuploader.genre.GenreService;
import software.yuji.zaimuploader.goldpoint.GoldPointService;
import software.yuji.zaimuploader.paypay.PayPayService;
import software.yuji.zaimuploader.pocketcard.PocketCardService;
import software.yuji.zaimuploader.suica.SuicaService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Controller
@SessionAttributes(types = ZaimController.UploadForm.class)
public class ZaimController {

    private static final String SESSION_ATTRIBUTES_UPLOAD_FORM = "uploadForm";

    public static class UploadForm {
        private String type;

        private UUID uuid;

        private Payment[] payments;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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

    private final PayPayService payPay;

    private final GoldPointService goldPoint;

    private final SuicaService suica;

    private final PocketCardService pocketCard;

    private final AccountService accountService;

    private final CategoryService categoryService;

    private final GenreService genreService;

    public ZaimController(PayPayService payPay, GoldPointService goldPoint, SuicaService suica, PocketCardService pocketCard, AccountService accountService, CategoryService categoryService, GenreService genreService) {
        this.payPay = payPay;
        this.goldPoint = goldPoint;
        this.suica = suica;
        this.pocketCard = pocketCard;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.genreService = genreService;
    }

    @ModelAttribute(SESSION_ATTRIBUTES_UPLOAD_FORM)
    public UploadForm setupUploadForm() {
        return new UploadForm();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/init")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public String init() throws OAuthException, IOException {
        accountService.init();
        categoryService.init();
        genreService.init();

        return null;
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("csv") MultipartFile multipartFile,
            @RequestParam(value = "type") String type, Model model) throws IOException {
        PaymentService service = getPaymentService(type);

        try (InputStream stream = multipartFile.getInputStream()) {
            Payment[] payments = service.readCSV(stream);

            UploadForm uploadForm = (UploadForm) model.getAttribute(SESSION_ATTRIBUTES_UPLOAD_FORM);
            uploadForm.setType(type);
            uploadForm.setUuid(UUID.randomUUID());
            uploadForm.setPayments(payments);

            List<Category> categories = categoryService.findByMode(CategoryMode.PAYMENT);
            model.addAttribute("categories", categories);

            return "confirm";
        }
    }

    @PostMapping("/commit")
    public String commit(@ModelAttribute UploadForm form, RedirectAttributes redirectAttributes) throws OAuthException, IOException {
        for (Payment payment : form.getPayments()) {
            System.out.printf("%s, %d%n", payment.getPlace(), payment.getGenreId());
        }

        PaymentService service = getPaymentService(form.getType());

        int send = service.send(form.getPayments());
        redirectAttributes.addFlashAttribute(
                "message",
                String.format("%d 件のレコードを Zaim に登録しました。", send)
        );

        return "redirect:/";
    }

    private PaymentService getPaymentService(String type) {
        return switch (type) {
            case "paypay" -> payPay;
            case "goldpoint" -> goldPoint;
            case "suica" -> suica;
            case "pocketcard" -> pocketCard;
            default -> null;
        };
    }
}
