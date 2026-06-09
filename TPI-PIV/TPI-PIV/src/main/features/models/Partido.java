import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHorarioInicio;

    @Enumerated(EnumType.STRING)
    private EstadoPartido estado;

    @ManyToOne
    @JoinColumn(name = "equipo_local_id", nullable = false)
    private Equipo equipoLocal;

    @ManyToOne
    @JoinColumn(name = "equipo_visitante_id", nullable = false)
    private Equipo equipoVisitante;

    private Integer golesLocal;

    private Integer golesVisitante;

    @Enumerated(EnumType.STRING)
    private ResultadoTendencia resultadoTendencia;

    @ManyToOne
    @JoinColumn(name = "fecha_id", nullable = false)
    private Fecha fecha;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pronostico> pronosticos = new ArrayList<>();
}