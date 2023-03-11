package service;


//BEAN CLASSES MUST!!!! have this import and the "@XXML ROOT" tag
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Product {

	String id = "not found";
	
	String name = "";
	
	Double price = 0.0;
	
	
	// ** getters & setters ** 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	
	
	
	
	
}
