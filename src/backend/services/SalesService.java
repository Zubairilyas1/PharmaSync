package backend.services;

import backend.models.Sale;
import backend.repositories.SaleRepository;

import java.util.List;

public class SalesService {
    private SaleRepository saleRepository;

    public SalesService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public void recordSale(Sale sale) {
        saleRepository.add(sale);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}
