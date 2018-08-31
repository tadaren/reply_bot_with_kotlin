package tadaren

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MappingService{
    @Autowired
    lateinit var mappingRepository: MappingRepository

}