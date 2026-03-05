package com.civicfix.tfg.model.entities.daos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.civicfix.tfg.model.entities.Survey;

public interface SurveyDao extends JpaRepository<Survey, Long> {

    List<Survey> findByType(Survey.SurveyType type);
}
