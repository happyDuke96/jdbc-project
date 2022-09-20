package uz.abbos.jdbcproject.service;

import uz.abbos.jdbcproject.dao.ProductDao;
import uz.abbos.jdbcproject.dto.ProductFilterDto;
import uz.abbos.jdbcproject.entity.Product;

import java.util.List;

public class DaoRunner {
    public static void main(String[] args) {
//        extracted();
//        getProduct();
//        updateWithFindID();
//        findAll();
        findAllWithByFilterLimit();


    }

    private static void findAll() {
        var productFilter = new ProductFilterDto();
        var  all = ProductDao.getInstance().findAll();

        System.out.println(all);
    }

    private static void findAllWithByFilterLimit() {
        // hop endi azgina tushuncha limit offset nima qilib beradi
        // limit tabiiyki qancha qatordagi malumot kelishini belgilaymiz
        // offset bilan esa nechta qator o'tkazib yuborib turib keyin qolgandagi
        // malumotlardan limit da ko'rsatilgani bo'yicha berib yoboramiz
        // odatda bizada qatorlar soni yani malumotlar soni bilan bir hil bo'ladi
        // misol uchun offsent ni shu quyidagi ko'rinishda ham berishimiz mumkin
        // offset = id > 10
        var productFilter = new ProductFilterDto(2,0,"Jeans","G");
        var  all = ProductDao.getInstance().findAll(productFilter);

        System.out.println(all);
    }

    private static void updateWithFindID() {
        var productDao = ProductDao.getInstance();
        var maybeProduct = productDao.findById("P7");
        System.out.println(maybeProduct);


        // bu yerda agar findByid dan qaytgan yani shu id lik product bo'lsa uni update qivommiz
        // buni yana boshqacha usullar bilan qilsa bo'ladi o'ylab chiqish kere lekin hozircha
        // shu tayyori
        maybeProduct.ifPresent(product -> {
            product.setStore("London");
            product.setSize(50);
            productDao.update(product);
        });
    }

    private static void extracted() {
        var productDao = ProductDao.getInstance();
        var deleteResult = productDao.delete("P8");
        System.out.println(deleteResult);
    }

    private static void getProduct() {
        var productDao = ProductDao.getInstance();
        var product = new Product();
        product.setPid("P8");
        product.setPname("Kepka");
        product.setColor("Black");
        product.setSize(48);
        product.setStore("Toshkent");

        var savedProduct = productDao.save(product);
        System.out.println(savedProduct);
    }
}
