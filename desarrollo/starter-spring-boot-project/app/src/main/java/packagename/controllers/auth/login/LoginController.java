package packagename.controllers.auth.login;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import packagename.annotations.NotAuthenticatedRequest;
import packagename.entities.UserSessionEntity;
import packagename.models.LoginViewModel;
import packagename.services.UserSessionsService;


@Controller
public class LoginController
{
    public static String MODEL_AUTH_ERROR_KEY       = "errors";
    public static String AUTH_ERROR_BAD_CREDENTIALS = "login.auth_error";

    @Autowired
    public UserSessionsService userSessionsService;

    @NotAuthenticatedRequest
    @GetMapping(value={"/auth/login"})
    public String httpGet(@ModelAttribute LoginViewModel loginViewModel, Model model)
    {
        // Empty errors by default
        List<String> errors = new ArrayList<String>();
        
        // Append error messages to template
        model.addAttribute(MODEL_AUTH_ERROR_KEY, errors);

        // Template to render
        return "fragments/auth/login";
    }
    
    @NotAuthenticatedRequest
    @PostMapping(value={"/auth/login"})
    public String httpPost(@Valid @ModelAttribute LoginViewModel loginViewModel, BindingResult bindingResult, Model model)
    {
        // Empty errors by default
        List<String> errors = new ArrayList<String>();
        
        // Have errors on entity data from http request
        if(bindingResult.hasErrors())
        {
            // Results
            model.addAttribute(MODEL_AUTH_ERROR_KEY, errors);
            return "fragments/auth/login";
        }

        // Authenticate
        UserSessionEntity userSessionEntity = this.userSessionsService.newUserSessionEntityFromCredentials(
            loginViewModel.getEmail(),
            loginViewModel.getPassword(),
            loginViewModel.getRemember()
        );
        
        // Bad login?
        if(userSessionEntity == null)
        {
            // Error message
            errors.add(AUTH_ERROR_BAD_CREDENTIALS);

            // Results
            model.addAttribute(MODEL_AUTH_ERROR_KEY, errors);
            return "fragments/auth/login";
        }

        // Save the session in cookie
        this.userSessionsService.applyUserSessionCookie(userSessionEntity);

        // User is authenticated, redirect to default homepage
        return "redirect:/";
    }
}
