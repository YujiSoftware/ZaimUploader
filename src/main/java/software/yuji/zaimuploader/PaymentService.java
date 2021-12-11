package software.yuji.zaimuploader;

import oauth.signpost.exception.OAuthException;

import java.io.IOException;
import java.io.InputStream;

public interface PaymentService {
    Payment[] readCSV(InputStream stream) throws IOException;

    int send(Payment[] payments) throws OAuthException, IOException;
}
