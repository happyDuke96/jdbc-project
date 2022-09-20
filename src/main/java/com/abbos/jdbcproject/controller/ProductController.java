package uz.abbos.jdbcproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.abbos.jdbcproject.model.Product;
import uz.abbos.jdbcproject.service.ProductService;

@RestController
@RequestMapping
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body(productService);
    }

    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id){
        return ResponseEntity.ok().body(productService.get(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product product){
        return ResponseEntity.ok().body(productService.create(product));
    }
    @PutMapping
    public ResponseEntity<?> update(@RequestParam("id") Integer id,Product product){
        return ResponseEntity.ok().body(productService.update(id,product));
    }
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") Integer id){
        return ResponseEntity.ok().body(productService.delete(id));
    }
}
