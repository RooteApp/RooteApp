package com.roote.entity;

import java.util.ArrayList;

public class Importer {
	
	public static ArrayList<Importer> importers = null;
	
	private String hs10Key;
	private String hs6Key;
	private String codeDescription;
	private String companyName;
	private String country;
	private String province;
	private String cityVille;
	private String postalCode;
	
	public String getHs10Key() {
		return hs10Key;
	}
	public void setHs10Key(String hs10Key) {
		this.hs10Key = hs10Key;
	}
	public String getHs6Key() {
		return hs6Key;
	}
	public void setHs6Key(String hs6Key) {
		this.hs6Key = hs6Key;
	}
	public String getCodeDescription() {
		return codeDescription;
	}
	public void setCodeDescription(String codeDescription) {
		this.codeDescription = codeDescription;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCityVille() {
		return cityVille;
	}
	public void setCityVille(String cityVille) {
		this.cityVille = cityVille;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String toString(){
		return "hs10Key: " + this.hs10Key + "\n" +
			   "hs6Key: " + this.hs6Key + "\n" + 
			   "codeDescription: " + this.codeDescription + "\n" +
			   "companyName: " + this.companyName + "\n" +
			   "country: " + this.country + "\n" +
			   "province: " + this.province + "\n" +
			   "cityVille: " + this.cityVille + "\n" +
			   "postalCode: " + this.postalCode + "\n------\n";
	}
}
