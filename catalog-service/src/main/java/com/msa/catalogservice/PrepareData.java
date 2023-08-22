package com.msa.catalogservice;

import com.msa.catalogservice.jpa.CatalogEntity;
import com.msa.catalogservice.jpa.CatalogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PrepareData {

    private final CatalogRepository catalogRepository;

    @PostConstruct
    @Transactional
    public void init() {
        CatalogEntity catalogEntity1 = new CatalogEntity();
        catalogEntity1.setProductId("CATALOG-001");
        catalogEntity1.setProductName("A");
        catalogEntity1.setStock(100);
        catalogEntity1.setUnitPrice(1500);
        catalogRepository.save(catalogEntity1);

        CatalogEntity catalogEntity2 = new CatalogEntity();
        catalogEntity2.setProductId("CATALOG-002");
        catalogEntity2.setProductName("B");
        catalogEntity2.setStock(200);
        catalogEntity2.setUnitPrice(2500);
        catalogRepository.save(catalogEntity2);

        CatalogEntity catalogEntity3 = new CatalogEntity();
        catalogEntity3.setProductId("CATALOG-003");
        catalogEntity3.setProductName("C");
        catalogEntity3.setStock(300);
        catalogEntity3.setUnitPrice(3500);
        catalogRepository.save(catalogEntity3);
    }
}
