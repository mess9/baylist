package org.baylist.experiments.generic;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

public interface MyObjectStore<K, V> {
	/**
	 * Кладёт значение в хранилище по заданному ключу.
	 *
	 * @param key   Ключ.
	 * @param value Значение.
	 */
	void put(K key, V value);

	/**
	 * Читает значение из хранилища по заданному ключу.
	 *
	 * @param key Ключ.
	 * @return Значение либо null.
	 */
	@Nullable
	V get(K key);

	/**
	 * Кладёт все пары ключ-значение в хранилище.
	 *
	 * @param entries Набор пар ключ-значение.
	 */
	void putAll(Map<? extends K, ? extends V> entries);

	/**
	 * Читает все значения из хранилища по заданным
	 * ключам.
	 *
	 * @param keys Набор ключей.
	 * @return Пары ключ-значение.
	 */
	Map<K, V> getAll(Collection<? super K> keys);

	/**
	 * Читает из хранилища все значения, удовлетворяющие
	 * заданному условию (предикату).
	 *
	 * @param p Предикат для проверки значений.
	 * @return Значения, удовлетворяющие предикату.
	 */
	Collection<V> getAll(Predicate<? super V> p);

}