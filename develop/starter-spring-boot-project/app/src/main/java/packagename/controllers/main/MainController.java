package packagename.controllers.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import packagename.services.UserSessionsService;
import packagename.entities.UserSessionEntity;


@Controller
public class MainController
{
    public static String MODEL_XSRF_KEY = "tokenXsrf";

    @Autowired
    UserSessionsService userSessionsService;

    @RequestMapping("/")
    public String main(Model model)
    {
        // Authenticate
        UserSessionEntity userSessionEntity = this.userSessionsService.getUserSessionEntityFromCookie();
        
        if(userSessionEntity != null)
        {
            model.addAttribute(MODEL_XSRF_KEY, userSessionEntity.getTokenXsrf());
        }

        return "fragments/main";
    }
}