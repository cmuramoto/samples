package com.nc.app.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nc.domain.base.FrontEndTranslatedError;
import com.nc.domain.frontend.api.v1.ApiConstraints;
import com.nc.domain.frontend.api.v1.ApiRound;
import com.nc.domain.frontend.api.v1.ApiUser;
import com.nc.domain.frontend.api.v1.FinishMatchRequest;
import com.nc.domain.frontend.api.v1.FinishMatchResponse;
import com.nc.domain.frontend.api.v1.FinishRoundRequest;
import com.nc.domain.frontend.api.v1.FinishRoundResponse;
import com.nc.domain.frontend.api.v1.GetOrStartGameRequest;
import com.nc.domain.frontend.api.v1.GetOrStartGameResponse;
import com.nc.domain.frontend.api.v1.TopPlayerRequest;
import com.nc.domain.frontend.api.v1.TopPlayerResponse;
import com.nc.domain.frontend.services.v1.MovieGameService;
import com.nc.domain.internal.DomainException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@RestController
@RequestMapping("/v1/game")
@CrossOrigin
@SecurityScheme(type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "Authorization", bearerFormat = "Bearer <jwt>")
public class GameController {

	@Autowired
	MovieGameService gameService;

	@Operation(summary = "Changes the game's constraints (maxRounds,maxErrors)")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Value updated successfully", //
					content = @Content(schema = @Schema(implementation = ApiConstraints.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "maxRounds < 1 || maxErrors < 1", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/constraints", method = RequestMethod.POST)
	public ResponseEntity<ApiConstraints> changeConstraints(@RequestBody ApiConstraints request) throws Exception {
		return ResponseEntity.ok(gameService.changeConstraints(request));
	}

	@Operation(summary = "Fetch current authenticated user information")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Authenticated user ranking information", //
					content = @Content(schema = @Schema(implementation = ApiUser.class))//
			), //
			@ApiResponse( //
					responseCode = "417", //
					description = "Unlikelly (e.g. user was deleted after authentication layer and before service layer)", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))//
			) //
	})
	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public ResponseEntity<ApiUser> findAuthenticatedUserInfo() throws Exception {
		return ResponseEntity.ok(gameService.me());
	}

	@Operation(summary = "Find current round that user is playing")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Finds the pair of movies for the active round of a match. Returns an empty object if user has no active match", //
					content = @Content(schema = @Schema(implementation = ApiRound.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "The movie associated with the current round was deleted", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/round/current", method = RequestMethod.GET)
	public ResponseEntity<ApiRound> findCurrentRound() throws Exception {
		return ResponseEntity.ok(gameService.current());
	}

	@Operation(summary = "Retrieves the current match in play or create a new one if user has no active match associated")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Finishes current round with user selection. Returns either new round if rounds played < 10 or the match summary if request was for the last round", //
					content = @Content(schema = @Schema(implementation = FinishMatchResponse.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "If category is not supported or there are not enough movies in the database to satisfy a match of 10 rounds", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/finish", method = RequestMethod.POST)
	public ResponseEntity<FinishMatchResponse> finishMatch(@RequestBody FinishMatchRequest request) throws Exception {
		return ResponseEntity.ok(gameService.finish(request));
	}

	@Operation(summary = "Retrieves the current match in play or create a new one if user has no active match associated")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Finishes current round with user selection. Returns either new round if rounds played < 10 or the match summary if request was for the last round", //
					content = @Content(schema = @Schema(implementation = FinishRoundResponse.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "If category is not supported or there are not enough movies in the database to satisfy a match of 10 rounds", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/round/finish", method = RequestMethod.POST)
	public ResponseEntity<FinishRoundResponse> finishRound(@RequestBody FinishRoundRequest request) throws Exception {
		return ResponseEntity.ok(gameService.finish(request));
	}

	@Operation(summary = "Fetches the game constraints (maxRounds,maxErrors)")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Value updated successfully", //
					content = @Content(schema = @Schema(implementation = ApiConstraints.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "maxRounds < 1 || maxErrors < 1", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/constraints", method = RequestMethod.GET)
	public ResponseEntity<ApiConstraints> getConstraints() throws Exception {
		return ResponseEntity.ok(gameService.getConstraints());
	}

	@ExceptionHandler({ DomainException.class })
	public ResponseEntity<Object> onException(DomainException de) {
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(de.getTranslation());
	}

	@Operation(summary = "Fetches players ordered by ranking")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Value updated successfully", //
					content = @Content(schema = @Schema(implementation = TopPlayerResponse.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "maxRounds < 1 || maxErrors < 1", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/top", method = RequestMethod.POST)
	public ResponseEntity<TopPlayerResponse> ranking(@RequestBody TopPlayerRequest request) throws Exception {
		return ResponseEntity.ok(gameService.top(request));
	}

	@Operation(summary = "Retrieves the current match in play or create a new one if user has no active match associated")
	@ApiResponses({ //
			@ApiResponse( //
					responseCode = "200", //
					description = "Gets active match in play by the current user or creates a new one if the user has no active matches", //
					content = @Content(schema = @Schema(implementation = GetOrStartGameResponse.class))), //
			@ApiResponse( //
					responseCode = "417", //
					description = "If category is not supported or there are not enough movies in the database to satisfy a match of 10 rounds", //
					content = @Content(schema = @Schema(implementation = FrontEndTranslatedError.class))) //
	})
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public ResponseEntity<GetOrStartGameResponse> startOrRetrieve(@RequestBody GetOrStartGameRequest request) throws Exception {
		return ResponseEntity.ok(gameService.start(request));
	}

}
