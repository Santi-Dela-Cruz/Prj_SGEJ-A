package application.service;

import application.dao.BitacoraCasoDAO;
import application.model.BitacoraCaso;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BitacoraCasoService {
    private BitacoraCasoDAO bitacoraCasoDAO;

    public BitacoraCasoService(Connection conn) {
        this.bitacoraCasoDAO = new BitacoraCasoDAO(conn);
    }

    public void registrarBitacora(BitacoraCaso bitacora) throws SQLException {
        bitacoraCasoDAO.insertarBitacora(bitacora);
    }

    public List<BitacoraCaso> consultarBitacorasPorCaso(int casoId) throws SQLException {
        return bitacoraCasoDAO.consultarBitacorasPorCaso(casoId);
    }

    // Otros métodos según necesidades
}
