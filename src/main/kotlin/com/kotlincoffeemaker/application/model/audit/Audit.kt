import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class Audit {
    @Id
    @GeneratedValue
    val id: Long? = null
    @CreatedDate
    var created: LocalDateTime? = null
    @LastModifiedDate
    var lastModified: LocalDateTime? = null
}