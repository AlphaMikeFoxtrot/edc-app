package com.cia.rfclibrary.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Subscriber implements Parcelable{

    private String name, 
                   id, 
                   enrolledFor, 
                   reb, 
                   leb, 
                   center, 
                   enrollmentType, 
                   enrolledOn, 
                   dob, 
                   gender,
                   phone, 
                   jointAccount, 
                   toyIssued, 
                   bookIssued, 
                   isToy, 
                   isGen, 
                   bookCount, 
                   toyCount,
                   is_ecre,
                   ecre_level,
                   is_external_ecd,
                   external_ecd_name,
                   subscriber_class,
                   board,
                   m_name,
                   m_qual,
                   m_occ,
                   m_phone,
                   m_email,
                   m_lang,
                   f_name,
                   f_qual,
                   f_occ,
                   f_phone,
                   f_email,
                   f_lang;

    public Subscriber() {
    }

    protected Subscriber(Parcel in) {
        name = in.readString();
        id = in.readString();
        enrolledFor = in.readString();
        reb = in.readString();
        leb = in.readString();
        center = in.readString();
        enrollmentType = in.readString();
        enrolledOn = in.readString();
        dob = in.readString();
        gender = in.readString();
        phone = in.readString();
        jointAccount = in.readString();
        toyIssued = in.readString();
        bookIssued = in.readString();
        isToy = in.readString();
        isGen = in.readString();
        bookCount = in.readString();
        toyCount = in.readString();
        is_ecre = in.readString();
        ecre_level = in.readString();
        is_external_ecd = in.readString();
        external_ecd_name = in.readString();
        subscriber_class = in.readString();
        board = in.readString();
        m_name = in.readString();
        m_qual = in.readString();
        m_occ = in.readString();
        m_phone = in.readString();
        m_email = in.readString();
        m_lang = in.readString();
        f_name = in.readString();
        f_qual = in.readString();
        f_occ = in.readString();
        f_phone = in.readString();
        f_email = in.readString();
        f_lang = in.readString();
    }

    public static final Creator<Subscriber> CREATOR = new Creator<Subscriber>() {
        @Override
        public Subscriber createFromParcel(Parcel in) {
            return new Subscriber(in);
        }

        @Override
        public Subscriber[] newArray(int size) {
            return new Subscriber[size];
        }
    };

    public String getBookCount() {
        return bookCount;
    }

    public void setBookCount(String bookCount) {
        this.bookCount = bookCount;
    }

    public String getToyCount() {
        return toyCount;
    }

    public void setToyCount(String toyCount) {
        this.toyCount = toyCount;
    }

    public String getReb() {
        return reb;
    }

    public void setReb(String reb) {
        this.reb = reb;
    }

    public String getLeb() {
        return leb;
    }

    public void setLeb(String leb) {
        this.leb = leb;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getEnrollmentType() {
        return enrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        this.enrollmentType = enrollmentType;
    }

    public String getEnrolledOn() {
        return enrolledOn;
    }

    public void setEnrolledOn(String enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJointAccount() {
        return jointAccount;
    }

    public void setJointAccount(String jointAccount) {
        this.jointAccount = jointAccount;
    }

    public String getToyIssued() {
        return toyIssued;
    }

    public void setToyIssued(String toyIssued) {
        this.toyIssued = toyIssued;
    }

    public String getBookIssued() {
        return bookIssued;
    }

    public void setBookIssued(String bookIssued) {
        this.bookIssued = bookIssued;
    }

    public String getIsToy() {
        return isToy;
    }

    public void setIsToy(String isToy) {
        this.isToy = isToy;
    }

    public String getIsGen() {
        return isGen;
    }

    public void setIsGen(String isGen) {
        this.isGen = isGen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnrolledFor() {
        return enrolledFor;
    }

    public void setEnrolledFor(String enrolledFor) {
        this.enrolledFor = enrolledFor;
    }

    public String getis_ecre() {
        return is_ecre;
    }

    public void setis_ecre(String is_ecre) {
        this.is_ecre = is_ecre;
    }

    public String getecre_level() {
        return ecre_level;
    }

    public void setecre_level(String ecre_level) {
        this.ecre_level = ecre_level;
    }

    public String getis_external_ecd() {
        return is_external_ecd;
    }

    public void setis_external_ecd(String is_external_ecd) {
        this.is_external_ecd = is_external_ecd;
    }

    public String getexternal_ecd_name() {
        return external_ecd_name;
    }

    public void setexternal_ecd_name(String external_ecd_name) {
        this.external_ecd_name = external_ecd_name;
    }

    public String getSubscriber_class() {
        return subscriber_class;
    }

    public void setSubscriber_class(String subscriber_class) {
        this.subscriber_class = subscriber_class;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getm_name() {
        return m_name;
    }

    public void setm_name(String m_name) {
        this.m_name = m_name;
    }

    public String getm_qual() {
        return m_qual;
    }

    public void setm_qual(String m_qual) {
        this.m_qual = m_qual;
    }

    public String getm_occ() {
        return m_occ;
    }

    public void setm_occ(String m_occ) {
        this.m_occ = m_occ;
    }

    public String getm_phone() {
        return m_phone;
    }

    public void setm_phone(String m_phone) {
        this.m_phone = m_phone;
    }

    public String getm_email() {
        return m_email;
    }

    public void setm_email(String m_email) {
        this.m_email = m_email;
    }

    public String getm_lang() {
        return m_lang;
    }

    public void setm_lang(String m_lang) {
        this.m_lang = m_lang;
    }

    public String getf_name() {
        return f_name;
    }

    public void setf_name(String f_name) {
        this.f_name = f_name;
    }

    public String getf_qual() {
        return f_qual;
    }

    public void setf_qual(String f_qual) {
        this.f_qual = f_qual;
    }

    public String getf_occ() {
        return f_occ;
    }

    public void setf_occ(String f_occ) {
        this.f_occ = f_occ;
    }

    public String getf_phone() {
        return f_phone;
    }

    public void setf_phone(String f_phone) {
        this.f_phone = f_phone;
    }

    public String getf_email() {
        return f_email;
    }

    public void setf_email(String f_email) {
        this.f_email = f_email;
    }

    public String getf_lang() {
        return f_lang;
    }

    public void setf_lang(String f_lang) {
        this.f_lang = f_lang;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(enrolledFor);
        dest.writeString(reb);
        dest.writeString(leb);
        dest.writeString(center);
        dest.writeString(enrollmentType);
        dest.writeString(enrolledOn);
        dest.writeString(dob);
        dest.writeString(gender);
        dest.writeString(phone);
        dest.writeString(jointAccount);
        dest.writeString(toyIssued);
        dest.writeString(bookIssued);
        dest.writeString(isToy);
        dest.writeString(isGen);
        dest.writeString(bookCount);
        dest.writeString(toyCount);
        dest.writeString(is_ecre);
        dest.writeString(ecre_level);
        dest.writeString(is_external_ecd);
        dest.writeString(external_ecd_name);
        dest.writeString(subscriber_class);
        dest.writeString(board);
        dest.writeString(m_name);
        dest.writeString(m_qual);
        dest.writeString(m_occ);
        dest.writeString(m_phone);
        dest.writeString(m_email);
        dest.writeString(m_lang);
        dest.writeString(f_name);
        dest.writeString(f_qual);
        dest.writeString(f_occ);
        dest.writeString(f_phone);
        dest.writeString(f_email);
        dest.writeString(f_lang);
    }
}
