package org.baylist.experiments.generic;

import lombok.ToString;

@ToString
//@AllArgsConstructor
public class Car extends Vehicle {

	{
		super.brand = "dfg";
		year = 123;
	}
//
//	public Car(String s, String s2, Integer i) {
//		super(s, s2, i);
//	}


	public Car(String brand, String model, Integer year) {
		super(brand, model, year);
	}
}
