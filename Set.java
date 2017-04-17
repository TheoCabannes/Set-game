import java.util.Stack;

public class Set {
	private Stack<Integer> list;
	
	protected Set(){
		list = new Stack<Integer>();
		int[] a=melange();
		for(int i=0; i<81;i++)
			list.add(a[i]);
	}
	
	protected boolean isEmpty(){
		return list.isEmpty();
	}
	
	//Melange des cartes ensuite mis dans une pile. On utilise un tableau temporaire
	private static int[] melange(){
		int[] array = new int[81];
		int n=0;
		for(int j=1;j<4;j++)
			for(int k=1;k<4;k++)
				for(int l=1; l<4; l++)
					for(int m=1; m<4; m++)
						array[n++]=valueOf(j, k, l, m);
		for (int i = 1; i < array.length; i++) { // Mélange de Fisher-Yates
		    int j = (int)(Math.random() * (i + 1)); // j dans 0..i
		    int tmp = array[i];
		    array[i]=array[j];
		    array[j]=tmp;
		  }
		return array;
	}
	
	protected int getCard(){
		if(list.isEmpty())
			return -1;
		return list.pop();
	}
	
	protected static int valueOf(int number, int color, int filling, int shape) {
        if (number <= 0 || number > 3 ||
            color <= 0 || color > 3 ||
            filling <= 0 || filling > 3 ||
            shape <= 0 || shape > 3) {
            throw new IllegalArgumentException("Characteristics out of range.");
        }
        return number | (color << 2) | (filling << 4) | (shape << 6);
    }
}
