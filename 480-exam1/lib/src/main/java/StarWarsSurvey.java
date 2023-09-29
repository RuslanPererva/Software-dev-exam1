import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

interface StarWarsSurvey {
	ToLongFunction<Stream<String>>                     getTotalRespondents = null;
	Function<Stream<String>,Map<String,Long>>          countRespondentsByGender = null;
	Function<Stream<String>,Map<String,Long>>          countRespondentsWhoHaveWatchedAnyOfTheSixMoviesByAgeRange = null;
	Function<Stream<String>,Map<String,Long>>          countVotesForMostLikedMovieAmongThoseWhoWatchedAnyOfTheSixMovies = null;
	Function<Stream<String>,Optional<String>>          getMostLikedMovie = null;
	BiFunction<Stream<String>,String,Map<String,Long>> countVeryFavorableCharacterVotesByGender = null;
}
