package nl.abnamro.api.recipes.common;

import java.time.LocalDateTime;

public record ApiResponse(String message, Object object) {

	// try again


	public String getTimestamp() {
		return LocalDateTime.now().toString();
	}
}
