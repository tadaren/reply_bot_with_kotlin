package tadaren

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "reply_map")
class Mapping{

    @Id
    @Column(name = "key", nullable = false)
    val key: String = ""

    @Column(name = "value", nullable = false)
    val value: String = ""



}