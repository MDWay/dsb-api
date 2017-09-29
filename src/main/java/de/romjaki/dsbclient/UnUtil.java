package de.romjaki.dsbclient;


import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static java.util.Calendar.*;

public class UnUtil {

    private UnUtil() {
        UnUtil.singleton(UnUtil.class);
    }

    public static Calendar calendarByDate(Date date, Locale locale) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar sanitizeDate(Calendar temp) {
        Calendar calendar = Calendar.getInstance(temp.getTimeZone());

        calendar.set(
                temp.get(YEAR),
                temp.get(MONTH),
                temp.get(DAY_OF_MONTH),
                0, 0, 0);
        return calendar;
    }

    public static void singleton(Class<UnUtil> utilClass) throws IllegalStateException {
        throw new IllegalStateException("No " + utilClass.getCanonicalName() + " class instances for you :P");
    }

    public static String httpGet(String urlString, Map<String, List<String>> headers) throws IOException {
        return performGeneralHttpRequest("GET", urlString, headers);
    }

    public static String performGeneralHttpRequest(String method, String urlString, Map<String, List<String>> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.getRequestProperties().putAll(headers);

        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        InputStream stream = conn.getInputStream();
        return readFully(stream);
    }

    public static UrlEncodedFormEntity createFormEntity(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        params.forEach((name, val) -> nameValuePairs.add(new BasicNameValuePair(name, val)));
        try {
            return new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String readFully(InputStream stream) {
        return new Scanner(stream).useDelimiter("\\A").next();
    }
}
