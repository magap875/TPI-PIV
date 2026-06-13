package com.example.features.equipos.services.interfaces;

import java.util.List;
import com.example.features.equipos.dtos.request.EquipoCreateDTO;
import com.example.features.equipos.dtos.response.EquipoResponseDTO;

public interface IEquipoService {

    EquipoResponseDTO crear(EquipoCreateDTO request);
    List<EquipoResponseDTO> listar();
    EquipoResponseDTO buscarPorId(Long id);
    void eliminar(Long id);

}
