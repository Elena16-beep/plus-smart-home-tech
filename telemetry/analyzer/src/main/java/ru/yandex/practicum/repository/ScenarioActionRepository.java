package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.dal.model.Scenario;
import ru.yandex.practicum.dal.model.ScenarioAction;
import ru.yandex.practicum.dal.model.ScenarioActionId;

import java.util.List;

@Repository
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {

    void deleteByScenario(Scenario scenario);

    List<ScenarioAction> findByScenario(Scenario scenario);

    List<ScenarioAction> findAllByScenarioIdIn(List<Long> scenarioIds);

    void deleteAllByScenarioId(Long scenarioId);
}