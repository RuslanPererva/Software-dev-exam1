

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

class TelegraphTest {
	@Test
	void testNoStaticNonPrivateFields() {
		BiConsumer<Class<?>,Predicate<Field>> fieldsIn = (c,p) -> Arrays.stream (c.getDeclaredFields())
			      .filter (p::test)
			      .forEach(f->{
				    	  int mod = f.getModifiers();
				    	  Truth.assertWithMessage( String.format( "field '%s' can't be private", f.getName()) )
				    	       .that( Modifier.isPrivate( mod ))
				    	       .isTrue();
				    	  Truth.assertWithMessage( String.format( "field '%s' can't be static",  f.getName()) )
				    	       .that( Modifier.isStatic ( mod ))
				    	       .isFalse();
			      });
		fieldsIn.accept( Telegraph       .class, f->!f.isSynthetic());
		fieldsIn.accept( TelegraphAdapter.class, f->!f.isSynthetic());
	}
	@ParameterizedTest
	@MethodSource("dataTelegraph")
	void testTelegraph(Telegraph telegraph, Function<Telegraph,String> run, String expected) {
		String actual = run.apply( telegraph );
		Truth.assertThat( actual ).isEqualTo( expected );
	}
	static Stream<Arguments> dataTelegraph() {
		var telegraph = new Telegraph();
	    return Stream.of(
	    	Arguments.of( new Telegraph(), 
			          (Function<Telegraph,String>)t -> t.start()
			                                            .end(), 
			          "" ),
	    	Arguments.of( new Telegraph(), 
			          (Function<Telegraph,String>)t -> t.start()
			                                            .gap()
			                                            .dot(1)
			                                            .word()
			                                            .end(), 
			          " .   " ),
	    	Arguments.of( new Telegraph(), 
   			          (Function<Telegraph,String>)t -> t.start()
   			                                            .dot (3).gap()
   			                                            .dash(3).gap()
   			                                            .dot (3)
   			                                            .end(), 
   			          "... --- ..." ),
	    	Arguments.of( new Telegraph(), 
			          (Function<Telegraph,String>)t -> t.start()
			                                            .dot (4).gap()
			                                            .dot (1).gap()
			                                            .dot (1).dash(1).dot(2).gap()
			                                            .dot (1).dash(1).dot(2).gap()
			                                            .dash(3).word()
			                                            .dot (1).dash(2).gap()
			                                            .dash(3).gap()
			                                            .dot (1).dash(1).dot(1).gap()
			                                            .dot (1).dash(1).dot(2).gap()
			                                            .dash(1).dot (2)
			                                            .end(), 
	    			".... . .-.. .-.. ---   .-- --- .-. .-.. -.." ),
	    	Arguments.of( new Telegraph(), 
		              (Function<Telegraph,String>)t -> t.start()
		                                                .dash(2).gap()
		                                                .dash(3).gap()
		                                                .dot (1).dash(1).dot(1).gap()
		                                                .dot (3).gap()
		                                                .dot (1).word()
		                                                .dash(1).dot(1).dash(1).dot(1).gap()
		                                                .dash(3).gap()
		                                                .dash(1).dot(2).gap()
		                                                .dot (1)
		                                                .end(), 
	    			"-- --- .-. ... .   -.-. --- -.. ." ),
	    	Arguments.of( new Telegraph(), 
		              (Function<Telegraph,String>)t -> t.start()
		                                                .dash(3).gap()
		                                                .dash(1).dot (1).gap()
		                                                .dash(1).dot (1).dash(1).dot(1).gap()
		                                                .dot (1).word()
		                                                .dot (2).dash(1).gap()
		                                                .dot (1).dash(2).dot(1).gap()
		                                                .dash(3).gap()
		                                                .dash(1).dot (1).word()
		                                                .dot (1).dash(1).word()
		                                                .dash(1).gap()
		                                                .dot (2).gap()
		                                                .dash(2).gap()
		                                                .dot (1)
		                                                .end(), 
	    			"--- -. -.-. .   ..- .--. --- -.   .-   - .. -- ." ),
	    	Arguments.of( telegraph, 
		              (Function<Telegraph,String>)t -> t.start()
		                                                .dot (1).dash(1).gap()                // a
		                                                .dash(1).dot (3).gap()                // b
		                                                .dash(1).dot (1).dash(1).dot(1).gap() // c
		                                                .dash(1).dot (2).gap()                // d
		                                                .dot (1).gap()                        // e
		                                                .dot (2).dash(1).dot (1).gap()        // f
		                                                .dash(2).dot (1).gap()                // g
		                                                .dot (4)                              // h
		                                                .end(), 
	    			".- -... -.-. -.. . ..-. --. ...." ),
	    	Arguments.of( telegraph, 
		              (Function<Telegraph,String>)t -> t.start()
		                                                .dot (2).gap()                 // i
		                                                .dot (1).dash(3).gap()         // j
		                                                .dash(1).dot (1).dash(1).gap() // k
		                                                .dot (1).dash(1).dot (2).gap() // l
		                                                .dash(2).gap()                 // m
		                                                .dash(1).dot (1).gap()         // n
		                                                .dash(3).gap()                 // o
		                                                .dot (1).dash(2).dot (1)       // p
		                                                .end(),
	    			".. .--- -.- .-.. -- -. --- .--." ),
	    	Arguments.of( telegraph, 
		              (Function<Telegraph,String>)t -> t.start()
		                                                .dash(2).dot (1).dash(1).gap() // q
		                                                .dot (1).dash(1).dot (1).gap() // r
		                                                .dot (3).gap()                 // s
		                                                .dash(1).gap()                 // t
		                                                .dot (2).dash(1).gap()         // u
		                                                .dot (3).dash(1).gap()         // v
		                                                .dot (1).dash(2).gap()         // w
		                                                .dash(1).dot (2).dash(1).gap() // x
		                                                .dash(1).dot (1).dash(2).gap() // y
		                                                .dash(2).dot (2)               // z
		                                                .end(), 
	    			"--.- .-. ... - ..- ...- .-- -..- -.-- --.." )
	    	);
	}
	@ParameterizedTest
	@MethodSource("dataTelegraphAdapter")
	void testTelegraph(String message, String expected) {
		String actual = TelegraphAdapter.toMorse( new Telegraph(), message );
		Truth.assertThat( actual ).isEqualTo( expected );
	}
	static Stream<Arguments> dataTelegraphAdapter() {
	    return Stream.of(
		    	Arguments.of( "",
	    			          "" ),
		    	Arguments.of( "sos",
				              "... --- ..." ),
		    	Arguments.of( "hello world",
                              ".... . .-.. .-.. ---   .-- --- .-. .-.. -.." ),
		    	Arguments.of( "morse code",
		    			      "-- --- .-. ... .   -.-. --- -.. ." ),
		    	Arguments.of( "once upon a time",
				              "--- -. -.-. .   ..- .--. --- -.   .-   - .. -- ." ),
		    	Arguments.of( "AbCdEfGhIjKlMnOpQrStUvWxYz",
				              ".- -... -.-. -.. . ..-. --. .... .. .--- -.- .-.. -- -. --- .--. --.- .-. ... - ..- ...- .-- -..- -.-- --.." ),
		    	Arguments.of( "I am your father",
	    			          "..   .- --   -.-- --- ..- .-.   ..-. .- - .... . .-." ),
		    	Arguments.of( "Dr Teeth and The Electric Mayhem",
	    			          "-.. .-.   - . . - ....   .- -. -..   - .... .   . .-.. . -.-. - .-. .. -.-.   -- .- -.-- .... . --" ),
		    	Arguments.of( "Sesame Street",
	    			          "... . ... .- -- .   ... - .-. . . -" ));
	}
	@Test
	void testInvalidCharaterThrowsException() {
		var input = new String[] { "abra;cadabra", "dr. who?", "call me!", "YABBA-DABBA-DOO", "42" };
		var wrong = new char[]   { ';', '.', '!', '-', '4' };
		for (int i = 0; i < input.length; i++) {
			var in = input[ i ];
			var t  = assertThrows( 
					IllegalArgumentException.class,
					() -> TelegraphAdapter.toMorse( new Telegraph(), in )
					);
			Truth.assertThat( t.getMessage() ).isEqualTo( String.format( "wrong character '%s'", wrong[i] ));
		}
	}
	@Test
	void testAdapterCallsTelegraph1() {
		Telegraph telegraph = Mockito.spy( new Telegraph() );
		TelegraphAdapter.toMorse( telegraph, "a" );
		Mockito.verify( telegraph ).start();
		Mockito.verify( telegraph ).dot ( 1 );
		Mockito.verify( telegraph ).dash( 1 );
		Mockito.verify( telegraph ).end();
		Mockito.verifyNoMoreInteractions( telegraph );
	}
	@Test
	void testAdapterCallsTelegraph2() {
		Telegraph telegraph = Mockito.spy( new Telegraph() );
		TelegraphAdapter.toMorse( telegraph, "abba" );
		Mockito.verify( telegraph ).start();
		Mockito.verify( telegraph, Mockito.times( 2 )).dot ( 1 );
		Mockito.verify( telegraph, Mockito.times( 2 )).dot ( 3 );
		Mockito.verify( telegraph, Mockito.times( 4 )).dash( 1 );
		Mockito.verify( telegraph, Mockito.times( 3 )).gap();
		Mockito.verify( telegraph ).end();
		Mockito.verifyNoMoreInteractions( telegraph );
	}
	@Test
	void testAdapterCallsTelegraph3() {
		Telegraph telegraph = Mockito.spy( new Telegraph() );
		TelegraphAdapter.toMorse( telegraph, " A B B A " );
		Mockito.verify( telegraph ).start();
		Mockito.verify( telegraph, Mockito.times( 2 )).dot ( 1 );
		Mockito.verify( telegraph, Mockito.times( 2 )).dot ( 3 );
		Mockito.verify( telegraph, Mockito.times( 4 )).dash( 1 );
		Mockito.verify( telegraph, Mockito.times( 5 )).word();
		Mockito.verify( telegraph ).end();
		Mockito.verifyNoMoreInteractions( telegraph );
	}
}
