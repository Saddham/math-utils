package commons.utils.math;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

	public Map<Integer, Integer> uniquePrimeFactors(int n) {
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

	public boolean isProbablePrime(int n){
		if(powModuloN(2, n-1, n)==1){
			return true;
		}

		return false;
	}

	/*    Start: 29/01/2016*/
	public boolean millerRabinPrimalityTest(int p, int iteration){
		if(p<2){
			return false;
		}

		if(p!=2 && p%2==0){
			return false;
		}

		int m=p-1;
		while(m%2==0){
			m/=2;
		}

		int i, a, temp, b;
		Random random = new Random();
		for(i=0;i<iteration;i++){
			a = random.nextInt(p-1)+1;
			temp = m;
			b=powModuloN(a,temp,p);
			while(temp!=p-1 && b!=1 && b!=p-1){
				b = (b*b) % p;
				temp *= 2;
			}
			if(b!=p-1 && temp%2==0){
				return false;
			}
		}

		return true;
	}


	/**
	 * Fermat's test for checking primality, the more iterations the more is accuracy
	 * Doesn't work for Carmichael numbers for which all values of a<p for which
	 * gcd(a,p)=1, (a^(p-1))%p = 1.
	 *
	 * */
	public boolean fermatPrimalityTest(int p,int iterations){
		if(p == 1){ // 1 isn't prime
			return false;
		}

		Random random = new Random();
		int i, a;
		for(i=0;i<iterations;i++){
			// choose a random integer between 1 and p-1 ( inclusive )
			a = random.nextInt(p-1)+1;
			// powModuloN is the function we developed above for modular exponentiation.
			if(powModuloN(a,p-1,p) != 1){
				return false; /* p is definitely composite */
			}
		}
		return true; /* p is probably prime */
	}


	/**
	 * Legendre Symbol:
	 * This symbol is defined for a pair of integers a and p such that p is prime.
	 * It is denoted by (a/p) and calculated as:
	 * */
	public int lengendreSymbol(int a, int p){
		if(a%p==0)
			return 0;
		if(powModuloN(a, (p-1)/2, p)==1){
			return 1;
		}

		return -1;
	}

	//calculates Jacobian(a/n) n>0 and n is odd
	public int calculateJacobian(int a,int n){
		if(a==0) return 0; // (0/n) = 0
		int ans=1;
		if(a<0){
			a=-a;    // (a/n) = (-a/n)*(-1/n)
			if(n%4==3) ans=-ans; // (-1/n) = -1 if n = 3 ( mod 4 )
		}
		if(a==1) return ans; // (1/n) = 1
		while(a>0){
			if(a<0){
				a=-a;    // (a/n) = (-a/n)*(-1/n)
				if(n%4==3) ans=-ans;    // (-1/n) = -1 if n = 3 ( mod 4 )
			}
			while(a%2==0){
				a=a/2;    // Property (iii)
				if(n%8==3||n%8==5) ans=-ans;   
			}

			//System.out.println("a: "+a+" n: "+n );
			a = a^n;
			n = a^n;
			a = a^n;// Property (iv)
			//System.out.println("a: "+a+" n: "+n );

			if(a%4==3 && n%4==3) ans=-ans; // Property (iv)
			a=a%n; // because (a/p) = (a%p / p ) and a%pi = (a%n)%pi if n % pi = 0
			if(a>n/2) a=a-n;
		}
		if(n==1) return ans;
		return 0;
	}

	/* Iterations determine the accuracy of the test */
	public boolean solovoyPrimalityTest(int p, int iteration){
		if(p<2)
			return false;

		if(p!=2 && p%2==0)
			return false;

		Random random = new Random();
		int i, a, jacobian, mod;
		for(i=0;i<iteration;i++){
			a = random.nextInt(p-1)+1;
			jacobian = (p+calculateJacobian(a,p))%p;
			mod = powModuloN(a, (p-1)/2, p);

			if(jacobian==0 || mod!=jacobian){
				return false;
			}
		}
		return true;
	}

	/*End: 29/01/2016*/
	
	public long [] coPrimeSieve;
	public final int n = 10000001;
	public void superFastEulerTotientWithCoPrimeSeive(){
		coPrimeSieve = new long[n];
        coPrimeSieve[1] = 1;
        for(int i = 2; i < n; i++) {
            if (coPrimeSieve[i] == 0) {
                coPrimeSieve[i] = i - 1;
                for (int j = 2; i * j < n; j++) {
                    if (coPrimeSieve[j] == 0)
                        continue;
 
                    int q = j;
                    int f = i - 1;
                    while (q % i == 0) {
                        f *= i;
                        q /= i;
                    }
                    coPrimeSieve[i * j] = f * coPrimeSieve[q];
                }
            }
        }
        
        for(int i = 2; i < n; i++) {
        	coPrimeSieve[i] += coPrimeSieve[i-1];
        }
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
		
		Map<Integer, Integer> factorPower = primeUtils.uniquePrimeFactors(315);
        Set<Integer> keySet = factorPower.keySet();
        for(Integer key : keySet){
              System.out.println("Factor: "+key+" Power: "+factorPower.get(key));
        }

        System.out.println(primeUtils.eulerTotient(7));
        System.out.println(primeUtils.gcd(7, 100));
        System.out.println(primeUtils.powModuloN(7, 91, 100));
		
        System.out.println(primeUtils.isProbablePrime(31));

		System.out.println("Miller Rabin:");
		for(int i=1; i<100; i++){
			if(primeUtils.millerRabinPrimalityTest(i, 10)){
				System.out.println(i);
			}
		}    

		System.out.println("Fermat:");
		for(int i=5; i<100; i++){
			if(primeUtils.fermatPrimalityTest(i, 10)){
				System.out.println(i);
			}
		}    

		for(int i=1; i<100; i++){
			System.out.println("Legendre Symbol 155/"+i+": "+primeUtils.lengendreSymbol(155, i));
		}

		for(int i=1; i<100; i++){
			System.out.println("Jacobian 155/"+i+": "+primeUtils.calculateJacobian(155, i));
		}

		System.out.println("Solovoy:");
		for(int i=5; i<100; i++){
			if(primeUtils.solovoyPrimalityTest(i, 50)){
				System.out.println(i);
			}
		}	
	}
}
