package com.m5.android.avicola.model;

public class Advert implements ListItemInterface {
    public String inline;
    public String fullscreen;
    public String link;
    public String style;

    @Override
    public String getImageUrl() {
        return inline;
    }

    @Override
    public String getHeadline() {
        return null;
    }

    @Override
    public String getTeaser() {
        return null;
    }

    @Override
    public Content.Type getType() {
        return null;
    }
}
