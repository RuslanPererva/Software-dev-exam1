import static org.mockito.ArgumentMatchers.booleanThat;

import java.util.HashMap;

public class TelegraphAdapter {
	
	
	
	
	
	static String toMorse(Telegraph t, String s) {
		HashMap<Character, String> Morse = new HashMap<Character, String>();
		Morse = new HashMap<>();
        Morse.put('a', ".-");
        Morse.put('b', "-...");
        Morse.put('c', "-.-.");
        Morse.put('d', "-..");
        Morse.put('e', ".");
        Morse.put('f', "..-.");
        Morse.put('g', "--.");
        Morse.put('h', "....");
        Morse.put('i', "..");
        Morse.put('j', ".---");
        Morse.put('k', "-.-");
        Morse.put('l', ".-..");
        Morse.put('m', "--");
        Morse.put('n', "-.");
        Morse.put('o', "---");
        Morse.put('p', ".--.");
        Morse.put('q', "--.-");
        Morse.put('r', ".-.");
        Morse.put('s', "...");
        Morse.put('t', "-");
        Morse.put('u', "..-");
        Morse.put('v', "...-");
        Morse.put('w', ".--");
        Morse.put('x', "-..-");
        Morse.put('y', "-.--");
        Morse.put('z', "--..");
        
		t.start();
		boolean addgap = true;
		char[] schar = s.toCharArray();
		int maxgaps = schar.length-1;
		int curgaps = 0;
		for (int x =0; x<schar.length; x++) {
			char c = schar[x];
			char lc = Character.toLowerCase(c);
			
			if (x<schar.length-2 && schar[x+1]==' ') {
				addgap=false;
			}
			if (c ==' ') {
				t.word();
				
			}
			else if (Morse.containsKey(lc)) {
				String morseCode = Morse.get(lc);
				char[] ctemp = morseCode.toCharArray();
				int count = 0;
				char prevChar = '-';
				for (int loc = 0; loc < ctemp.length; loc++) {
					if (ctemp[loc]==prevChar) {
						count += 1;
					}
					else if (ctemp[loc]=='.') {
						if (count>0) {
							t.dash(count);
						}
						count=1;
						prevChar=ctemp[loc];
					}
					else if (ctemp[loc]=='-') {
						if (count>0) {
							t.dot(count);
						}
						count=1;
						prevChar=ctemp[loc];
					}
					}
				if (count>0 && prevChar == '.') {
					t.dot(count);
				}
				else if (count>0 && prevChar == '-') {
					t.dash(count);
				}
				if (addgap==true && curgaps != maxgaps) {
				t.gap();
				curgaps +=1;
				}
				else { addgap = true;}
			}
			else {
				throw new IllegalArgumentException("wrong character '" + c+ "'");
			}
		}
		String ret = t.end();
		if (ret.length()>1 && ret.charAt(ret.length()-1)==' ') {
		return ret.substring(0, ret.length()-1);
		}
		else {
			return ret;
		}
	}
	
	
	
	
	
    public static void main(String[] args){
    	String temp = TelegraphAdapter.toMorse(new Telegraph(), "sos");
    	System.out.println(temp);
		
    	
	}
    

}
