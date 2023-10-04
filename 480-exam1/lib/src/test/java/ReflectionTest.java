

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.truth.Truth;

class ReflectionTest {
	@Test
	void testNoFieldsDefined() {
		Function<Class<?>,Optional<String>> fieldsNone = c -> Arrays.stream( c.getDeclaredFields() )
				                                                    .filter( f->!f.isSynthetic() )
				                                                    .map( Field::getName ).findFirst();
		var found = fieldsNone.apply( Reflection.class );
		Truth.assertWithMessage( String.format( "no fields expected; but found '%s'", found.orElse( "" )))
		     .that             ( found.isEmpty() )
		     .isTrue();
	}
	@ParameterizedTest
	@MethodSource("dataGetClassName")
	void testGetClassName(Object object, String expected) {
		String actual = Reflection.getClassName( object );
		Truth.assertThat( actual ).isNotNull();
		Truth.assertThat( actual ).isEqualTo( expected );
	}
	static Stream<Arguments> dataGetClassName() {
	    return Stream.of(
	    	Arguments.of( null,                 ""               ),
	    	Arguments.of( "yay",                "String"         ),
	    	Arguments.of( Integer.valueOf(42),  "Integer"        ),
	    	Arguments.of( new double[0],        "double[]"       ),
	    	Arguments.of( PublicEnum.ONE,       "PublicEnum"     ),
	    	Arguments.of( new JPanel(),         "JPanel"         ),
	    	Arguments.of( new ReflectionTest(), "ReflectionTest" )
	    );
	}

	@ParameterizedTest
	@MethodSource("dataKeepArrays")
	void testKeepArrays(List<Object> input, List<Object> expected) {
		List<Object> actual = new ArrayList<>( input );
		Reflection.keepArrays( actual );
		Truth.assertThat( actual ).containsExactlyElementsIn( expected ).inOrder();
	}
	static Stream<Arguments> dataKeepArrays() {
		Object[] obs = {
		/* 0 */	Integer.valueOf( 42 ),
		/* 1 */	new Integer[]  { 42 },
		/* 2 */	Double .valueOf( 42 ),
		/* 3 */	new Double [] {},
		/* 4 */	String .valueOf( 42 ),
		/* 5 */	new String [] {},
		/* 6 */	Boolean.FALSE,
		/* 7 */	new Boolean[] {},
		/* 8 */ Optional.empty(),
		/* 9 */ Stream.empty()	
		};
	    return Stream.of(
	    	Arguments.of( List.of() , 
	    			      List.of() ),
	    	Arguments.of( List.of( obs[0] ) , 
  			              List.of() ),
	    	Arguments.of( List.of( obs[2],obs[3] ) , 
		                  List.of( obs[3] )),
	    	Arguments.of( List.of( obs[0],obs[2],obs[3],obs[5],obs[6],obs[7] ), 
  	                      List.of( obs[3],obs[5],obs[7] ) ),
	    	Arguments.of( List.of( obs[0],obs[1],obs[2],obs[5],obs[4] ), 
	                      List.of( obs[1],obs[5] )),
  	    	Arguments.of( List.of( obs[6],obs[4],obs[7],obs[2],obs[0] ) ,
	    			      List.of( obs[7] )),
	    	Arguments.of( List.of( obs[8],obs[9],obs[1] ), 
                          List.of( obs[1] ))
	    );
	}

	@ParameterizedTest
	@MethodSource("dataGetEnumConstants")
	void testGetEnumConstants(Class<?> clazz, List<String> expected) {
		List<String>      actual = Reflection.getEnumConstants( clazz );
		Truth.assertThat( actual ).isNotNull();
		Truth.assertThat( actual ).isEqualTo( expected );
	}
	static Stream<Arguments> dataGetEnumConstants() {
	    return Stream.of(
		    	Arguments.of( Object.class,        List.of() ),
		    	Arguments.of( PublicEnum.class,    List.of( "ONE" )),
		    	Arguments.of( ProtectedEnum.class, List.of( "A", "L", "M", "O", "S", "T" )),
		    	Arguments.of( PrivateEnum.class,   List.of( "B", "O", "X" )),
		    	Arguments.of( JustEnum.class,      List.of( "BAR", "FOO", "JINX", "MEH" ))
	    		);
	}
	protected enum ProtectedEnum { T, M, A, O, L, S }
	private   enum PrivateEnum   { X, B, O }
	public    enum PublicEnum    { ONE }
	          enum JustEnum      { FOO, BAR, JINX, MEH }
	
