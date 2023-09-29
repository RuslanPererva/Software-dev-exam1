

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

class HandMeDownProxyTest {
	private static final class HandMeDown implements IHandMeDown {
		private String name;
		@Override
		public void setOwner(String name) {
			this.name = name;
		}
		@Override
		public String getOwner() {
			return name;
		}			
	};
	@Test
	void testClassStructure() {
		BiConsumer<Class<?>,Class<?>>
		hasSuperClass = (a,b) -> Truth.assertWithMessage( String.format( "'%s' is not the superclass of '%s'", b.getSimpleName(), a.getSimpleName() ))
                                      .that( a.getSuperclass() ).isEqualTo( b );
		BiConsumer<Class<?>,List<Class<?>>> 
		hasInterfaces = (a,b) -> Truth.assertThat( a.getInterfaces() )
                                      .asList().containsExactlyElementsIn( b );
		Consumer<Class<?>>
		hasNoFields = c -> Arrays.stream( c.getDeclaredFields() ).filter( f->!f.isSynthetic() )
				                 .forEach( f->fail( String.format( "No fields expected; found '%s %s'", f.getType().getName(), f.getName() )));
		Consumer<Class<?>> 
		hasPrivateFields = c -> Arrays.stream( c.getDeclaredFields() ).filter( f->!f.isSynthetic() ).forEach( f->{
			var mods = f.getModifiers();
			var name = f.getName();
			Truth.assertWithMessage( String.format("field '%s' is not private", name )).that( Modifier.isPrivate( mods )).isTrue();
		});
		
		hasInterfaces.accept( HandMeDownProxyFactory.class, List.of() );
		hasSuperClass.accept( HandMeDownProxyFactory.class, Object.class );
		hasNoFields  .accept( HandMeDownProxyFactory.class );

		var proxy = HandMeDownProxyFactory.get( new HandMeDown() );
		Truth.assertThat( proxy ).isNotNull();
		hasInterfaces   .accept( proxy.getClass(), List.of( IHandMeDown.class, IHandMeDownHistory.class ));
		hasSuperClass   .accept( proxy.getClass(), Proxy.class );
		hasPrivateFields.accept( proxy.getClass() );
	}
	
	@Test
	void testNullHandMeDownThrowsException() {
		var t = assertThrows(
				IllegalArgumentException.class,
				() -> HandMeDownProxyFactory.get( null ));		
		Truth.assertThat( t.getMessage() )
		     .isEqualTo( "subject cannot be null" );
	}
	
	@Test
	void testNullOwnerThrowsException() {
		var subject = new HandMeDown();
		var proxy   = HandMeDownProxyFactory.get( subject );
		Truth.assertThat( proxy ).isNotNull();

		var t = assertThrows(
				IllegalArgumentException.class,
				() -> proxy.setOwner( null ));		
		Truth.assertThat( t.getMessage() )
		     .isEqualTo( "owner cannot be null" );
	}

	@Test
	void testHandMeDownGetSetOwner() {
		var subject = Mockito.spy( new HandMeDown() );
		var proxy   = HandMeDownProxyFactory.get( subject );
		Truth.assertThat( proxy  ).isNotNull();

		var owner   = proxy.getOwner();
		Truth.assertThat( owner  ).isNull();
		Mockito.verify( subject, Mockito.atLeastOnce() ).getOwner();

		proxy.setOwner( "Count Olaf" );
		Mockito.verify( subject ).setOwner( "Count Olaf" );

		owner = proxy.getOwner();
		Truth.assertThat( owner  ).isNotNull();
		Truth.assertThat( owner  ).isEqualTo( "Count Olaf" );
		Mockito.verify( subject, Mockito.atLeast( 2 )).getOwner();
	}

	@Test
	void testHandMeDownHistoryGetOwnersNoName() {
		var a       = new HandMeDown();
		var proxy   = HandMeDownProxyFactory.get( a );
		Truth.assertThat( proxy  ).isNotNull();
		Truth.assertThat( proxy  ).isInstanceOf( IHandMeDownHistory.class );
		var owners  = ((IHandMeDownHistory)proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).isEmpty();
	}

	@Test
	void testHandMeDownHistoryGetOwnersExistingName() {
		var a       = new HandMeDown();
		a.setOwner( "Foo" );
		
		var proxy   = HandMeDownProxyFactory.get( a );
		Truth.assertThat( proxy  ).isNotNull();
		Truth.assertThat( proxy  ).isInstanceOf( IHandMeDownHistory.class );
		var owners  = ((IHandMeDownHistory) proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).containsExactly( "Foo" );

		proxy.setOwner( "Bar" );
		
		owners  = ((IHandMeDownHistory) proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).containsExactly( "Foo", "Bar" );
		
		proxy.setOwner( "Foo" );
		
		owners  = ((IHandMeDownHistory) proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).containsExactly( "Foo", "Bar", "Foo" );
		
		proxy.setOwner( "Foo" );
		
		owners  = ((IHandMeDownHistory) proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).containsExactly( "Foo", "Bar", "Foo" );

		proxy.setOwner( "Jinx" );
		
		owners  = ((IHandMeDownHistory) proxy).getOwners();
		Truth.assertThat( owners ).isNotNull();
		Truth.assertThat( owners ).isInstanceOf( List.class );
		Truth.assertThat( owners ).containsExactly( "Foo", "Bar", "Foo", "Jinx" );
	}
}
