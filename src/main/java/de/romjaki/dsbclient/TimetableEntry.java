package de.romjaki.dsbclient;

public class TimetableEntry {
    private String klasse;
    private String stunde;
    private String vertreter;
    private String fach;
    private String raum;
    private String art;
    private String text;

    public String getKlasse() {
        return klasse;
    }

    public void setKlasse(String klasse) {
        klasse = klasse.trim();
        if (klasse.isEmpty()) {
            klasse = null;
        }
        this.klasse = klasse;
    }

    public String getStunde() {
        return stunde;
    }

    public void setStunde(String stunde) {
        stunde = stunde.trim();
        if (stunde.isEmpty()) {
            stunde = null;
        }
        this.stunde = stunde;
    }

    public String getVertreter() {
        return vertreter;
    }

    public void setVertreter(String vertreter) {
        vertreter = vertreter.trim();
        if (vertreter.isEmpty()) {
            vertreter = null;
        }
        this.vertreter = vertreter;
    }

    public String getFach() {
        return fach;
    }

    public void setFach(String fach) {
        fach = fach.trim();
        if (fach.isEmpty()) {
            fach = null;
        }
        this.fach = fach;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        raum = raum.trim();
        if (raum.isEmpty()) {
            raum = null;
        }
        this.raum = raum;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        art = art.trim();
        if (art.isEmpty()) {
            art = null;
        }
        this.art = art;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            text = null;
        }
        this.text = text;
    }
}
