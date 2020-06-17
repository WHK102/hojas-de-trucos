package packagename.annotations;

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packagename.services.UserSessionsService;
import packagename.services.UsersService;
import packagename.entities.UserSessionEntity;


@Aspect
@Component
public class AuthenticatedRequestAspect
{
    @Autowired
    public UserSessionsService userSessionsService;

    @Autowired
    public UsersService usersService;
    
    
    // Handle controllers only with @AuthenticatedRequest
    @Around("@annotation(AuthenticatedRequest)")
    public Object requestIntercept(ProceedingJoinPoint joinPoint) throws Throwable
    {
        // Get annotation parameter
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthenticatedRequest authenticatedRequest = method.getAnnotation(AuthenticatedRequest.class);
        List<String> userGroupsNamespaces = Arrays.asList(authenticatedRequest.userGroupsNamespaces());

        // Load session by cookie request
        UserSessionEntity userSessionEntity = this.userSessionsService.getUserSessionEntityFromCookie();

        if(userSessionEntity == null)
        {
            // Authenticated user is not found
            return "redirect:/auth/login";
        }
        
        // Specific user group namespace is required?
        if(userGroupsNamespaces.size() == 0)
        {
            return joinPoint.proceed();
        }

        // User have all required roles?
        if(!this.usersService.hasAllRoles(userSessionEntity.getUserId(), userGroupsNamespaces))
        {
            return "fragments/error/403";
        }
        
        return joinPoint.proceed();
    }
}
