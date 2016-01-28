package commons.utils.math;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	public int gcd(int a, int b){
		a = Math.max(a, b);
		b = Math.min(a, b);
		if(b<0){
			throw new IllegalArgumentException("Not valid!");
		}
		return gcd1(a, b);		
	}


	private int gcd1(int a1, int b1) {
		if(b1==0)
			return a1;
		return gcd(b1, a1%b1);
	}

	public static class Result{
		int x;
		int y;
		int gcd;
	}

	/*
	 * Finds inverse of (a mod b i.e. x) and (b mod a i.e. y)
	 * a and b has to be coprime numbers for the inverse to exists
	 *
	 * */
	public void extendedGCD(int a, int b, Result result){
		if(b==0){
			result.gcd = a;
			result.x = 1;
			result.y = 0;
		} else{
			extendedGCD(b, a%b, result);
			int temp = result.x;
			result.x = result.y;
			result.y = temp - (a/b) * result.y; //rn-2= rn-1 - (an/bn) * rn-1
		}
	}

	public int chineseRemainder(int [] a, int [] n, int len){
		int prod = 1;
		int i;
		for(i=0; i<len; i++){
			prod *= n[i];
		}

		int ans = 0;
		int p;
		for(i=0; i<len; i++){
			p = prod/n[i];
			Result res = new Result();
			extendedGCD(p, n[i], res);
			ans += (a[i]*res.x*p);
		}

		return ans;
	}

	public int eulerTotient(int n){
		Map<Integer, Integer> factorPower = uniquePrimeFactors(n);
		int ans = n;

		Set<Integer> keySet = factorPower.keySet();
		for(Integer key : keySet){
			ans = (ans*(key-1))/key;
		}

		return ans;
	}

	private Map<Integer, Integer> uniquePrimeFactors(int n) {
		Map<Integer, Integer> factorPower = new HashMap<Integer, Integer>();
		Integer num;
		while(n%2==0){
			num = factorPower.get(2);
			factorPower.put(2, (num!=null?num:0)+1);
			n /= 2;
		}

		for(int i=3; i<=Math.sqrt(n); i+=2){
			while(n%i==0){
				num = factorPower.get(i);
				factorPower.put(i, (num!=null?num:0)+1);
				n /= i;
			}
		}


		if(n>0 && n!=1){
			factorPower.put(n, 1);
		}

		return factorPower;
	}

	public int powModuloN(int a, int pow, int n){
		int ans = 1;
		if(gcd(a, n)==1){
			pow = pow%eulerTotient(n);
		}
		while(pow>0){
			if((pow&1) > 0){
				ans = (ans*a)%n;
			}

			a = a*a;
			pow = pow>>1;
		}

		return ans;
	}

	public static void main(String[] args) {
		PrimeNumberUtils primeUtils = new PrimeNumberUtils(1000000);
		System.out.println(primeUtils.isPrime(165576));

		System.out.println(primeUtils.gcd(10, 10));

		Result result = new Result();
		primeUtils.extendedGCD(2, 7, result);
		System.out.println(result.gcd);
		System.out.println(result.x);
		System.out.println(result.y);

		int [] a = {2, 3, 2};
		int [] n = {3, 5, 7};

		System.out.println(primeUtils.chineseRemainder(a, n, 3));
	}

}
