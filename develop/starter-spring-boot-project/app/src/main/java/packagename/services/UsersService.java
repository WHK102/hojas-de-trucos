package packagename.services;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import packagename.repositories.UsersRepository;
import packagename.entities.UserEntity;
import packagename.entities.UserRoleEntity;
import packagename.helpers.StringHelper;


@Service
public class UsersService
{

    @Autowired
    private UsersRepository usersRepository;


    public UserEntity getById(Long id)
    {
        return this.usersRepository.findById(id).orElse(null);
    }

    public UserEntity getByIdentifier(String identifier)
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
        
        return this.getByIdentifier(uuid);
    }

    public UserEntity getByIdentifier(UUID identifier)
    {
        return this.usersRepository.findByIdentifier(identifier).orElse(null);
    }

    public UserEntity getByEmail(String email)
    {
        return this.usersRepository.findByEmail(email).orElse(null);
    }

    public void updateLastAccess(Long userId)
    {
        UserEntity userEntity = this.getById(userId);
        this.updateLastAccess(userEntity);
    }

    public void updateLastAccess(UserEntity userEntity)
    {
        if(userEntity == null)
        {
            // TODO: exception user empty?
            return;
        }

        userEntity.setLastAccess(
            new Timestamp(System.currentTimeMillis())
        );
        this.usersRepository.save(userEntity);
    }

    public Boolean passwordIsMatch(Long userId, String password)
    {
        UserEntity userEntity = this.getById(userId);
        return this.passwordIsMatch(userEntity, password);
    }

    public Boolean passwordIsMatch(UserEntity userEntity, String password)
    {
        if(userEntity == null)
        {
            // TODO: exception user empty?
            return false;
        }

        return userEntity.getPasswordHash().equals(
            StringHelper.SHA1(password + userEntity.getPasswordSalt())
        );
    }

    public void setPassword(Long userId, String password)
    {
        UserEntity userEntity = this.getById(userId);
        this.setPassword(userEntity, password);
    }

    public void setPassword(UserEntity userEntity, String password)
    {
        if(userEntity == null)
        {
            // TODO: exception user empty?
            return;
        }

        String salt = StringHelper.getRandomSha1();
        password = StringHelper.SHA1(password + salt);
        
        userEntity.setPasswordSalt(salt);
        userEntity.setPasswordHash(password);

        this.usersRepository.save(userEntity);
    }

    public Boolean hasRole(Long userId, String roleNamespace)
    {
        UserEntity userEntity = this.getById(userId);
        return this.hasRole(userEntity, roleNamespace);
    }

    public Boolean hasRole(UserEntity userEntity, String roleNamespace)
    {
        if(userEntity == null)
        {
            // User is not found
            return false;
        }

        for(UserRoleEntity userRoleEntity : userEntity.getUserRoles())
        {
            if(userRoleEntity.getNamespace().equals(roleNamespace))
            {
                return true;
            }
        }

        return false;
    }

    public Boolean hasOneRole(Long userId, List<String> roleNamespaces)
    {
        UserEntity userEntity = this.getById(userId);
        return this.hasOneRole(userEntity, roleNamespaces);
    }

    public Boolean hasOneRole(UserEntity userEntity, List<String> roleNamespaces)
    {
        if(userEntity == null)
        {
            // User is not found
            return false;
        }

        for(String roleNamespace : roleNamespaces)
        {
            for(UserRoleEntity userRoleEntity : userEntity.getUserRoles())
            {
                if(userRoleEntity.getNamespace().equals(roleNamespace))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Boolean hasAllRoles(Long userId, List<String> roleNamespaces)
    {
        UserEntity userEntity = this.getById(userId);
        return this.hasAllRoles(userEntity, roleNamespaces);
    }

    public Boolean hasAllRoles(UserEntity userEntity, List<String> roleNamespaces)
    {
        if(userEntity == null)
        {
            // User is not found
            return false;
        }

        for(String roleNamespace : roleNamespaces)
        {
            Boolean haveRole = false;
            for(UserRoleEntity userRoleEntity : userEntity.getUserRoles())
            {
                if(userRoleEntity.getNamespace().equals(roleNamespace))
                {
                    haveRole = true;
                }
            }
            
            if(!haveRole)
            {
                return false;
            }
        }

        return true;
    }
}