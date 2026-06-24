package com.example.features.users.repositories;

// import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// import com.example.features.rankings.dtos.RankingResponseDTO;
import com.example.features.users.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // @Query("""
    //         SELECT new com.example.TPI_PIV.features.rankings.dtos.RankingResponseDTO(
    //             u.id,
    //             u.nombre,
    //             u.puntosTotales,
    //             u.cantidadResultadosExactos
    //         )
    //         FROM Usuario u
    //         LEFT JOIN Pronostico p ON p.usuario = u
    //         GROUP BY u.id, u.nombre, u.puntosTotales, u.cantidadResultadosExactos
    //         ORDER BY
    //             u.puntosTotales DESC,
    //             u.cantidadResultadosExactos DESC,
    //             MIN(p.fechaCreacion) ASC
    //         """)
    // List<RankingResponseDTO> obtenerRankingGlobal();

    // @Query("""
    //         SELECT new com.example.TPI_PIV.features.rankings.dtos.RankingResponseDTO(
    //             u.id,
    //             u.nombre,
    //             u.puntosTotales,
    //             u.cantidadResultadosExactos
    //         )
    //         FROM MiembroGrupo mg
    //         JOIN mg.usuario u
    //         LEFT JOIN Pronostico p ON p.usuario = u
    //         WHERE mg.grupo.id = :grupoId
    //         GROUP BY u.id, u.nombre, u.puntosTotales, u.cantidadResultadosExactos
    //         ORDER BY
    //             u.puntosTotales DESC,
    //             u.cantidadResultadosExactos DESC,
    //             MIN(p.fechaCreacion) ASC
    //         """)
    // List<RankingResponseDTO> obtenerRankingGrupo(Long grupoId);

}