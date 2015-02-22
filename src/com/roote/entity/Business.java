package com.roote.entity;

import java.util.ArrayList;

public class Business {

	private String name;
	private String phone;
	private String type;
	private String link;
	private Address address;
	private ArrayList<Deal> deals;
	private double overallRating;
	private String ratingImage;
	private int numberOfReviews;
	private String photo;
	private double longitude;
	private double latitude;
	private double distance;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public ArrayList<Deal> getDeals() {
		return deals;
	}
	
	public void setDeals(ArrayList<Deal> deals) {
		this.deals = deals;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public double getOverallRating() {
		return overallRating;
	}
	
	public void setOverallRating(double overallRating) {
		this.overallRating = overallRating;
	}
	
	public int getNumberOfReviews() {
		return numberOfReviews;
	}
	
	public void setNumberOfReviews(int numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}
	
	public String getPhoto() {
		return photo;
	}
	
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String getRatingImage() {
		return ratingImage;
	}

	public void setRatingImage(String ratingImage) {
		this.ratingImage = ratingImage;
	}
	
	public String toString(){
		return "name: " + this.name + "\n" +
			   "phone: " + this.phone + "\n" +
			   "type: " + this.type +  "\n" +
			   "link: " + this.link +  "\n" +
			   "address: " + this.address +  "\n" +
			   "deals: " + this.deals + "\n" +
			   "overallRating: " + this.overallRating + "\n" +
			   "numberOfReviews:" + this.numberOfReviews + "\n" +
			   "photo: " + this.photo + "\n" +
			   "longitude: " + this.longitude + "\n" +
			   "latitude: " + this.latitude + "\n" +
			   "distance: " + this.distance + "\n------------\n";
		
	}

	


}
