package packagename.services;

import java.sql.Timestamp;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;
import packagename.entities.UserEntity;
import packagename.entities.UserSessionEntity;
import packagename.repositories.UserSessionsRepository;
import packagename.services.UsersService;


@Service
public class UserSessionsService
{
    public static String SESSION_COOKIE_NAME = "session";
    public static String SESSION_COOKIE_PATH = "/";

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private UserSessionsRepository userSessionsRepository;

    @Autowired
    private UsersService usersService;


    public void deleteUserSessionEntity(UserSessionEntity userSessionEntity)
    {
        this.userSessionsRepository.delete(userSessionEntity);
    }

    public UserSessionEntity getUserSessionEntity(String identifier)
    {
        UUID uuid;
        try
        {
            uuid = UUID.fromString(identifier);
        }
        catch(Exception ignored)
        {
            return null;
        }
        
        return this.getUserSessionEntity(uuid);
    }

    public UserSessionEntity getUserSessionEntity(UUID identifier)
    {
        return this.userSessionsRepository.findByIdentifier(identifier).orElse(null);
    }

    public UserSessionEntity getUserSessionEntityFromCookie()
    {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        if (attribs == null) 
        {
            // Unable get the http/request
            return null;
        }
        
        HttpServletRequest request = ((ServletRequestAttributes)attribs).getRequest();
        if(request == null)
        {
            // No request headers found
            return null;
        }
        
        Cookie[] cookies = request.getCookies();
        if((cookies == null) || (cookies.length == 0))
        {
            // No cookies found
            return null;
        }
        
        // Get the cookie
        Cookie sessionIdentifierCookie = WebUtils.getCookie(request, SESSION_COOKIE_NAME);
        
        // Is a valid cookie?
        if(
            (sessionIdentifierCookie == null) ||
            (sessionIdentifierCookie.getValue().isEmpty())
        )
        {
            // Empty cookie
            return null;
        }
        
        // Get session entity from cookie value
        UserSessionEntity userSessionEntity = this.getUserSessionEntity(sessionIdentifierCookie.getValue());

        if(userSessionEntity == null)
        {
            // Session is not found
            return null;
        }
        
        if(userSessionEntity.getExpireAt().getTime() <= System.currentTimeMillis())
        {
            // Delete the old session
            this.deleteUserSessionEntity(userSessionEntity);

            // Session is expired
            return null;
        }
        
        return userSessionEntity;
    }

    public UserSessionEntity newUserSessionEntityFromCredentials(String email, String password, Boolean remember)
    {
        if((email == null) || (password ==  null))
        {
            // Empty credentials
            return null;
        }
        
        UserEntity userEntity = this.usersService.getByEmail(email);
        if(userEntity == null)
        {
            // User not found
            return null;
        }
        
        // Password is match?
        if(!this.usersService.passwordIsMatch(userEntity, password))
        {
            // Password is not match
            return null;
        }
        
        if(!userEntity.getEnable())
        {
            // User is disabled
            return null;
        }

        Timestamp expirationTime;

        // Expiration of session
        if((remember != null) && remember)
        {
            expirationTime = new Timestamp(
                System.currentTimeMillis() + 
                (180L * 24L * 60L * 60L * 1000L) // 180 days
            );
        }
        else
        {
            expirationTime = new Timestamp(
                System.currentTimeMillis() + 
                (24L * 60L * 60L * 1000L) // 24 hours
            );
        }
        
        // Make new session
        UserSessionEntity userSessionEntity = new UserSessionEntity();
        userSessionEntity.setUserId(userEntity.getId());
        userSessionEntity.setLastAccess(new Timestamp(System.currentTimeMillis()));
        userSessionEntity.setExpireAt(expirationTime);

        this.userSessionsRepository.save(userSessionEntity);

        // Update last access
        this.usersService.updateLastAccess(userEntity);

        return userSessionEntity;
    }

    public void applyUserSessionCookie(UserSessionEntity userSessionEntity)
    {
        // Set session cookies
        Cookie cookieSession = new Cookie(
            SESSION_COOKIE_NAME,
            userSessionEntity.getIdentifier().toString()
        );

        // TODO: Secure flag? (detect current schema)
        cookieSession.setHttpOnly(true);
        cookieSession.setPath(SESSION_COOKIE_PATH);
        cookieSession.setMaxAge(
            (int)(
                (
                    userSessionEntity.getExpireAt().getTime() -
                    (new Timestamp(System.currentTimeMillis())).getTime()
                ) / 1000L
            )
        );
        this.response.addCookie(cookieSession);
    }
}
