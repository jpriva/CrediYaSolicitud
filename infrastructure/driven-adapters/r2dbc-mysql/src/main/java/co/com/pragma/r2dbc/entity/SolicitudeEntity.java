package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table("solicitud")
@Builder
public class SolicitudeEntity {
    @Id
    @Column("id_solicitud")
    private Integer solicitudeId;

    @Column("monto")
    private BigDecimal value;

    @Column("plazo")
    private Integer deadline;

    @Column("email")
    private String email;

    @Column("id_estado")
    private Integer stateId;

    @Column("id_tipo_prestamo")
    private Integer loanTypeId;
}
