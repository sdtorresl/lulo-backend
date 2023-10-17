package co.innovaciones.lulo.repos

import co.innovaciones.lulo.domain.File
import org.springframework.data.jpa.repository.JpaRepository


interface FileRepository : JpaRepository<File, Long>
