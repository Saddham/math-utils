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
	              result.y = temp - (a/b) * result.y; //rn-2= rn-1 - (an/bn) * rn
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
