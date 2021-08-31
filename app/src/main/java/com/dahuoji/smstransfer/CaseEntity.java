package com.dahuoji.smstransfer;

import java.io.Serializable;

public class CaseEntity implements Serializable {
    private String id;
    private String filtersPhoneNumber;
    private String filtersKeyword1;
    private String filtersKeyword2;
    private Contact contact1;
    private Contact contact2;
    private boolean showButtons;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFiltersPhoneNumber() {
        return filtersPhoneNumber;
    }

    public void setFiltersPhoneNumber(String filtersPhoneNumber) {
        this.filtersPhoneNumber = filtersPhoneNumber;
    }

    public String getFiltersKeyword1() {
        return filtersKeyword1;
    }

    public void setFiltersKeyword1(String filtersKeyword1) {
        this.filtersKeyword1 = filtersKeyword1;
    }

    public String getFiltersKeyword2() {
        return filtersKeyword2;
    }

    public void setFiltersKeyword2(String filtersKeyword2) {
        this.filtersKeyword2 = filtersKeyword2;
    }

    public Contact getContact1() {
        return contact1;
    }

    public void setContact1(Contact contact1) {
        this.contact1 = contact1;
    }

    public Contact getContact2() {
        return contact2;
    }

    public void setContact2(Contact contact2) {
        this.contact2 = contact2;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }
}
