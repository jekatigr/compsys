package algorithm1;

import comparative_system.Counter;

public class Max{
  public static int max(int[] arr) {
    Counter.add(2);
	int max = arr[0];
    Counter.add(2);
	for(int i = 0; i < arr.length; i++) {
		Counter.add(3);
		{
		  Counter.add(2);
		if (max < arr[i]) {
		  	Counter.add(2);
			max = arr[i];
		  }
		}
	}
    return max;
  }
}


