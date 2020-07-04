package packagename.annotations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packagename.entities.UserSessionEntity;
import packagename.services.UserSessionsService;


@Aspect
@Component
public class NotAuthenticatedRequestAspect
{
    @Autowired
    public UserSessionsService userSessionsService;

    
    // Handle controllers only with @AuthenticatedRequest
    @Around("@annotation(NotAuthenticatedRequest)")
    public Object requestIntercept(ProceedingJoinPoint joinPoint) throws Throwable
    {
        // Load session by cookie request
        UserSessionEntity userSessionEntity = this.userSessionsService.getUserSessionEntityFromCookie();
        
        if(userSessionEntity != null)
        {
            // Already authenticated
            return "fragments/error/403";
        }

        return joinPoint.proceed();
    }
}
