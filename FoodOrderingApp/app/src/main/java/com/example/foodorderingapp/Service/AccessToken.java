package com.example.foodorderingapp.Service;

import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"foodorderingapp-b8079\",\n" +
                    "  \"private_key_id\": \"04ea9b98335c5cb43e55755f124755ba7da00b85\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCuWUqi3ZzwKVOX\\nAC/xSdbBd0Fuo7n2IWCN68i9pUkzdPks/9ZBzDnwFpZsUrAYeMyC1q4F9r9uhxI8\\nm7rrvvq32VXGKugXJvKLNpWusnjNi4rH1CrXo2VHzMY21yMOi0D0zy5OMg3Ciykd\\nf6cFcNrVulZ/w/2EoK1Qys6FgzChNPRtOQSgRLQmnT5aHzSGKCc1WBIK5eIwineQ\\ndPPXFJqRxqplnF326JDHzf8gwNR9G6hzgYVr60ofdox438atHjhDK1fqhnHq2EgU\\nStGU2N5UU6OkbfRtALHMzn3r6UWfT93mEFkOkL9sXqunh/6XBqY4pth7CQY/vLW8\\nOqEgRyM3AgMBAAECggEANwmwAn7kCnp5TUs9VzXAih1L9hHQZA8ATHz4x0lUtN5B\\nxL9UkzvVtGFlBfgKaAdGRC1iDTbqEomoK6LbnI0S15PPvHmR/7EHUwQdt2LWuMhK\\nvK9RAPavQ720et5qTi6OiFmY3A14A4hrp8jC4HhSVLL4mGe18bbJT55eGI6j/NdV\\nvF+18i1NQMyuO7BN939Z2yIXzMZK/XVdHluVJ4CysiesKqNsDw5Dg+8RlRFPmd9A\\nfHfpML3crdvwtymM/hAqjdoTFANqblyFe9AI7yR2zwSHRfWNLOOdSMGYm5VWSXzl\\n9w5TVMd7zgeymH2IuLPK0ynt5tBE2spExWecRUV8CQKBgQDZIloowsL/JyyKTEdF\\nlcet0+szk7aaNOUSZ+qwx9IUP40W937q8cJ4zgeUd6ChXGuzAkExZFa1FhUFx35W\\n+4/xvFLSV0G5qkMNvOsAXuvxAMuhmPKidd+Ra15vDdfl7CDg0fN0Oo6Y4rE/LjGN\\nMO91bNIoi84gmsnfL9Q3cXDl7wKBgQDNjmcGW+qofhuWlK00NHxZM72ya/9XR8Xb\\ngckF2baNr4B+XzQHiiQNElx4VQ7X0tzcEqpfJVIajLR7DclZ+DWgZJMHtjgMpu3X\\nF2AnisHCyN0hqR9t04HW95XrB7HcPyTS67uav/ClfSrSDmMqwj1h+Jv4WCKK+qfe\\nijeuHnQfOQKBgE8ciwjq3S0l0r6YyCb8SYJ2Ae4K1a8dspCgOAws4cfq17cUebOU\\nXiyxtGVNqMLh74OLQMGEM2tLHmG6q7d5dJq46NwmWpYRCNlKibza2NA4X/kCxvSW\\nSdyr5/5CZvUosVn4ZOFmLGpbvgStCfTNbZG1EmUfevy6KO8eeA2zUVUVAoGBAJXy\\nPic+X9QfggJ7XEnLy7XKb6O68l355lQ0va593ZRHpQUKb8rcM2YxsbC544T+tcv3\\n9b5IXDcv0uw851exH3VsSHEEtKhOEQupaqQNrUBpBB5s35dfBcUJgJwdVdpjWXpf\\nKdA+Gc6umtD2W9KyNNLF5wjnSfN124UT8IAciPrJAoGBAJmich3TMxPEYP49Nvsm\\nVm9GYlVYwZypEyAAfTbnxAV6vYdLIi4vTaHXPLZ6t3t66jGutVN2hhfbXICKLCL8\\n59uASoURVC4cLqx1+aSyIp/dwC/kfmbmfvLHMTDSgDjWSp9fQUr7QNBqxEQVlsGs\\n0LaMOM0WAF9RP8C+Crf1/OIx\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-nddfp@foodorderingapp-b8079.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"113553747586133324292\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-nddfp%40foodorderingapp-b8079.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream).createScoped(firebaseMessagingScope);
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
