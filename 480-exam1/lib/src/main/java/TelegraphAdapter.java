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
		for (char c : s.toCharArray()) {
			char lc = Character.toLowerCase(c);
			if (Morse.containsKey(lc)) {
				String morseCode = Morse.get(lc);
				char[] ctemp = morseCode.toCharArray();
				int loc = 0;
				while (loc < ctemp.length) {
					if (ctemp[loc]=='-' && loc  != ctemp.length) {
						int dashtemp = 0;
						while (ctemp[loc]=='-') {
							dashtemp+=1;
							loc+=1;
						}
						t.dash(dashtemp);
					}
					else if (ctemp[loc]=='.'&& loc  != ctemp.length) {
						int dottemp = 0;
						while (ctemp[loc]=='.') {
							dottemp+=1;
							loc+=1;
						}
						t.dot(dottemp);
					}
					else if (ctemp[loc]==' '&& loc  != ctemp.length) {
						t.gap();
					}
					
				}
				
			}
			else {
				throw new IllegalArgumentException("wrong character '" + c+ "'");
			}
			t.word();
		}
		String ret = t.end();
		return ret;
	}

}
