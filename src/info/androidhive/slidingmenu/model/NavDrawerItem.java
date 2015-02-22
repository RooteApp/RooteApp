package info.androidhive.slidingmenu.model;

public class NavDrawerItem {
     
    private String title;
    private Boolean checkBox;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
     
    public NavDrawerItem(){}
 
    public NavDrawerItem(String title, int icon, Boolean checkBox){
        this.title = title;
        this.icon = icon;
        this.checkBox = checkBox;
    }
     
    public NavDrawerItem(String title, int icon, Boolean checkBox, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.checkBox = checkBox;
        this.count = count;
    }
     
    public String getTitle(){
        return this.title;
    }
     
    public int getIcon(){
        return this.icon;
    }
     
    public String getCount(){
        return this.count;
    }
    
    public Boolean getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(Boolean checkBox) {
		this.checkBox = checkBox;
	}

	public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
     
    public void setIcon(int icon){
        this.icon = icon;
    }
     
    public void setCount(String count){
        this.count = count;
    }
     
    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }
}