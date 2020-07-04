package packagename.repositories;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import packagename.entities.UserRoleEntity;


@Repository
public interface UserRolesRepository  extends CrudRepository<UserRoleEntity, Long>
{
    public List<UserRoleEntity>     findAll();
    public Optional<UserRoleEntity> findById(Long id);
    public Optional<UserRoleEntity> findByIdentifier(UUID identifier);
    public Optional<UserRoleEntity> findByNamespace(String namespace);
}