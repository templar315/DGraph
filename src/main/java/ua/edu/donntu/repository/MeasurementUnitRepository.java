package ua.edu.donntu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.donntu.domain.MeasurementUnit;

import java.util.List;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {

    List<MeasurementUnit> findAllByNodeId(Long id);
    List<MeasurementUnit> findAllByMeasurementId(Long id);
}
