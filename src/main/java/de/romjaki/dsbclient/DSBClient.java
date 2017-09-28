package de.romjaki.dsbclient;

import com.google.common.collect.Iterables;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DSBClient {
    private static final String BASE_URL = "https://iphone.dsbcontrol.de/iPhoneService.svc/DSB/";
    private static final String AUTH_URL = BASE_URL + "AuthID/";
    private static final String DATA_URL = BASE_URL + "timetables/";
    private static final DateFormat dateformat = new SimpleDateFormat("d.M.Y H:m");
    private final String user;
    private final String pass;
    private String authid;
    private DefaultHttpClient client = new DefaultHttpClient();
    private Map<Long, List<TimetableEntry>> cache;

    private DSBClient(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public static DSBClient buildBlocking(String user, String pass) throws HttpException, IOException, URISyntaxException, JSONException, ParseException {
        DSBClient client = new DSBClient(user, pass);
        client.login();
        client.receiveData();
        return client;
    }

    public static DSBClient buildAsync(String user, String pass, Consumer<Exception> exceptionCallback) {
        DSBClient client = new DSBClient(user, pass);
        Thread t = new Thread(() -> {
            try {
                client.login();
                client.receiveData();
            } catch (Exception e) {
                exceptionCallback.accept(e);
            }
        });
        return client;
    }

    private void login() throws IOException, HttpException, URISyntaxException {
        HttpGet get = new HttpGet(AUTH_URL + user + "/" + pass);

        HttpResponse response = client.execute(get);

        authid = EntityUtils.toString(response.getEntity()).replace("\"", "");
    }

    private void receiveData() throws URISyntaxException, IOException, HttpException, JSONException, ParseException {
        HttpGet get = new HttpGet(DATA_URL + authid);

        HttpResponse response = client.execute(get);

        String str = EntityUtils.toString(response.getEntity());
        JSONArray data = new JSONArray(str);
        cache = new HashMap<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject o = data.getJSONObject(i);
            boolean isHtml = o.getBoolean("ishtml");
            Date timetabledate = dateformat.parse(o.getString("timetabledate"));
            String timetableurl = o.getString("timetableurl");
            long millis = UnUtil.sanitizeDate(UnUtil.calendarByDate(timetabledate, Locale.GERMANY)).getTimeInMillis();
            List<TimetableEntry> arrL = cache.computeIfAbsent(millis, ignored -> new ArrayList<>());
            arrL.addAll(parseEntry(isHtml, timetableurl));
        }
    }


    private List<TimetableEntry> parseEntry(boolean isHtml, String timetableurl) throws URISyntaxException, IOException, HttpException {
        if (!isHtml) return Collections.emptyList();
        HttpGet get = new HttpGet(timetableurl);
        HttpResponse response = client.execute(get);
        Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
        Elements rows = document.getElementsByTag("tr").not(":not(:has(th))");
        List<TimetableEntry> entries = new ArrayList<>(rows.size());
        for (Element row : rows) {
            entries.add(parseElement(row));
        }
        return entries;
    }

    private TimetableEntry parseElement(Element row) {
        TimetableEntry entry = new TimetableEntry();
        entry.setKlasse(row.child(1).text());
        entry.setStunde(row.child(3).text());
        entry.setVertreter(row.child(4).text());
        entry.setFach(row.child(5).text());
        entry.setRaum(row.child(8).text());
        entry.setArt(row.child(9).text());
        entry.setText(row.child(11).text());
        if (entry.getFach() == null) {
            entry.setFach(row.child(6).text());
        }
        if (entry.getRaum() == null) {
            entry.setRaum(row.child(7).text());
        }
        if (entry.getKlasse() == null) {
            entry.setKlasse(row.child(10).text());
        }
        return entry;
    }

    public List<TimetableEntry> byTeacher(String teacher) {
        return filter(timetableEntry -> timetableEntry.getVertreter().equalsIgnoreCase(teacher));
    }

    public List<TimetableEntry> byDate(Date date) {
        return cache.get(UnUtil.sanitizeDate(UnUtil.calendarByDate(date, Locale.GERMANY)).getTimeInMillis());
    }

    public List<TimetableEntry> byClass(String klasse) {
        return filter(timetableEntry -> timetableEntry.getKlasse().contains(klasse) || klasse.contains(timetableEntry.getKlasse()));
    }


    public List<TimetableEntry> filter(Predicate<TimetableEntry> predicate) {
        return StreamSupport.stream(Iterables.concat(cache.values()).spliterator(), false)
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
