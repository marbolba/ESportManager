package sample;

public class Product
{
    private String name;
    private double price;
    private int quantity;

    public Product()
    {
        this.name="Blank";
        this.price=0;
        this.quantity=0;
    }
    public Product(String n,double p,int q)
    {
        this.name=n;
        this.price=p;
        this.quantity=q;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
