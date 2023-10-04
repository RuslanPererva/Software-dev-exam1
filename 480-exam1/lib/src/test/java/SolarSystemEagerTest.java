

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

//** EAGER ** //
class SolarSystemEagerTest {
	private static final String CLASS_NAME    = "SolarSystemEager"; 
	private static       Object instanceValue = null;
	private static       Field  instanceField = null;

	private static final String       THE_SUN     = "The Sun";
	private static final List<String> THE_PLANETS = List.of("Mercury","Venus","Earth","Mars","Jupiter","Saturn","Uranus","Neptune");

	private Class<?> getClazz(String name) {
		Class<?> result = null;
		try {
			Package pkg  = getClass().getPackage();
			String  path = (pkg == null || pkg.getName().isEmpty()) ? "" : pkg.getName()+".";
			result = Class.forName( path + name );
		} catch (ClassNotFoundException e) {
			fail( String.format( "Class %s not found", name ));
		}
		return result;
	}
	private static void hasMethod(Class<?> clazz, Class<?> result, String name, Class<?>... args) {
		try {
			var method = clazz.getDeclaredMethod( name, args );

			Truth.assertWithMessage( String.format("'%s.%s' should be public", clazz.getSimpleName(), name ))
 				 .that( Modifier.isPublic( method.getModifiers() ))
				 .isTrue();

			Truth.assertWithMessage( String.format("'%s.%s' should return a value of type '%s'", clazz.getSimpleName(), name, result.getSimpleName() ))
				 .that( method.getReturnType() )
				 .isEqualTo( result );
		} catch (NoSuchMethodException | SecurityException e) {
			String params = Arrays.stream( args ).map( Class::getSimpleName ).collect( Collectors.joining( "," ));
			String msg    = String.format( "'%s' doesn't have method '%s %s(%s)'", clazz.getSimpleName(), result.getSimpleName(), name, params );
			fail( String.format( msg ));
		}					
	}
	private static final BiFunction<Class<?>,Predicate<Field>,List<Field>> getFields = (clazz,predicate) -> Arrays.stream ( clazz.getDeclaredFields())
             .filter ( f->!f.isSynthetic())
             .filter ( predicate )
             .collect( Collectors.toList());

