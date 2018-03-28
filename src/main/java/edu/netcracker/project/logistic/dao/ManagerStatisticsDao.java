package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.Person;
import edu.netcracker.project.logistic.model.SearchFormOrderStatistic;
import edu.netcracker.project.logistic.model.SearchFormStatisticEmployee;
import edu.netcracker.project.logistic.model.StatisticTask;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ManagerStatisticsDao {

    List<StatisticTask> searchStatisticOrders(SearchFormOrderStatistic searchFormOrderStatistic);

    List<Person> searchStatisticForManager(SearchFormStatisticEmployee searchFormStatisticEmployee);

    Integer countOrdersBetweenDate(LocalDateTime from, LocalDateTime to);

    List<Person> employeesByCourierOrCall_Center();

    Integer countOrdersHandToHand(LocalDateTime from, LocalDateTime to);

    Integer countOrdersFromOffice(LocalDateTime from, LocalDateTime to);

    Integer countEmployees(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesAdmins(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesCouriers(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesCouriersDriving(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesCouriersWalking(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesManagers(LocalDateTime from, LocalDateTime to);

    Integer countEmployeesAgentCallCenter(LocalDateTime from, LocalDateTime to);

    Integer countOffices();

    Double avarageWeightDocument();

    Double avarageCapacityDocument();

    Double  avarageWeightPackage();

    Double avarageCapacityPackage();

    Double  avarageWeightCargo();

    Double avarageCapacityCargo();

    Integer countUsers(LocalDateTime from, LocalDateTime to);

    Integer countUsersNormal(LocalDateTime from, LocalDateTime to);

    Integer countUsersVip(LocalDateTime from, LocalDateTime to);

    Integer countUnregisteredContacts(LocalDateTime from, LocalDateTime to);

    Integer countOrders();
}
