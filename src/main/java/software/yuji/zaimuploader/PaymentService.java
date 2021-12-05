package software.yuji.zaimuploader;

import java.io.IOException;
import java.io.InputStream;

public interface PaymentService {
    Payment[] readCSV(InputStream stream) throws IOException;
}
