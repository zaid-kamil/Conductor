package com.bluelinelabs.conductor.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class StringSparseArrayParceler implements Parcelable {

    private final SparseArray<String> mStringSparseArray;

    public StringSparseArrayParceler(SparseArray<String> stringSparseArray) {
        mStringSparseArray = stringSparseArray;
    }

    private StringSparseArrayParceler(Parcel in) {
        mStringSparseArray = new SparseArray<>();

        final int size = in.readInt();

        for (int i = 0; i < size; i++) {
            mStringSparseArray.put(in.readInt(), in.readString());
        }
    }

    public SparseArray<String> getStringSparseArray() {
        return mStringSparseArray;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        final int size = mStringSparseArray.size();

        out.writeInt(size);

        for (int i = 0; i < size; i++) {
            int key = mStringSparseArray.keyAt(i);

            out.writeInt(key);
            out.writeString(mStringSparseArray.get(key));
        }
    }

    public static final Parcelable.Creator<StringSparseArrayParceler> CREATOR = new Parcelable.Creator<StringSparseArrayParceler>() {
        public StringSparseArrayParceler createFromParcel(Parcel in) {
            return new StringSparseArrayParceler(in);
        }

        public StringSparseArrayParceler[] newArray(int size) {
            return new StringSparseArrayParceler[size];
        }
    };

}
