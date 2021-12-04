package software.yuji.zaim;

import java.io.IOException;
import java.io.InputStream;

public interface PaymentService {
    Payment[] readCSV(InputStream stream) throws IOException;
}
