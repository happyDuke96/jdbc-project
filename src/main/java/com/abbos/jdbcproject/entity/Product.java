package uz.abbos.jdbcproject.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
    public Product(String pid) {
        this.pid = pid;
    }
    private String pid;
    private String pname;
    private String color;
    private Integer size;
    private String store;
}
