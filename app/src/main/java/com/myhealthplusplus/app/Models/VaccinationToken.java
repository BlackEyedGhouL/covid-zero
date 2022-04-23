package com.myhealthplusplus.app.Models;

public class VaccinationToken {
    String issuedDateT, validDateT, firstNameT, lastNameT, postalCodeT, nicT, phoneNumberT, genderT, dateOfBirthT, indigenousT, tokenImageUrl;
    boolean isFullyVaccinated, dose1, dose2, dose3;

    public VaccinationToken(String issuedDateT, String validDateT, String firstNameT, String lastNameT, String postalCodeT, String nicT, String phoneNumberT, String genderT, String dateOfBirthT, String indigenousT, String tokenImageUrl, boolean isFullyVaccinated, boolean dose1, boolean dose2, boolean dose3) {
        this.issuedDateT = issuedDateT;
        this.validDateT = validDateT;
        this.firstNameT = firstNameT;
        this.lastNameT = lastNameT;
        this.postalCodeT = postalCodeT;
        this.nicT = nicT;
        this.phoneNumberT = phoneNumberT;
        this.genderT = genderT;
        this.dateOfBirthT = dateOfBirthT;
        this.indigenousT = indigenousT;
        this.tokenImageUrl = tokenImageUrl;
        this.isFullyVaccinated = isFullyVaccinated;
        this.dose1 = dose1;
        this.dose2 = dose2;
        this.dose3 = dose3;
    }

    public String getIssuedDateT() {
        return issuedDateT;
    }

    public String getValidDateT() {
        return validDateT;
    }

    public String getFirstNameT() {
        return firstNameT;
    }

    public String getLastNameT() {
        return lastNameT;
    }

    public String getPostalCodeT() {
        return postalCodeT;
    }

    public String getNicT() {
        return nicT;
    }

    public String getPhoneNumberT() {
        return phoneNumberT;
    }

    public String getGenderT() {
        return genderT;
    }

    public String getDateOfBirthT() {
        return dateOfBirthT;
    }

    public String getIndigenousT() {
        return indigenousT;
    }

    public String getTokenImageUrl() {
        return tokenImageUrl;
    }

    public boolean isFullyVaccinated() {
        return isFullyVaccinated;
    }

    public boolean isDose1() {
        return dose1;
    }

    public boolean isDose2() {
        return dose2;
    }

    public boolean isDose3() {
        return dose3;
    }
}
