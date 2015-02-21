package com.roote.entity;

public class Deal {
	
	private String title;
	private String link;
	private String currency;
	private String image;
	private Boolean popular;
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Boolean getPopular() {
		return popular;
	}
	public void setPopular(Boolean popular) {
		this.popular = popular;
	}
	
	public String toString(){
		return "title: " + this.title + "\n" +
			   "link: " + this.link + "\n" +
			   "currency: " + this.currency + "\n" +
			   "image: " + this.image + "\n" +
			   "popular: " + this.popular;
			   
	}
	
}
