package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("estados")
@Builder
public class StateEntity {
    @Id
    @Column("id_estado")
    private Integer stateId;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
