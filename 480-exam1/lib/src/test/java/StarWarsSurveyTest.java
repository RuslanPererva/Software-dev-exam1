

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StarWarsSurveyTest {
	private static final String CSV_DATA_SET = "starwars-survey.csv";
	private static final String FEMALE       = "Female";
	private static final String MALE         = "Male";
	private static final String OTHER        = "Other";

	private static Path getPath(String filename) throws URISyntaxException {
		URL             url = StarWarsSurveyTest.class.getResource(filename);
		return Path.of( url.toURI() );
	}
	private static final long TOTAL_RESPONDENTS = 1066L;
	@Test
	void testTotalRespondents() throws IOException, URISyntaxException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			ToLongFunction<Stream<String>> function = StarWarsSurvey.getTotalRespondents;
			assertThat                   ( function ).isNotNull();
			var         expected = TOTAL_RESPONDENTS;
			var         actual   = function.applyAsLong( stream );
			assertThat( actual ).isNotNull();
			assertThat( actual ).isEqualTo( expected );
		} 
	}
	@Test
	void testCountRespondentsByGender() throws IOException, URISyntaxException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			Function<Stream<String>,Map<String,Long>> function = StarWarsSurvey.countRespondentsByGender;
			assertThat                              ( function ).isNotNull();
			var         expected = Map.of( FEMALE, 549L, 
					                       MALE,   497L, 
					                       OTHER,   20L );
			var         actual   = function.apply( stream );
			assertThat( actual ).isNotNull();
			assertThat( actual ).containsExactlyEntriesIn( expected );
		} 
	}
	@Test
	void testCountRespondentsWhoHaveWatchedAllSixMoviesByAgeRange() throws IOException, URISyntaxException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			Function<Stream<String>,Map<String,Long>> function = StarWarsSurvey.countRespondentsWhoHaveWatchedAnyOfTheSixMoviesByAgeRange;
			assertThat                              ( function ).isNotNull();
			var         expected = Map.of( "18-29", 186L,
					                       "30-44", 213L,
					                       "45-60", 245L,
					                       "> 60",  196L );
			var         actual   = function.apply( stream );
			assertThat( actual ).isNotNull();
			assertThat( actual ).containsExactlyEntriesIn( expected );
		} 
	}
	@Test
	void testGetVotesForMostLikedMovie() throws IOException, URISyntaxException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			Function<Stream<String>,Map<String,Long>> function = StarWarsSurvey.countVotesForMostLikedMovieAmongThoseWhoWatchedAnyOfTheSixMovies;
			assertThat                              ( function ).isNotNull();
			var         expected = Map.of(
					"Star Wars: Episode I  The Phantom Menace",     129L,
					"Star Wars: Episode II  Attack of the Clones",   32L,
					"Star Wars: Episode III  Revenge of the Sith",   36L,
					"Star Wars: Episode IV  A New Hope",            204L,
					"Star Wars: Episode V  The Empire Strikes Back",289L,
					"Star Wars: Episode VI  Return of the Jedi",    146L
			);
			var         actual   = function.apply( stream );
			assertThat( actual ).isNotNull();
			assertThat( actual ).containsExactlyEntriesIn( expected );
		} 
	}
	@Test
	void testGetMostLikedMovie() throws IOException, URISyntaxException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			Function<Stream<String>,Optional<String>> function = StarWarsSurvey.getMostLikedMovie;
			assertThat                              ( function ).isNotNull();
			var         expected = Optional.of("Star Wars: Episode V  The Empire Strikes Back");
			var         actual   = function.apply( stream );
			assertThat( actual ).isNotNull();
			assertThat( actual ).isEqualTo( expected );
		} 
	}
	@ParameterizedTest
	@MethodSource("dataCountVeryFavorableCharacterVotesByGender")
	void testCountVeryFavorableCharacterVotesByGender(String character, Map<String,Long> expected) throws URISyntaxException, IOException {
		Path csv = getPath(CSV_DATA_SET);
		try (Stream<String> stream = Files.lines( csv, StandardCharsets.UTF_8 )) {
			BiFunction<Stream<String>,String,Map<String,Long>> function = StarWarsSurvey.countVeryFavorableCharacterVotesByGender;
			assertThat                                       ( function ).isNotNull();
			
			var         actual = function.apply( stream, character );
			assertThat( actual ).isNotNull();
			assertThat( actual ).containsExactlyEntriesIn( expected );
		}
	}
	static Stream<Arguments> dataCountVeryFavorableCharacterVotesByGender() {
	    return 
	    Stream.of(
	    		Arguments.of( 
	    				"Han Solo", 
	    				Map.of( FEMALE, 289L, MALE, 311L, OTHER, 10L ) ),
	    		Arguments.of( 
	    				"Obi Wan Kenobi", 
	    				Map.of( FEMALE, 290L, MALE, 292L, OTHER,  9L ) ),
	    		Arguments.of( 
	    				"Princess Leia Organa", 
	    				Map.of( FEMALE, 272L, MALE, 266L, OTHER,  9L ) ),
	    		Arguments.of( 
	    				"Padme Amidala", 
	    				Map.of( FEMALE,  84L, MALE,  79L, OTHER,  5L ) ),
	    		Arguments.of( 
	    				"Yoda", 
	    				Map.of( FEMALE, 310L, MALE, 286L, OTHER,  9L ) ),
	    		Arguments.of( 
	    				"Luke Skywalker", 
	    				Map.of( FEMALE, 268L, MALE, 277L, OTHER,  7L ) ),
	    		Arguments.of( 
	    				"Darth Vader", 
	    				Map.of( FEMALE, 121L, MALE, 184L, OTHER,  5L ) ),
	    		Arguments.of( 
	    				"C-3P0", 
	    				Map.of( FEMALE, 263L, MALE, 202L, OTHER,  9L ) ),
	    		Arguments.of( 
	    				"R2 D2", 
	    				Map.of( FEMALE, 306L, MALE, 248L, OTHER,  8L ) ),
	    		Arguments.of( 
	    				"Lando Calrissian", 
	    				Map.of( FEMALE,  65L, MALE,  73L, OTHER,  4L ) ),
	    		Arguments.of( 
	    				"Boba Fett", 
	    				Map.of( FEMALE,  43L, MALE,  91L, OTHER,  4L ) ),
	    		Arguments.of( 
	    				"Jar Jar Binks", 
	    				Map.of( FEMALE,  62L, MALE,  47L, OTHER,  3L ) ),
	    		Arguments.of( 
	    				"Emperor Palpatine", 
	    				Map.of( FEMALE,  36L, MALE,  72L, OTHER,  2L ) ),
	    		Arguments.of( 
	    				"Anakin Skywalker", 
	    				Map.of( FEMALE, 133L, MALE, 105L, OTHER,  7L ) ),
	    		Arguments.of( 
	    				"SpongeBob", 
	    				Map.of() )
	    		);
	}
}
