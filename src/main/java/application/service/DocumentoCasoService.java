package application.service;

import application.dao.DocumentoCasoDAO;
import application.model.DocumentoCaso;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DocumentoCasoService {
    private DocumentoCasoDAO documentoCasoDAO;

    public DocumentoCasoService(Connection conn) {
        this.documentoCasoDAO = new DocumentoCasoDAO(conn);
    }

    public void registrarDocumento(DocumentoCaso doc) throws SQLException {
        documentoCasoDAO.insertarDocumento(doc);
    }

    public List<DocumentoCaso> obtenerDocumentosPorCaso(int casoId) throws SQLException {
        return documentoCasoDAO.consultarDocumentosPorCaso(casoId);
    }
}
