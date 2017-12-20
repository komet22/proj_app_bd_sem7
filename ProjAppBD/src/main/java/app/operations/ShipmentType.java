package app.operations;

public enum ShipmentType {

	BY_POST("Przesyłka pocztą"), BY_DELIVERY("Przesyłka kurierem");

	private String name;

	ShipmentType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
