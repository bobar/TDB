package main;

public class Transaction {

	public int id;
	public int price;
	public String comment;
	public int admin;
	public int date;
	public int id2;

	public Transaction(int id, int price, String comment, int admin, int date, int id2) {
		this.id = id;
		this.price = price;
		this.comment = comment;
		this.admin = admin;
		this.date = date;
		this.id2 = id2;
	}

}
