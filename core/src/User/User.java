package User;

public class User {
	
	private Integer smallBarLevel ;
	private Integer tireLevel ;
	private Integer springLevel ;
	private Integer money ;
	
	private static User instance;
	
	private User(){
		
	}
	
	public static User getInstance(){
		if(instance == null){
			instance = new User();
		}
		return instance;
	}
	
	public Integer getSmallBarLevel() {
		if(smallBarLevel<=1){
			return 1;
		}
		return smallBarLevel;
	}
	public void setSmallBarLevel(Integer smallBarLevel) {
		this.smallBarLevel = smallBarLevel;
	}
	public void decrementSmallBarLevel(){
		smallBarLevel--;
		if(smallBarLevel<=1){
			smallBarLevel = 1;
		}
	}
	
	
	public Integer getTireLevel() {
		if(tireLevel<=1){
			return 1;
		}
		return tireLevel;
	}
	public void setTireLevel(Integer tireLevel) {
		this.tireLevel = tireLevel;
	}	
	public void decrementTireLevel(){
		tireLevel--;
		if(tireLevel<=1){
			tireLevel = 1;
		}
	}
	
	
	
	public Integer getSpringLevel() {
		if(springLevel<=1){
			return 1;
		}
		return springLevel;
	}
	public void setSpringLevel(Integer springLevel) {
		this.springLevel = springLevel;
	}
	public void decrementSpringLevel(){
		springLevel--;
		if(springLevel<=1){
			springLevel = 1;
		}
	}
	
	
	
	public Integer getMoney() {
		return money;
	}
	public void setMoney(Integer money) {
		this.money = money;
	}	

}
