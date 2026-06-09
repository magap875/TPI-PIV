import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fecha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private EstadoFecha estado;

    @OneToMany(mappedBy = "fecha", cascade = CascadeType.ALL)
    private List<Partido> partidos = new ArrayList<>();
}
