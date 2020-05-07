package com.cia.rfclibrary.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{

    private String bookName, bookId, isIssued, issuedToId, issuedToName, issuedTo;

    public Book(){
        // empty constructor
        // ...
    }

    protected Book(Parcel in) {
        bookName = in.readString();
        bookId = in.readString();
        isIssued = in.readString();
        issuedToId = in.readString();
        issuedToName = in.readString();
        issuedTo = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsIssued() {
        return isIssued;
    }

    public void setIsIssued(String isIssued) {
        this.isIssued = isIssued;
    }

    public String getIssuedToId() {
        return issuedToId;
    }

    public void setIssuedToId(String issuedToId) {
        this.issuedToId = issuedToId;
    }

    public String getIssuedToName() {
        return issuedToName;
    }

    public void setIssuedToName(String issuedToName) {
        this.issuedToName = issuedToName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookName);
        dest.writeString(bookId);
        dest.writeString(isIssued);
        dest.writeString(issuedToId);
        dest.writeString(issuedToName);
        dest.writeString(issuedTo);
    }
}
