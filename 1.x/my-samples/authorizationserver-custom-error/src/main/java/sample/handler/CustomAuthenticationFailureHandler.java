package sample.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {
		Map<String, Object> map = new HashMap<>();

		if (exception instanceof OAuth2AuthenticationException) {
			OAuth2AuthenticationException e = (OAuth2AuthenticationException) exception;
			OAuth2Error oauth2Error = e.getError();
			map.put("code", String.valueOf(oauth2Error.getErrorCode()));
			map.put("message", oauth2Error.getDescription());
		} else {
			String message = "此异常暂未分类，请联系管理员";
			map.put("code", "error");
			map.put("message", message);
		}

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		new ObjectMapper().writeValue(response.getOutputStream(), map);
	}

}
