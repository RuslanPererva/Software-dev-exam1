

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class FlushTest {
    @SuppressWarnings("unchecked")
	private static <T extends Serializable> T serialize(T original) throws IOException, ClassNotFoundException  {
    	// save original to stream
    	var bout = new ByteArrayOutputStream();
    	try (var out = new ObjectOutputStream( bout )) {
    		out.writeObject( original );
    	}
    	// read copy from byte array
    	try (var bin = new ByteArrayInputStream( bout.toByteArray() )) {
    		var in = new ObjectInputStream( bin );
    		return (T)in.readObject();
    	}
    }
	@Test
	void testNoStaticNonPrivateFields() {
		BiConsumer<Class<?>,Predicate<Field>> fieldsIn = (c,p) -> Arrays.stream (c.getDeclaredFields())
			      .filter (p::test)
			      .forEach(f->{
				    	  int mod = f.getModifiers();
				    	  Truth.assertWithMessage( String.format( "field '%s' can't be private", f.getName()) )
				    	       .that( Modifier.isPrivate( mod ))
				    	       .isTrue();
						  if (Modifier.isStatic( mod )) {
							  Truth.assertWithMessage( String.format( "field '%s' is static but not final",  f.getName()) )
				    	           .that( Modifier.isFinal( mod ))
				    	           .isTrue();
						  }
			      });
		fieldsIn.accept ( Flush.class, f->!f.isSynthetic());
		Truth.assertWithMessage( String.format( "'%s' should implement Serializable", Flush.class.getSimpleName() ))
		     .that( Flush.class ).isAssignableTo( Serializable.class );
	}
	@Test
	public void test0() throws IOException, ClassNotFoundException {
		var before = new Flush();
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).isEmpty();
		Truth.assertThat( before.getThreshold() ).isEqualTo( 0 );
		Truth.assertThat( after .getNumbers()   ).isEmpty();
		Truth.assertThat( after .getThreshold() ).isEqualTo( 0 );
	}
	@Test
	public void test1() throws IOException, ClassNotFoundException {
		var before = new Flush( 42 );
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 42 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( 0 );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 42 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( 0 );
	}
	@Test
	public void test2() throws IOException, ClassNotFoundException {
		var before = new Flush( 42 );
		before.setThreshold( 42 );
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 42 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( 42 );
		Truth.assertThat( after .getNumbers()   ).isEmpty();
		Truth.assertThat( after .getThreshold() ).isEqualTo( 42 );
	}
	@Test
	public void test3() throws IOException, ClassNotFoundException {
		var before = new Flush( 0,4,2,6,3,5,1 );
		before.setThreshold( 3 );
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 0,4,2,6,3,5,1 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( 3 );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 4,6,5 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( 3 );
	}
	@Test
	public void test4() throws IOException, ClassNotFoundException {
		var before = new Flush( 0,4,2,6,3,5,1 );
		before.setThreshold( 5 );
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 0,4,2,6,3,5,1 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( 5 );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 6 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( 5 );

		before.setThreshold( -1 );
		
		after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 0,4,2,6,3,5,1 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( -1 );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 0,4,2,6,3,5,1 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( -1 );
	}
	@Test
	public void test5() throws IOException, ClassNotFoundException {
		var before = new Flush( 1,0,Integer.MIN_VALUE,-1,Integer.MAX_VALUE, 1 );
		
		var after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 1,0,Integer.MIN_VALUE,-1,Integer.MAX_VALUE, 1 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( 0 );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 1,Integer.MAX_VALUE, 1 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( 0 );

		before.setThreshold( Integer.MIN_VALUE );
		
		after  = serialize( before ); 
		Truth.assertThat( before.getNumbers()   ).containsExactly( 1,0,Integer.MIN_VALUE,-1,Integer.MAX_VALUE, 1 );
		Truth.assertThat( before.getThreshold() ).isEqualTo( Integer.MIN_VALUE );
		Truth.assertThat( after .getNumbers()   ).containsExactly( 1,0,-1,Integer.MAX_VALUE, 1 );
		Truth.assertThat( after .getThreshold() ).isEqualTo( Integer.MIN_VALUE );
	}
}
