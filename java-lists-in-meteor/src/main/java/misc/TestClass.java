package misc;

public class TestClass {
	private final double myDouble;
	private final String myFirstName;
	private final String myLastName;
	private final String _id;
	public TestClass(String _id,double myDouble, String myFirstName, String myLastName) {
		super();
		this._id = _id;
		this.myDouble = myDouble;
		this.myFirstName = myFirstName;
		this.myLastName = myLastName;
	}
	public double getMyDouble() {
		return myDouble;
	}
	public String getMyFirstName() {
		return myFirstName;
	}
	public String getMyLastName() {
		return myLastName;
	}
	public String get_id(){
		return _id;
	}
	@Override
	public String toString() {
		return _id+", "+ myDouble + ", " + myFirstName + ", " + myLastName;
	}
	
}
