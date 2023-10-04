import java.lang.annotation.Annotation;
import java.util.List;

interface Reflection {
	static String getClassName(Object o) {
		if (o != null) {
		return o.getClass().getSimpleName();
		}
		else {return "";}
	}
	static List<String> getEnumConstants(Class<?> clazz) {
		return null;
	}
	static void keepArrays(List<Object> list) {
		for (int x =0; x<list.size(); x++) {
			if (list.get(x).getClass().toString()!="class java.lang.Array") {
				list.remove(x);
			}
		}
	}
	static String toString(Class<?> clazz) {
		//would not let me split the string, planned to check each thing in the list
		char[] temp = clazz.getCanonicalName().toCharArray();
		List<String> stringList = null;
		for (char c : temp) {
			String temptwo = "";
		if (c=='.') {
			stringList.add(temptwo);
			temptwo = "";
		}
		else {
			temptwo+=c;
		}
		}
	}
}
