package packagename.repositories;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import packagename.entities.UserEntity;


@Repository
public interface UsersRepository extends PagingAndSortingRepository<UserEntity, Long>
{
    public Optional<UserEntity> findById(Long id);
    public Optional<UserEntity> findByIdentifier(UUID identifier);
    public Optional<UserEntity> findByEmail(String email);
}