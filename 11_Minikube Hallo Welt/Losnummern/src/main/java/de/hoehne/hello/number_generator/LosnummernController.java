package de.hoehne.hello.number_generator;

import static java.lang.Math.atan;
import static java.lang.Math.cbrt;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class LosnummernController {
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private static final AtomicInteger count = new AtomicInteger(0);

	private static Random random = new Random();

	@GetMapping()
	@ResponseBody
	public RandomNumber getRandomNumber() {
		logger.info(String.valueOf(count.incrementAndGet()));
		
		//blast some CPU
		double r = 0.0;
		for (double i = 1_000_000; i < 1_500_000; i++) {
			double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(sqrt(i)))))))))));
			d = cbrt(d);
			r += d;
		}
		System.out.println(r);
		
		return new RandomNumber(random.nextInt(1000000));
	}

	public static class RandomNumber {

		private static final UUID myId = UUID.randomUUID();
		private int randomNumber;
		private static String hostName;
		static {
			try {
				hostName = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		public RandomNumber(int randomNumber) {
			this.randomNumber = randomNumber;
		}

		public int getRandomNumber() {
			return randomNumber;
		}

		public String getHostName() {
			return hostName;
		}

		public UUID getMyid() {
			return myId;
		}

	}
}
