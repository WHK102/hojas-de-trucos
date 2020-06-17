package packagename.controllers.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import packagename.annotations.AuthenticatedRequest;
import packagename.entities.UserSessionEntity;
import packagename.services.UserSessionsService;


@Controller
public class LogoutController
{
    public static String MODEL_XSRF_KEY = "tokenXsrf";

    @Autowired
    UserSessionsService userSessionsService;
    

    @AuthenticatedRequest
    @RequestMapping(value={"/auth/logout/{token_xsrf}"})
    public String httpGet(
        @PathVariable("token_xsrf") String tokenXsrf,
        Model model,
        HttpServletRequest request,
        HttpServletResponse response
    )
    {
        // Authenticate
        UserSessionEntity userSessionEntity = this.userSessionsService.getUserSessionEntityFromCookie();

        if(
            (userSessionEntity == null) ||
            (!userSessionEntity.getTokenXsrf().equals(tokenXsrf))
        )
        {
            return "fragments/error/403";
        }

        // Delete current session
        this.userSessionsService.deleteUserSessionEntity(userSessionEntity);
        
        // XSRF token
        model.addAttribute(MODEL_XSRF_KEY, userSessionEntity.getTokenXsrf());

        // Default guest homepage
        return "redirect:/";
    }
}
