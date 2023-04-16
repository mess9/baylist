package org.baylist.experiments.generic;

import java.util.Collection;
import java.util.Map;

public class MyStore<Long, Car> implements MyObjectStore<Long, Car> {

	Long id;
	Car car;


	@Override
	public void put(Object key, Object value) {
		id = (Long) key;
		car = (Car) value;
	}

	@Override
	public Object get(Object key) {
		return car;
	}

	@Override
	public void putAll(Map entries) {

	}

	@Override
	public Map getAll(Collection keys) {
		return null;
	}

	@Override
	public Collection getAll(Predicate p) {
		return null;
	}
}
