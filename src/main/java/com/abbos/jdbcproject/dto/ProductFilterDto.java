package uz.abbos.jdbcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDto {
    private Integer limit;
    private Integer offset;
    private String pname;
    private String color;

}
