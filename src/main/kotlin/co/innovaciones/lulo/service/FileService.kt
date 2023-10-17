package co.innovaciones.lulo.service

import co.innovaciones.lulo.model.FileDTO


interface FileService {

    fun findAll(): List<FileDTO>

    fun `get`(id: Long): FileDTO

    fun create(fileDTO: FileDTO): Long

    fun update(id: Long, fileDTO: FileDTO)

    fun delete(id: Long)

    fun getReferencedWarning(id: Long): String?

}
