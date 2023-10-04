import static org.mockito.ArgumentMatchers.contains;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// used https://www.baeldung.com/java-stream-operations-on-strings || for some help

interface StarWarsSurvey {
	ToLongFunction<Stream<String>> getTotalRespondents = SWList -> SWList.skip(2).count(); // using skip (2) to skip the
																							// first two rows, due to
																							// them not having important
																							// info
	Function<Stream<String>, Map<String, Long>> countRespondentsByGender = SWList -> SWList.skip(2)
			.map(line -> line.split(",")).collect(Collectors.groupingBy(fields -> fields[29], Collectors.counting()));
	static Function<Stream<String>, Map<String, Long>> countRespondentsWhoHaveWatchedAnyOfTheSixMoviesByAgeRange = SWList -> SWList
			.skip(2).map(line -> line.split(",")).filter(fields -> fields[1].contains("Yes"))
			.collect(Collectors.groupingBy(fields -> {
				return fields[30].trim();
			}, Collectors.counting()));
	static Function<Stream<String>, Map<String, Long>> countVotesForMostLikedMovieAmongThoseWhoWatchedAnyOfTheSixMovies = SWList -> SWList
			.skip(2).map(line -> line.split(",")).filter(fields -> fields[1].contains("Yes")).flatMap(fields -> {
				Map<String, Long> movieRankings = new HashMap<>();

				if (fields[11] != "") {
					movieRankings.put("Star Wars: Episode IV  A New Hope", (long) Integer.parseInt(fields[11]));
				}
				if (fields[13] != "") {
					movieRankings.put("Star Wars: Episode VI  Return of the Jedi", (long) Integer.parseInt(fields[13]));
				}

				if (fields[9] != "") {
					movieRankings.put("Star Wars: Episode II  Attack of the Clones",
							(long) Integer.parseInt(fields[9]));
				}
				if (fields[12] != "") {
					movieRankings.put("Star Wars: Episode V  The Empire Strikes Back",
							(long) Integer.parseInt(fields[12]));
				}
				if (fields[8] != "") {
					movieRankings.put("Star Wars: Episode I  The Phantom Menace", (long) Integer.parseInt(fields[8]));
				}
				if (fields[10] != "") {
					movieRankings.put("Star Wars: Episode III  Revenge of the Sith",
							(long) Integer.parseInt(fields[10]));
				}

				return movieRankings.entrySet().stream();
			}).filter(entry -> entry.getValue() == 1)
			.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.counting()));

	static Function<Stream<String>, Optional<String>> getMostLikedMovie =
			// calling the above function since to find the most liked I have to essentially
			// do that function anyways
			movieList -> countVotesForMostLikedMovieAmongThoseWhoWatchedAnyOfTheSixMovies.apply(movieList).entrySet()
					.stream().max(Comparator.comparing(Map.Entry::getValue)).map(Map.Entry::getKey);

	static BiFunction<Stream<String>, String, Map<String, Long>> countVeryFavorableCharacterVotesByGender = (SWList,
			character) -> SWList.skip(2) // Skip the header rows
					.map(line -> line.split(",")).filter(fields -> fields[1].contains("Yes")).filter(fields -> {
						int columnIndex = -1;

						if (character == "Han Solo") {
							columnIndex = 15;

						} else if (character == "Luke Skywalker") {
							columnIndex = 16;
						} else if (character == "Anakin Skywalker") {
							columnIndex = 17;
						} else if (character == "Obi Wan Kenobi") {
							columnIndex = 18;
						} else if (character == "Emperor Palpatine") {
							columnIndex = 19;
						} else if (character == "Darth Vader") {
							columnIndex = 20;
						} else if (character == "Lando Calrissian") {
							columnIndex = 21;
						} else if (character == "Boba Fett") {
							columnIndex = 22;
						} else if (character == "C-3P0") {
							columnIndex = 23;
						} else if (character == "R2 D2") {
							columnIndex = 24;
						} else if (character == "Jar Jar Binks") {
							columnIndex = 25;
						} else if (character == "Padme Amidala") {
							columnIndex = 26;
						} else if (character == "Yoda") {
							columnIndex = 27;

						}
						return columnIndex != -1 && columnIndex < fields.length
								&& fields[columnIndex].equals("Very favorably");
					}).map(fields -> fields[29])
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

}
