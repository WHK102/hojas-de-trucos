package packagename.entities;

import java.sql.Timestamp;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import packagename.helpers.StringHelper;


@Entity
@Table(name="user_sessions")
public class UserSessionEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, name="identifier")
    private UUID identifier = UUID.randomUUID();

    @Column(nullable = false, name="user_id")
    private Long userId;

    @Column(nullable = false, name="token_xsrf", length=40)
    private String tokenXsrf = StringHelper.getRandomSha1();

    @Column(nullable = false, name="created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(nullable = false, name="last_access")
    private Timestamp lastAccess;

    @Column(nullable = false, name="expire_at")
    private Timestamp expireAt;


    public Long getId() 
    {
        return this.id;
    }

    public UUID getIdentifier()
    {
        return this.identifier;
    }

    public Long getUserId()
    {
        return this.userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getTokenXsrf()
    {
        return this.tokenXsrf;
    }

    public void setTokenXsrf(String tokenXsrf)
    {
        this.tokenXsrf = tokenXsrf;
    }

    public Timestamp getCreatedAt() 
    {
        return this.createdAt;
    }

    public Timestamp getLastAccess() 
    {
        return this.lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess)
    {
        this.lastAccess = lastAccess;
    }

    public Timestamp getExpireAt() 
    {
        return this.expireAt;
    }

    public void setExpireAt(Timestamp expireAt)
    {
        this.expireAt = expireAt;
    }
}