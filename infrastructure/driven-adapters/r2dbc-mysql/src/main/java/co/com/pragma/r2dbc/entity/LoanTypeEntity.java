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
@Table("tipo_prestamo")
@Builder
public class LoanTypeEntity {
    @Id
    @Column("id_tipo_prestamo")
    private Integer loanTypeId;

    @Column("nombre")
    private String name;

    @Column("monto_minimo")
    private BigDecimal minValue;

    @Column("monto_maximo")
    private BigDecimal maxValue;

    @Column("tasa_interes")
    private BigDecimal interestRate;

    @Column("validacion_automatica")
    private Boolean autoValidation;
}
