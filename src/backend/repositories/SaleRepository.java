package backend.repositories;

import backend.models.Sale;

import java.util.List;

public interface SaleRepository {
    void add(Sale sale);
    List<Sale> findAll();
}