package commons.utils.math;

import java.util.Arrays;

public class PrimeNumberUtils {
	public final int MAX;
	private boolean [] primeCache;
	
	public PrimeNumberUtils(){
		this(10000005);
	}
	
	public PrimeNumberUtils(int capacity){
		super();
		MAX = capacity;
		primeCache = new boolean[MAX];
		sieve();
	}	
	
	private void sieve(){
		Arrays.fill(primeCache, true);
		int i, j;
		primeCache[0] = false;
		primeCache[1] = false;
		for(i=2; i<Math.sqrt(MAX); i++){
			if(primeCache[i])
				for(j=i*i; j<MAX; j+=i)
					primeCache[j] = false;
		}
	}
	
	public boolean isPrime(int num){
		return primeCache[num];
	}
	
	public static void main(String[] args) {
		PrimeNumberUtils primeUtils = new PrimeNumberUtils(1000000);
		System.out.println(primeUtils.isPrime(165576));

	}

}
