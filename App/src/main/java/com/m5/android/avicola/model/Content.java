package com.m5.android.avicola.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.m5.android.avicola.R;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Content implements ListItemInterface, Parcelable {

    public enum Type {ALL, HELP, NEWS, EVENT}
    public enum Territory {
        ALL (new String[]{AppContext.context().getString(R.string.territory_all)}),
        NORTH_AMERICA(new String[] {"Norteamérica", "America - North"}),
        LATINO_AMERICA(new String[] {"Latinoamérica", "America - Latin"}),
        EUROPA(new String[] {"Europa", "Europe"}),
        AFRICA(new String[] {"África", "Africa"}),
        ASIA_CENTRAL(new String[] {"Asia - Central, Occidental", "Asia - Central, West"}),
        ASIA_ORIENTAL(new String[] {"Asia - Oriental (inc China)"}),
        ASIA_EAST(new String[] {"Asia - East (inc China)"}),
        ASIA_SOUTH(new String[] {"Asia - South (inc India)"}),
        ASIA_SOUTH_EAST(new String[] {"Asia - South East"}),
        ASIA_INDIA(new String[] {"Asia - Sur (inc India)"}),
        ASIA_OTHER(new String[] {"Asia - Sureste"}),
        OCEANIA(new String[] {"Oceanía, Pacífico", "Oceania, Pacific"}),
        MIDDLE_EAST(new String[] {"Middle East"}),
        ORIENT(new String[] {"Medio Oriente"});

        private String[] value;

        private Territory(String[] value) {
            this.value = value;
        }

        public String[] value() {
            return this.value;
        }

        public String firstValue() {
            return this.value[0];
        }
    }

    /**
     * Db autoincrement for favorites
     */
    public long localUid;

    public String id;
    public String newsId;
    public Type type;
    public String headline;
    public String link;
    public String snippet;
    public String body;
    public String country;
    public Territory territory;
    public long date;
    public long start;
    public long end;
    public String thumbnail;
    public Contact contact;
    public String[] channels;
    public Category[] categories;
    public String location;
    public String eventId;

    public Content(){}

    @JsonCreator
    public Content(@JsonProperty("type") String typeValue, @JsonProperty("territory") String territoryValue) throws Exception {
        boolean found = false;
        for (Type type : Type.values()) {
            if (type.name().equals(typeValue.toUpperCase())) {
                this.type = type;
                found = true;
            }
        }

        if (!found) {
            throw new Exception("Not found enum for Type: " + typeValue);
        }

        if (territoryValue != null) {
            this.territory = findTerritory(territoryValue);
            if (territory == null) {
                throw new Exception("Not found enum for Territory: " + territoryValue);
            }
        }
    }

    public static Territory findTerritory(String territoryValue) {
        for (Territory territory : Territory.values()) {
            for (String enumValue : territory.value()) {
                if (enumValue.equals(territoryValue.trim())) {
                    return territory;
                }
            }
        }
        return null;
    }

    public void setThumbnail(String val) {
        if (val != null) {
            thumbnail = val.replace(" ", "%20");
        }
    }

    public void setDate(JsonNode date) {
        this.date = date.asLong();
    }

    public static class Category {

    }

    @Override
    public String getImageUrl() {
        return thumbnail;
    }

    @Override
    public String getHeadline() {
        return headline;
    }

    @Override
    public String getTeaser() {
        return getFormattedDate();
    }

    public String getFormattedDate() {
        if (date != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            return sdf.format(new Date(date * 1000));
        }
        return null;
    }

    public String getFormattedStart() {
        if (start != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            return sdf.format(new Date(start*1000));
        }
        return null;
    }

    public String getFormattedEnd() {
        if (end != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
            return sdf.format(new Date(end*1000));
        }
        return null;
    }

    //parcelling part
    public Content (Parcel in) {
        String[] stringArray = new String[12];
        in.readStringArray(stringArray);
        id = stringArray[0];
        newsId = stringArray[1];
        type = Type.valueOf(stringArray[2]);
        headline = stringArray[3];
        link = stringArray[4];
        snippet = stringArray[5];
        body = stringArray[6];
        country = stringArray[7];
        if (!TextUtils.isEmpty(stringArray[8])) {
            territory = Territory.valueOf(stringArray[8]);
        }
        thumbnail = stringArray[9];
        location = stringArray[10];
        eventId = stringArray[11];

        long[] longArray = new long[3];
        in.readLongArray(longArray);
        date = longArray[0];
        start = longArray[1];
        end = longArray[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                id, newsId, type.name(), headline, link, snippet, body, country, territory.name(), thumbnail, location, eventId});

        dest.writeLongArray(new long[]{
                date, start, end});
    }

    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRAS_CONTENT, this);
        bundle.putParcelable(Constants.EXTRAS_CONTENT_CONTACT, contact);
        return bundle;
    }

    public static Content fromBundle(Bundle bundle) {
        final Content content = (Content) bundle.getParcelable(Constants.EXTRAS_CONTENT);
        content.contact = (Contact) bundle.getParcelable(Constants.EXTRAS_CONTENT_CONTACT);
        return content;
    }
}