	private static final BiFunction<Object,Field,?> getFieldValue = (object,field) -> {
		try {
			field.setAccessible( true );
			var value = field.get( object );
			return value;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	};
	private static final BiFunction<Object,Field,Predicate<Object>> setFieldValue = (object,field) -> value -> {
		try {
			field.setAccessible( true );
			field.set( object, value );
			return true;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	};
	private static final Function<Field,?> getStaticFieldValue = field -> getFieldValue.apply( null, field );

	@BeforeEach
	void testInstanceIsInitializedEagerly() {
		// has the instance field been read before? (field == null if it hasn't been read before) 
		// if not:
		//    get all static fields
		//    there should be only 1 static field (if not fail)
		//    this field should be final (if not fail).
		//    this field should have a value (if not fail: instance field is not eagerly initialized)
		//    this value should be of the singleton class (if not fail: not a singleton variable)
		//    if all OK: initialize instance field and value
		// read instance value:
		//    it should not be null (fail)
		//    it should be the same instance read earlier (if not fail: instance modified after initialization).
		if (instanceField == null) {
			var              clazz     = getClazz( CLASS_NAME );
			Predicate<Field> areStatic = f -> Modifier.isStatic( f.getModifiers() );
			var              fields    = getFields.apply( clazz, areStatic );
			
			Truth.assertWithMessage( "only one static variable should exist" )
			     .that( fields )
			     .hasSize( 1 );
	
			var field = fields.get(0);
			var name  = field.getName();
			Truth.assertWithMessage( String.format( "static field '%s.%s' should be final", CLASS_NAME, name ))
				 .that( Modifier.isFinal( field.getModifiers() ))
				 .isTrue();
		
			var value = getStaticFieldValue.apply( field );
			Truth.assertWithMessage( String.format( "field '%s.%s' is not eagerly initialized", CLASS_NAME, name ))
				 .that( value )
				 .isNotNull();
			
			Truth.assertWithMessage( String.format( "field '%s.%s' has the wrong type", CLASS_NAME, name ))
			     .that( value )
			     .isInstanceOf( clazz );
			
			instanceField = field;
			instanceValue = value;
		}
		var value = getStaticFieldValue.apply( instanceField );
		var name  = instanceField.getName();

		Truth.assertWithMessage( String.format( "unique instance '%s.%s' changed value after initialization", CLASS_NAME, name ))
		     .that( value )
		     .isSameInstanceAs( instanceValue );
	}

	@Test
	void testFieldsArePrivate() {
		var clazz = getClazz( CLASS_NAME );
		Arrays.stream ( clazz.getDeclaredFields() )
		      .filter ( f -> !f.isSynthetic() )
		      .forEach( f -> Truth.assertWithMessage( String.format( "field '%s.%s' is not private", CLASS_NAME, f.getName() ))
		    		              .that             ( Modifier.isPrivate( f.getModifiers() ))
		    		              .isTrue() );
	}
	@Test
	void testOnePrivateConstructor() {
		var clazz        = getClazz( CLASS_NAME );
		var constructors = Arrays.stream (clazz.getDeclaredConstructors()).collect( Collectors.toList() );
		
		Truth.assertThat( constructors )
		     .hasSize( 1 );
		
		var constructor  = constructors.get(0);
		Truth.assertWithMessage( String.format( "constructor in '%s' should be private", CLASS_NAME ))
			 .that( Modifier.isPrivate( constructor.getModifiers() ))
			 .isTrue();
	}
	@Test
	void testHasMethods() {
		var clazz = getClazz( CLASS_NAME );
		hasMethod( clazz, String.class, "getStar"     );
		hasMethod( clazz, List.class,   "getPlanets"  );
		hasMethod( clazz, clazz,        "getInstance" );
	}
	@Test
	void testGetInstanceReturnsSameObject() {
		var one = SolarSystemEager.getInstance();
		Truth.assertWithMessage( "getInstance() should return an object" )
		     .that( one )
		     .isNotNull();

		var two = SolarSystemEager.getInstance();
		Truth.assertWithMessage( "getInstance() should return an object" )
		     .that( two )
		     .isNotNull();
		Truth.assertWithMessage( "getInstance() should return the same object" )
		     .that( two )
		     .isSameInstanceAs( one );
	}
	@Test
	public void testFieldsHaveValues() {
		var instance = SolarSystemEager.getInstance();
		Truth.assertWithMessage( "getInstance() should return an object" )
		     .that( instance )
		     .isNotNull();
		// star
		var              clazz     = getClazz( CLASS_NAME );
		Predicate<Field> areString = f -> f.getType().isAssignableFrom( String.class );
		var              strings   = getFields.apply( clazz, areString );
		Truth.assertWithMessage( "one string field expected" )
		     .that( strings )
		     .hasSize( 1 );
		var              string    = strings.get( 0 );
		var              star      = getFieldValue.apply( instance, string );
		Truth.assertWithMessage( String.format( "field '%s.%s' should be initialized", CLASS_NAME, string.getName() ))
		     .that( star )
		     .isNotNull();
		Truth.assertWithMessage( String.format( "field '%s.%s' has unexpected value", CLASS_NAME, string.getName() ))
		     .that( star )
		     .isEqualTo( THE_SUN );

		// planets
		Predicate<Field> areList   = f -> f.getType().isAssignableFrom( List.class );
		List<Field>      lists     = getFields.apply( clazz, areList );
		Truth.assertWithMessage("one list field expected")
		     .that( lists )
		     .hasSize( 1 );
		var              list      = lists.get( 0 );
		var              planets   = (List<?>)getFieldValue.apply( instance, list );
		Truth.assertWithMessage( String.format( "field '%s.%s' should be initialized", CLASS_NAME, list.getName() ))
		     .that( planets )
		     .isNotNull();
		Truth.assertWithMessage( String.format( "field '%s.%s' has unexpected value", CLASS_NAME, list.getName() ))
		     .that( planets )
		     .containsExactlyElementsIn( THE_PLANETS );
	}
	@Test
	public void testGettersUseFields() {
		var instance = SolarSystemEager.getInstance();
		Truth.assertWithMessage( "getInstance() should return an object" )
		     .that( instance )
		     .isNotNull();
		// star
		var              clazz     = getClazz( CLASS_NAME );
		Predicate<Field> areString = f -> f.getType().isAssignableFrom( String.class );
		var              strings   = getFields.apply( clazz, areString );
		Truth.assertWithMessage( "one string field expected" )
		     .that( strings )
		     .hasSize( 1 );
		var              string    = strings.get( 0 );
		var              star      = getFieldValue.apply( instance, string );
		Truth.assertWithMessage( String.format( "field '%s.%s' should be initialized", CLASS_NAME, string.getName() ))
		     .that( star )
		     .isNotNull();
		Truth.assertWithMessage( String.format( "field '%s.%s' has unexpected value", CLASS_NAME, string.getName() ))
		     .that( star )
		     .isEqualTo( THE_SUN );

		// planets
		Predicate<Field> areList   = f -> f.getType().isAssignableFrom( List.class );
		List<Field>      lists     = getFields.apply( clazz, areList );
		Truth.assertWithMessage("one list field expected")
		     .that( lists )
		     .hasSize( 1 );
		var              list      = lists.get( 0 );
		var              planets   = (List<?>)getFieldValue.apply( instance, list );
		Truth.assertWithMessage( String.format( "field '%s.%s' should be initialized", CLASS_NAME, list.getName() ))
		     .that( planets )
		     .isNotNull();
		Truth.assertWithMessage( String.format( "field '%s.%s' has unexpected value", CLASS_NAME, list.getName() ))
		     .that( planets )
		     .containsExactlyElementsIn( THE_PLANETS );

		// changing fields, calling methods with new values
		Truth.assertThat( setFieldValue.apply( instance, string ).test(          "foo"  )).isTrue(); 
		Truth.assertThat( setFieldValue.apply( instance, list   ).test( List.of( "bar" ))).isTrue();
		Truth.assertWithMessage( "getStar() should return a field value (is value hardcoded in the method?)" )
		     .that( instance.getStar()    )
             .isEqualTo( "foo" );
		Truth.assertWithMessage( "getPlanets() should return a field value (is value hardcoded in the method?)" )
		     .that( instance.getPlanets() )
             .containsExactly( "bar" );

		// restoring fields, calling methods to verify
		Truth.assertThat( setFieldValue.apply( instance, string ).test( THE_SUN     )).isTrue(); 
		Truth.assertThat( setFieldValue.apply( instance, list   ).test( THE_PLANETS )).isTrue();

		Truth.assertWithMessage( "getStar() returned unexpected value" )
		     .that( instance.getStar() )
		     .isEqualTo( THE_SUN );
		Truth.assertWithMessage( "getPlanets() returned unexpected value" )
		     .that( instance.getPlanets() )
		     .containsExactlyElementsIn( THE_PLANETS );
	}
}