package packagename.repositories;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import packagename.entities.UserSessionEntity;


@Repository
public interface UserSessionsRepository extends CrudRepository<UserSessionEntity, Long>
{
    public Optional<UserSessionEntity> findById(Long id);
    public Optional<UserSessionEntity> findByIdentifier(UUID identifier);
    public List<UserSessionEntity>     findByUserId(Long userId);
}
