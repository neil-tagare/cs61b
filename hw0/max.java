public class max {
    public static void main(String[] a) {
       int k=a.length-1;
        String max_value=a[k];
       while(k>0){
        max_value=max_two(a[k-1],max_value);
        k -=1;
        // System.out.println(k +"maxvalue"+ max_value);
       }
       System.out.println ("max value is " + max_value);
       // System.out.println(a.length);
    	}
   		private static String max_two(String x, String y){
			if(Integer.parseInt(x)>Integer.parseInt(y)){
			return x;
			}else{
			return y;
				}
		}
}

	
