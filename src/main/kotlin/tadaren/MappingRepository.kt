package tadaren

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MappingRepository: JpaRepository<Mapping, Long>