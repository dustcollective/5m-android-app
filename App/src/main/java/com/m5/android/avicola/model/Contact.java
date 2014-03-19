package com.m5.android.avicola.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    public String name;
    public String email;

    public Contact(){}

    //parcelling part
    public Contact (Parcel in) {
        String[] stringArray = new String[2];
        in.readStringArray(stringArray);
        name = stringArray[0];
        email = stringArray[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                name, email});
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
