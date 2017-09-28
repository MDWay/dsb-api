import de.romjaki.dsbclient.DSBClient;
import org.apache.http.HttpException;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

public class Testing {
    public static void main(String[] args) throws URISyntaxException, IOException, HttpException, JSONException, ParseException {
        DSBClient client = DSBClient.buildBlocking("166162", "Riedberg2014");

    }
}
