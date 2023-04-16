package org.baylist.tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class DebugTests {

	/*
Написать метод, который будет возвращать значение медианы ряда целых чисел, поданного на вход.

Примеры:
func([7, 9, 3]) → 7
func([4, 1, 15, 5]) → 4.5
func([4, 7, 8]) → 7
func([1, 2]) → 1.5
*/
	@Test
	void debugTest() {
		System.out.println(getMedian(new int[]{7, 9, 3}));
		System.out.println(getMedian(new int[]{4, 1, 15, 5}));

	}

	float getMedian(int[] array) {
		Arrays.sort(array);
		if (array.length % 2 != 0) {
			return array[array.length / 2];
		} else {
			return (float) (array[(array.length / 2) - 1] + array[(array.length / 2)]) / 2;
		}
	}


}
