package de.hoehne.hello.number_generator;

import org.junit.Test;
import static java.lang.Math.*;

public class CPUPerfomanceTest {

	@Test
	public void testCPU() throws Exception {
		final long start = System.currentTimeMillis();
		double r = 0.0;
		for (double i = 1_000_000; i < 2_000_000; i++) {
			double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(sqrt(i)))))))))));
			d = cbrt(d);
			r += d;
		}
		System.out.println(r);

		System.out.println(System.currentTimeMillis() - start);
	}
}
