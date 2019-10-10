package io.github.ms.cloudappwatch.repository;
import io.github.ms.cloudappwatch.domain.Server;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Server entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServerRepository extends JpaRepository<Server, Long>, JpaSpecificationExecutor<Server> {

    @Query("select server from Server server where server.admin.login = ?#{principal.username}")
    List<Server> findByAdminIsCurrentUser();

}
