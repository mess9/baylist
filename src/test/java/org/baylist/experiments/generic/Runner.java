package org.baylist.experiments.generic;

import java.util.Collection;

public class Runner {

	public static void main(String[] args) {

		MyObjectStore<Long, Car> carStore = new MyStore<>();
		carStore.put(20334L, new Car("BMW", "X5", 2013));

		Collection<Car> cars = carStore.getAll(exp->exp.year > 0);

		Collection<Car> car2 = carStore.getAll(e->false);
		System.out.println(carStore.get(20334L));

		Luboy l;
		lubaya(()->System.out.println(123));
	}

	static void lubaya(Luboy l) {

	}

}
