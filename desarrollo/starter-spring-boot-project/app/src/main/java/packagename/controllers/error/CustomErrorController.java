package packagename.controllers.errors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class CustomErrorController
{
    @RequestMapping(produces=MediaType.TEXT_HTML_VALUE)
    @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handler(HttpServletRequest request, NoHandlerFoundException e)
    {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status == null)
        {
            return "fragments/error/500";
        }

        Integer statusCode = Integer.valueOf(status.toString());
     
        if(statusCode == HttpStatus.NOT_FOUND.value())
        {
            return "fragments/error/404";
        }
        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
        {
            return "fragments/error/500";
        }
        else if(statusCode == HttpStatus.FORBIDDEN.value())
        {
            return "fragments/error/403";
        }
        else
        {
            return "fragments/error/500";
        }
    }
}
