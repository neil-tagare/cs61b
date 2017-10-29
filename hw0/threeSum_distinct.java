public class threeSum_distinct{
    public static void main(String[] input) {
      int[] a=new int[input.length];
       for(int i=0; i<input.length; i++){
          a[i]=Integer.parseInt(input[i]);
       }
       System.out.println(threeSum_distinct(a));
    }
    public static boolean threeSum_distinct(int[] a){

     for(int i=0; i<a.length; i++){
        int x=a[i];
        for(int j=i+1; j<a.length; j++){
          int y=a[j];
          for(int k=j+1; k<a.length; k++){
              int z=a[k];
              if(x+y+z==0){return true;}
           }
        }
      }
      return false;
      }
}


	
