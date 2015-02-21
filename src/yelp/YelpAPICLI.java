package yelp;

public class YelpAPICLI {

	private String term;
	private String ll;
	private String radius_filter;
	  
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getLl() {
		return ll;
	}

	public void setLl(String ll) {
		this.ll = ll;
	}

	public String getRadiusFilter() {
		return radius_filter;
	}

	public void setRadiusFilter(String radius_filter) {
		this.radius_filter = radius_filter;
	}

	public YelpAPICLI(final String term, final String ll, final String radius_filter){
		setTerm(term);
		setLl(ll);
		setRadiusFilter(radius_filter);
	}
}