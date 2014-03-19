package com.m5.android.avicola.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Feed {
    public String code;
    public AdvertWrapper adverts;

    @JsonIgnore
    public String territories;
    public Content[] contents;
}
