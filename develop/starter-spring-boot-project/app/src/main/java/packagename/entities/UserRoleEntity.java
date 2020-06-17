package packagename.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name="user_roles")
public class UserRoleEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, name="identifier")
    private UUID identifier = UUID.randomUUID();

    @Column(nullable = false, name="namespace", length=45)
    private String namespace;
    

    public Long getId() 
    {
        return this.id;
    }

    public UUID getIdentifier()
    {
        return this.identifier;
    }

    public String getNamespace()
    {
        return this.namespace;
    }

    public void setNamespace(String namespace) 
    {
        this.namespace = namespace;
    }
}