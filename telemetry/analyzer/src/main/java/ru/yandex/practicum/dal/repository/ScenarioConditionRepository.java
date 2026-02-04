//package ru.yandex.practicum.dal.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import ru.yandex.practicum.dal.model.Scenario;
//import ru.yandex.practicum.dal.model.ScenarioCondition;
//import ru.yandex.practicum.dal.model.ScenarioConditionId;
//import java.util.List;
//
//@Repository
//public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {
//
//    void deleteByScenario(Scenario scenario);
//
//    List<ScenarioCondition> findByScenario(Scenario scenario);
//
//    List<ScenarioCondition> findAllByScenarioIdIn(List<Long> scenarioIds);
//
//    void deleteAllByScenarioId(Long scenarioId);
//}