	@ParameterizedTest
	@MethodSource("dataToString")
	void testToString(Class<?> clazz, String expected) {
		String            actual = Reflection.toString( clazz );
		Truth.assertThat( actual ).isNotNull();
		Truth.assertThat( actual ).isEqualTo( expected );
	}
	static Stream<Arguments> dataToString() {
	    return Stream.of(
		    	Arguments.of( Object.class,                    "public class Object" ),
		    	Arguments.of( String.class,                    "public final class String" ),
		    	Arguments.of( PrivateClass.class,              "private class PrivateClass" ),
		    	Arguments.of( PrivateStaticClass.class,        "private static class PrivateStaticClass" ),
		    	Arguments.of( ProtectedStaticClass.class,      "protected static class ProtectedStaticClass" ),
		    	Arguments.of( ProtectedAbstractClass.class,    "protected abstract class ProtectedAbstractClass" ),
		    	Arguments.of( PublicFinalStaticClass.class,    "public final static class PublicFinalStaticClass" ),
		    	Arguments.of( PublicAbstractStaticClass.class, "public abstract static class PublicAbstractStaticClass" ),
		    	Arguments.of( NoneClass.class,                 "class NoneClass" ),
		    	Arguments.of( NoneFinalClass.class,            "final class NoneFinalClass" ),
		    	Arguments.of( NoneAbstractClass.class,         "abstract class NoneAbstractClass" ),
		    	Arguments.of( Iterator.class,                  "public interface Iterator" ),
		    	Arguments.of( NoneInterface.class,             "interface NoneInterface" ),
		    	Arguments.of( NoneAbstractInterface.class,     "interface NoneAbstractInterface" ),
		    	Arguments.of( ProtectedInterface.class,        "protected interface ProtectedInterface" ),
		    	Arguments.of( PrivateAbstractInterface.class,  "private interface PrivateAbstractInterface" ),
		    	Arguments.of( ProtectedEnum.class,             "protected static enum ProtectedEnum" ),
		    	Arguments.of( PrivateEnum.class,               "private static enum PrivateEnum" ),
		    	Arguments.of( JustEnum.class,                  "static enum JustEnum" )
	    		);
	}
	private class PrivateClass { }
	private static class PrivateStaticClass { }
	protected static class ProtectedStaticClass { }
	protected abstract class ProtectedAbstractClass { }
	public final static class PublicFinalStaticClass { }
	public abstract static class PublicAbstractStaticClass { }
	class NoneClass {}
	final class NoneFinalClass {}
	abstract class NoneAbstractClass {}
	interface NoneInterface {}
	abstract interface NoneAbstractInterface {}
	protected interface ProtectedInterface {}
	private abstract interface PrivateAbstractInterface {}
//
//	@ParameterizedTest
//	@MethodSource("dataMethodParamsToString")
//	void testMethodParamsToString(Class<?> clazz, String name, List<String> expected) {
//		List<String>      actual = Reflection.methodParamsToString( clazz, name );
//		Truth.assertThat( actual ).isNotNull();
//		Truth.assertThat( actual ).containsExactlyElementsIn( expected );
//	}
//	static Stream<Arguments> dataMethodParamsToString() {
//	    return Stream.of(
//		    	Arguments.of( Iterator    .class, "foo",     List.of() ),
//		    	Arguments.of( Object      .class, "equals",  List.of( "equals(Object)" )),
//		    	Arguments.of( Object      .class, "wait",    List.of( "wait()","wait(long)","wait(long,int)" )),
//		    	Arguments.of( String      .class, "valueOf", List.of( "valueOf(int)","valueOf(float)","valueOf(boolean)","valueOf(long)","valueOf(double)","valueOf(Object)","valueOf(char)","valueOf(char[])","valueOf(char[],int,int)" )),
//		    	Arguments.of( AbstractList.class, "add",     List.of( "add(Object)","add(int,Object)" )),
//		    	Arguments.of( PublicEnum  .class, "values",  List.of( "values()" ))
//	    		);
//	}
}
