package packagename.entities;

import java.sql.Timestamp;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import packagename.entities.UserRoleEntity;


@Entity
@Table(name="users")
public class UserEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name="identifier")
    private UUID identifier = UUID.randomUUID();

    @Column(nullable = true, name="activation_hash", length=40)
    private String activationHash;

    @Column(nullable = false, name="created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(nullable = false, name="enable")
    private Boolean enable = false;

    @Column(nullable = true, name="last_access")
    private Timestamp lastAccess;

    @Column(nullable = true, name="last_modified_at")
    private Timestamp lastModifiedAt;

    @Column(nullable = false, name="email", length=256)
    private String email;

    @Column(nullable = false, name="name", length=32)
    private String name;

    @Column(nullable = true, name="last_name", length=32)
    private String lastName;

    @Column(nullable = true, name="password_hash", length=40)
    private String passwordHash;
    
    @Column(nullable = true, name="password_salt", length=40)
    private String passwordSalt;

    @Column(nullable = true, name="created_by_user_id")
    private Long createdByUserId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinTable(
        name = "users_roles_relatinships",
        joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") },
        inverseJoinColumns = { @JoinColumn(name = "user_role_id", referencedColumnName = "id") }
    )
    private Set<UserRoleEntity> userRoles = new HashSet<>();


    public Long getId()
    {
        return this.id;
    }

    public UUID getIdentifier()
    {
        return this.identifier;
    }

    public String getActivationHash() 
    {
        return this.activationHash;
    }

    public void setActivationHash(String activationHash) 
    {
        this.activationHash = activationHash;
    }

    public Timestamp getCreatedAt() 
    {
        return this.createdAt;
    }

    public Boolean getEnable() 
    {
        return this.enable;
    }

    public void setEnable(Boolean enable) 
    {
        this.enable = enable;
    }

    public Timestamp getLastAccess() 
    {
        return this.lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess)
    {
        this.lastAccess = lastAccess;
    }

    public Timestamp getLastModifiedAt() 
    {
        return this.lastModifiedAt;
    }

    public void setLastModifiedAt(Timestamp lastModifiedAt) 
    {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getEmail() 
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName() 
    {
        return this.name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getLastName() 
    {
        return this.lastName;
    }

    public void setLastName(String lastName) 
    {
        this.lastName = lastName;
    }

    public String getPasswordHash() 
    {
        return this.passwordHash;
    }

    public String getPasswordSalt() 
    {
        return this.passwordSalt;
    }
    
    public void setPasswordHash(String passwordHash) 
    {
        this.passwordHash = passwordHash;
    }

    public void setPasswordSalt(String passwordSalt) 
    {
        this.passwordSalt = passwordSalt;
    }

    public Set<UserRoleEntity> getUserRoles() 
    {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoleEntity> userRoles)
    {
        this.userRoles = userRoles;
    }
}