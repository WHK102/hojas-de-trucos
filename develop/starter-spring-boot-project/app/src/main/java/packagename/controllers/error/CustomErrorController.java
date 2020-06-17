package packagename.controllers.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


@Controller
public class CustomErrorController implements ErrorController
{
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request)
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
 
    @Override
    public String getErrorPath()
    {
        return "/error";
    }
}